package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.currency.CurrencyRepository
import com.cherryperry.amiami.model.currency.CurrencyResponse

@Suppress("MagicNumber")
class CurrencyControllerCurrencyRepositoryTestImpl : CurrencyRepository {

    override val lastModified: Long
        get() = 1527276677164

    override fun get() = CurrencyResponse(success = true, rates = mapOf(Pair("USD", 1.0), Pair("EUR", 1.0)))
}
