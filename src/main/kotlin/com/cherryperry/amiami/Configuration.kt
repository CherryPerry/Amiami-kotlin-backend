package com.cherryperry.amiami

import com.cherryperry.amiami.model.currency.CurrencyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class Configuration {

    @Bean
    open fun currencyRepository(): CurrencyRepository = CurrencyRepository()
}