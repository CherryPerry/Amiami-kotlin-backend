package com.cherryperry.amiami.model.currency

import com.cherryperry.amiami.model.lastmodified.LastModifiedSupported

interface CurrencyRepository : LastModifiedSupported {

    fun get(): CurrencyResponse
}
