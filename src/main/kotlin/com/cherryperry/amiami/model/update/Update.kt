package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.model.mongodb.Store
import com.cherryperry.amiami.model.push.Push
import com.cherryperry.amiami.util.getString
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

object Update {
    private val log = LogManager.getLogger(Update::class.java)!!
    private val pages = 30
    private val perPage = 100
    private val threadPool = Executors.newFixedThreadPool(8)
    private val baseUrl = "http://slist.amiami.com"
    private val okHttp: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor {
                it.proceed(it.request().newBuilder()
                        .header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
                        .build())
            }
            .build()

    private var syncInProgress = false

    fun sync() {
        log.trace("sync")
        if (syncInProgress) {
            log.warn("Sync already in progress!")
            return
        }
        syncInProgress = true
        try {
            doSync()
        } catch (e: Exception) {
            log.error(e)
        } finally {
            syncInProgress = false
        }
    }

    private fun doSync() {
        log.trace("doSync")
        val startTime = System.currentTimeMillis() / 1000
        log.info("Sync started at $startTime")

        // Загружаем html для каждой страницы
        val pageUrls = ArrayList<String>(2 * pages)
        for (page in 1..pages) {
            pageUrls.add("$baseUrl/top/search/list3?s_condition_flg=1&s_cate2=1298&s_sortkey=preowned&pagemax=$perPage&inctxt2=31&pagecnt=$page")
            pageUrls.add("$baseUrl/top/search/list3?s_condition_flg=1&s_cate2=459&s_sortkey=preowned&pagemax=$perPage&inctxt2=31&pagecnt=$page")
        }
        val pageCompletableFutures = pageUrls.map { CompletableFuture.supplyAsync(Supplier { okHttp.getString(it) }, threadPool) }

        // С каждой страницы вытаскиваем html и ищем элементы
        val itemCompletableFutures = ArrayList<CompletableFuture<UpdateItem>>()
        pageCompletableFutures.forEach {
            try {
                val html = it.get()
                val doc = Jsoup.parse(html, baseUrl)
                val nodes = doc.select(".product_box")
                nodes.forEach {
                    try {
                        val divProductImg = it.select("div.product_img")?.first()
                        val urlFull = divProductImg?.select("a")?.first()?.attr("href")
                        val url = urlFull?.substring(0, urlFull.indexOf('&'))
                        val image = divProductImg?.select("a > img")?.first()?.attr("src")
                        val discount = it.select("li.product_price > span.product_off")?.first()?.text()
                        val priceFull = it.select("li.product_price")?.first()?.text()
                        val price = if (discount != null) priceFull?.replace(discount, "") else null
                        if (url != null) {
                            itemCompletableFutures.add(CompletableFuture.supplyAsync(Supplier {
                                val itemHtml = okHttp.getString(url)
                                val title = Jsoup.parse(itemHtml, baseUrl).select("#title > h2")?.first()?.text()
                                        ?: throw NullPointerException("Title not found for item $urlFull")
                                UpdateItem(url, title, image ?: "", price ?: "", discount ?: "")
                            }, threadPool))
                        }
                    } catch (e: Exception) {
                        log.error("Failed to parse item from list page", e)
                    }
                }
            } catch (e: Exception) {
                log.error("Failed to download and parse list page", e)
            }
        }

        // Сохраняем элементы в бд, попутно запоманая те, которые участвовали в транзакции
        val ids = ArrayList<String>()
        var updatedItemsCount = 0
        itemCompletableFutures.forEach {
            try {
                val item = it.get()
                ids.add(item.url)
                if (Store.compareAndSave(item, startTime)) {
                    updatedItemsCount++
                }
            } catch (e: Exception) {
                log.error("Failed to download and parse detail page", e)
            }
        }

        // Удалим ненайденные элементы
        Store.deleteOther(ids)

        // Отправим оповещение об обновлении
        Push.sendPushWithUpdatedCount(updatedItemsCount)

        syncInProgress = false
    }
}