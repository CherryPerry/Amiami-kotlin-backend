package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository
import com.cherryperry.amiami.model.push.PushService
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Component
class UpdateComponent @Autowired constructor(
    private val itemRepository: ItemRepository,
    private val pushService: PushService
) {

    companion object {
        private const val PER_PAGE = 20
        private const val TIMEOUT_SECONDS = 60L
        private const val CATEGORY_FIGURE_BISHOUJO = 14
        private const val CATEGORY_FIGURE_CHARACTER = 15
        private const val CATEGORY_FIGURE_DOLL = 2
        private const val PARALLELISM_FACTOR = 8
    }

    private val api: AmiamiJsonApi
    private val log = LogManager.getLogger(UpdateComponent::class.java)

    private var syncInProgress = AtomicBoolean(false)

    init {
        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { log.info(it) })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val scheduler = Schedulers.from(Executors.newScheduledThreadPool(PARALLELISM_FACTOR))
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(AmiamiJsonApi.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
            .addConverterFactory(JacksonConverterFactory.create())
            .validateEagerly(true)
            .client(okHttpClient)
            .build()
        api = retrofit.create(AmiamiJsonApi::class.java)
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
        val allItems = arrayListOf<AmiamiListItem>()
        arrayOf(CATEGORY_FIGURE_BISHOUJO, CATEGORY_FIGURE_CHARACTER).forEach {
            var page: AmiamiApiListResponse
            var pageNumber = 1
            do {
                page = api.items(it, PER_PAGE, pageNumber++).blockingGet()
                val items = page.items
                items?.let { allItems += it }
            } while (page.success && items != null && items.isNotEmpty())
        }

        // Сохраняем элементы в бд, попутно запоманая те, которые участвовали в транзакции
        val ids = ArrayList<String>()
        var updatedItemsCount = 0
        allItems.asSequence().filterNotNull().forEach { item ->
            try {
                ids.add(item.url)
                val dbItem = Item("https://www.amiami.com/eng/detail/?gcode=${item.url}", item.name ?: "",
                    "https://img.amiami.com${item.image}", "${item.minPrice} JPY", "", startTime)
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
