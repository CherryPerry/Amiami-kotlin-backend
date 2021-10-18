package com.cherryperry.amiami

import com.cherryperry.amiami.model.currency.CurrencyRepository
import com.cherryperry.amiami.model.currency.CurrencyRepositoryImpl
import com.cherryperry.amiami.model.mongodb.ItemMongoRepository
import com.cherryperry.amiami.model.mongodb.ItemRepository
import com.cherryperry.amiami.model.mongodb.ItemRepositoryImpl
import com.cherryperry.amiami.model.push.PushService
import com.cherryperry.amiami.model.push.PushServiceImpl
import com.cherryperry.amiami.model.update.AmiamiRestClient
import com.cherryperry.amiami.model.update.AmiamiRestClientImpl
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class Configuration {

    @Bean
    fun currencyRepository(): CurrencyRepository = CurrencyRepositoryImpl()

    @Bean
    fun pushService(): PushService = PushServiceImpl()

    @Bean
    fun amiamiRestClient(): AmiamiRestClient = AmiamiRestClientImpl()

    @Bean
    fun itemRepository(itemMongoRepository: ItemMongoRepository): ItemRepository =
        ItemRepositoryImpl(itemMongoRepository)
}
