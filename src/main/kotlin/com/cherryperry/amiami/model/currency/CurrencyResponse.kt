package com.cherryperry.amiami.model.currency

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CurrencyResponse(
    val success: Boolean = false,
    val error: CurrencyErrorResponse? = null,
    val timestamp: Long? = null,
    val date: String? = null,
    val base: String? = null,
    val rates: Map<String, Double>? = null
)