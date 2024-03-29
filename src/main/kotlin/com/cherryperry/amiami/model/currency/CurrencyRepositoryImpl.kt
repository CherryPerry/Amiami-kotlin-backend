package com.cherryperry.amiami.model.currency

import com.cherryperry.amiami.model.lastmodified.LastModifiedValue
import com.cherryperry.amiami.util.readProperty
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

@Repository
class CurrencyRepositoryImpl : CurrencyRepository {

    companion object {
        private const val CURRENCY_JPY = "JPY"
        private const val CURRENCY_EUR = "EUR"
        private const val PROPERTIES_FILE = "secure.properties"
        private const val PROPERTIES_KEY = "fixer.key"
        private val INTERNAL_ERROR_RESPONSE =
            CurrencyResponse(success = false, error = CurrencyErrorResponse(0, "Internal error"))
    }

    private val log = LogManager.getLogger(CurrencyRepositoryImpl::class.java)
    private val accessKey: String = readProperty(PROPERTIES_FILE) {
        if (!containsKey(PROPERTIES_KEY)) {
            throw IllegalStateException("No $PROPERTIES_KEY in $PROPERTIES_FILE found!")
        }
        getProperty(PROPERTIES_KEY)
    }
    private val cachedResult = AtomicReference<CurrencyResponse?>()
    private val lock = ReentrantLock()
    private val lastModifiedValue = LastModifiedValue()
    private val currencyRestClient = CurrencyRestClient()

    override val lastModified: Long
        get() = lastModifiedValue.value

    @Suppress("ReturnCount")
    override fun get(): CurrencyResponse {
        log.trace("get")
        try {
            lock.lock()
            val cached = cachedResult.get()
            log.info("cached $cached")
            if (cached != null && cached.isUpToDate(TimeUnit.DAYS, 1)) {
                log.info("cache is valid")
                return cached
            }
            log.info("cache is invalid")
            val result = currencyRestClient.currency(accessKey)
            if (result.success) {
                val jpyRate = result.rates?.get(CURRENCY_JPY)
                if (result.base != CURRENCY_EUR || jpyRate == null) {
                    log.error("invalid response $result")
                    return INTERNAL_ERROR_RESPONSE
                }
                log.info("valid response $result")
                val newRates = result.rates.mapValues { it.value / jpyRate }
                val newResult = CurrencyResponse(
                    success = true,
                    timestamp = result.timestamp,
                    date = result.date,
                    base = CURRENCY_JPY,
                    rates = newRates,
                )
                log.info("calculated response $newResult")
                cachedResult.set(newResult)
                lastModifiedValue.update()
                return newResult
            } else {
                log.info("currency api returns error $result")
                return cached ?: result
            }
        } catch (expected: Exception) {
            log.error("currency api throws exception", expected)
            val cached = cachedResult.get()
            return cached ?: INTERNAL_ERROR_RESPONSE
        } finally {
            lock.unlock()
        }
    }
}
