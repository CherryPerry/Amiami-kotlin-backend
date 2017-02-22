package com.cherryperry.amiami.model.currency

import com.cherryperry.amiami.controller.UpdateController
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.concurrent.TimeUnit

object CurrencyCache {
    private val log = LogManager.getLogger(UpdateController::class.java)!!
    private val defaultCacheString = """{"base":"JPY","date":"2016-12-16","rates":{"AUD":0.011541,"BGN":0.015852,"BRL":0.028433,"CAD":0.011315,"CHF":0.0087137,"CNY":0.058871,"CZK":0.21901,"DKK":0.060253,"GBP":0.0067993,"HKD":0.065677,"HRK":0.06108,"HUF":2.5324,"IDR":113.29,"ILS":0.032765,"INR":0.57365,"KRW":10.046,"MXN":0.17225,"MYR":0.037863,"NOK":0.07345,"NZD":0.01207,"PHP":0.42303,"PLN":0.035824,"RON":0.036606,"RUB":0.52117,"SEK":0.07934,"SGD":0.01221,"THB":0.30323,"TRY":0.029665,"USD":0.0084609,"ZAR":0.11823,"EUR":0.008105}}"""
    private val okHttp = OkHttpClient()

    @Volatile
    private var cache = CurrencyCacheItem(Date(0), defaultCacheString)

    @Synchronized
    fun get(): String {
        log.trace("get")
        val date = Date()
        if (date.time - cache.date.time > TimeUnit.DAYS.toMillis(1)) {
            log.info("Cache is invalid")
            try {
                val request = Request.Builder().url("http://api.fixer.io/latest?base=JPY").get().build()
                val response = okHttp.newCall(request).execute()
                if (response.isSuccessful) {
                    cache = CurrencyCacheItem(date, response.body().string())
                    log.info("Cache updated")
                } else {
                    log.error("Cache update failed")
                }
            } catch (e: Exception) {
                log.error("Cache update failed", e)
            }
        } else {
            log.info("Cache is valid")
        }
        return cache.string
    }
}