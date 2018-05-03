package com.cherryperry.amiami.model.currency

data class CurrencyResponse(
        val success: Boolean,
        val error: CurrencyErrorResponse? = null,
        val timestamp: Long? = null,
        val date: String? = null,
        val base: String? = null,
        val rates: Map<String, Double>? = null
)