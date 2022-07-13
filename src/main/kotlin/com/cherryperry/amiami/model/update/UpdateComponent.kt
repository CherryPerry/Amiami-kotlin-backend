package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository
import com.cherryperry.amiami.model.push.PushService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Component
class UpdateComponent @Autowired constructor(
    private val itemRepository: ItemRepository,
    private val pushService: PushService,
    private val restClient: AmiamiRestClient,
    private val timeout: Pair<Long, TimeUnit> = DEFAULT_TIMEOUT_MINUTES to TimeUnit.MINUTES,
) {

    companion object {
        const val PER_PAGE = 20
        const val CATEGORY_FIGURE_BISHOUJO = 14
        const val CATEGORY_FIGURE_CHARACTER = 15
        const val CATEGORY_FIGURE_DOLL = 2
        const val DEFAULT_TIMEOUT_MINUTES = 5L
    }

    private val log = LogManager.getLogger(UpdateComponent::class.java)
    private var threadCounter = 0
    private var syncInProgress = false

    fun sync() {
        log.trace("sync")

        synchronized(this) {
            if (syncInProgress) {
                log.warn("Sync already in progress!")
                return
            }
            syncInProgress = true
        }

        val countDownLatch = CountDownLatch(1)

        val thread = thread(name = "SyncThread#${threadCounter++}") {
            try {
                doSync()
            } catch (expected: Exception) {
                log.error(expected)
            } finally {
                countDownLatch.countDown()
            }
        }

        // if sync is stuck – interrupt it
        val success = countDownLatch.await(timeout.first, timeout.second)
        if (!success) thread.interrupt()

        synchronized(this) { syncInProgress = false }

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
                page = restClient.items(it, PER_PAGE, pageNumber++)
                val items = page.items
                items?.let { allItems += it.filter { it.hasPrice } }
            } while (page.success && items != null && items.isNotEmpty())
        }

        // Сохраняем элементы в бд, попутно запоманая те, которые участвовали в транзакции
        val ids = ArrayList<String>()
        var updatedItemsCount = 0
        allItems.asSequence().filterNotNull().forEach { item ->
            try {
                val dbItem = Item(
                    "https://www.amiami.com/eng/detail/?gcode=${item.url}", item.name ?: "",
                    "https://img.amiami.com${item.image}", "${item.price} JPY", "", startTime
                )
                ids.add(dbItem.url)
                if (itemRepository.compareAndSave(dbItem)) {
                    updatedItemsCount++
                }
            } catch (expected: Exception) {
                log.error("Failed to download and parse detail page", expected)
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
