package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository
import com.cherryperry.amiami.model.push.PushService
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Component
class UpdateComponent @Autowired constructor(
    private val itemRepository: ItemRepository,
    private val pushService: PushService
) {

    companion object {
        private const val PER_PAGE = 50
        private const val BASE_URL = "http://slist.amiami.com"
        private const val TIMEOUT_SECONDS = 60L
        private const val CATEGORY_FIGURE_BISHOUJO = 14
        private const val CATEGORY_FIGURE_CHARACTER = 15
        private const val PARALLELISM_FACTOR = 8
    }

    private val api: AmiamiHtmlAPI
    private val log = LogManager.getLogger(UpdateComponent::class.java)

    private var syncInProgress = AtomicBoolean(false)

    init {
        val scheduler = Schedulers.from(Executors.newScheduledThreadPool(PARALLELISM_FACTOR))
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
            .addConverterFactory(ScalarsConverterFactory.create())
            .validateEagerly(true)
            .client(okHttpClient)
            .build()
        api = retrofit.create(AmiamiHtmlAPI::class.java)
    }

    fun sync() {
        log.trace("sync")
        synchronized(syncInProgress) {
            val sync = syncInProgress.get()
            if (sync) {
                log.warn("Sync already in progress!")
                return
            }
            syncInProgress.set(true)
        }
        try {
            doSync()
        } catch (exception: Exception) {
            log.error(exception)
        } finally {
            syncInProgress.set(false)
        }
    }

    private fun doSync() {
        log.trace("doSync")
        val startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        log.info("Sync started at $startTime")

        // Загружаем а парсим информацию об элементах
        val crawler = ItemCrawler(api)
        val list = Flowable.fromArray(CATEGORY_FIGURE_BISHOUJO, CATEGORY_FIGURE_CHARACTER)
            .map { id ->
                "$BASE_URL/top/search/list3?s_cate_tag=$id&inc_txt2=31&s_condition_flg=1&s_sortkey=preowned" +
                    "&s_st_condition_flg=1&getcnt=0&pagemax=$PER_PAGE&pagecnt="
            }
            .flatMap { crawler.crawlLists(it) }
            .flatMap { crawler.crawlItem(it) }
            .toList()
            .blockingGet()

        // Сохраняем элементы в бд, попутно запоманая те, которые участвовали в транзакции
        val ids = ArrayList<String>()
        var updatedItemsCount = 0
        list.asSequence().filterNotNull().forEach { item ->
            try {
                ids.add(item.url)
                val dbItem = Item(item.url, item.name, item.image, item.price, item.discount, startTime)
                if (itemRepository.compareAndSave(dbItem)) {
                    updatedItemsCount++
                }
            } catch (exception: Exception) {
                log.error("Failed to download and parse detail page", exception)
            }
        }

        // Удалим ненайденные элементы
        itemRepository.deleteOther(ids)

        // Отправим оповещение об обновлении
        if (updatedItemsCount > 0) {
            pushService.sendPushWithUpdatedCount(updatedItemsCount)
        }
    }
}
