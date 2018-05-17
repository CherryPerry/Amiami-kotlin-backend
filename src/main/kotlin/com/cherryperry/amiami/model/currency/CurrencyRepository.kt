package com.cherryperry.amiami.model.currency

import com.cherryperry.amiami.util.readProperty
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Repository
import retrofit2.Retrofit
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@Repository
open class CurrencyRepository {

    companion object {
        private const val CURRENCY_JPY = "JPY"
        private const val CURRENCY_EUR = "EUR"
        private const val PROPERTIES_FILE = "currency.properties"
        private const val PROPERTIES_KEY = "key"
        private val INTERNAL_ERROR_RESPONSE = CurrencyResponse(success = false, error = CurrencyErrorResponse(0, "Internal error"))
    }

    private val log = LogManager.getLogger(CurrencyRepository::class.java)
    private val accessKey: String = readProperty(PROPERTIES_FILE) {
        getProperty(PROPERTIES_KEY, "no_key")
    }
    private val cachedResult = AtomicReference<CurrencyResponse?>()
    private val semaphore = Semaphore(1)
    private val api: CurrencyAPI

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(CurrencyAPI.BASE_URL)
            .addCallAdapterFactory(Java8CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .validateEagerly(true)
            .build()
        api = retrofit.create(CurrencyAPI::class.java)
    }

    open fun get(): CurrencyResponse {
        log.trace("get")
        try {
            semaphore.acquire()
            val cached = cachedResult.get()
            if (cached != null &&
                cached.success &&
                cached.timestamp != null &&
                System.currentTimeMillis() - cached.timestamp * 1000 < TimeUnit.DAYS.toMillis(1)) {
                log.info("cache is valid")
                semaphore.release()
                return cached
            }
            log.info("cache is invalid")
            val result = api.currencies(accessKey).get()
            if (result.success) {
                if (result.base != CURRENCY_EUR || result.rates == null || !result.rates.containsKey(CURRENCY_JPY)) {
                    log.error("invalid response $result")
                    return INTERNAL_ERROR_RESPONSE
                }
                log.info("valid response $result")
                val jpyRate = result.rates[CURRENCY_JPY]!!
                val newRates = result.rates.mapValues { it.value / jpyRate }
                val newResult = CurrencyResponse(success = true, timestamp = result.timestamp, date = result.date,
                    base = CURRENCY_JPY, rates = newRates)
                log.info("calculated response $newResult")
                this.cachedResult.set(newResult)
                return newResult
            } else {
                log.info("currency api returns error $result")
                return cached ?: result
            }
        } catch (exception: Exception) {
            log.error("currency api throws exception", exception)
            val cached = cachedResult.get()
            return cached ?: INTERNAL_ERROR_RESPONSE
        } finally {
            semaphore.release()
        }
    }
}