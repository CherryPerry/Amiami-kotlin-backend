package com.cherryperry.amiami.model.currency

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.concurrent.TimeUnit

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CurrencyResponse(
    val success: Boolean = false,
    val error: CurrencyErrorResponse? = null,
    val timestamp: Long? = null,
    val date: String? = null,
    val base: String? = null,
    val rates: Map<String, Double>? = null
) {

    /**
     * Is response still valid?
     * @param timeUnit Cache time unit.
     * @param value Cache time value.
     */
    fun isUpToDate(timeUnit: TimeUnit, value: Long) = success &&
        timestamp != null &&
        System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(timestamp) < timeUnit.toMillis(value)
}
