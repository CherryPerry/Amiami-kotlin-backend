package com.cherryperry.amiami

import com.cherryperry.amiami.model.currency.CurrencyRepository
import com.cherryperry.amiami.model.mongodb.ItemMongoRepository
import com.cherryperry.amiami.model.mongodb.ItemRepository
import com.cherryperry.amiami.model.push.PushService
import com.cherryperry.amiami.model.update.UpdateComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
open class Configuration {

    @Bean
    open fun currencyRepository(): CurrencyRepository = CurrencyRepository()

    @Bean
    open fun pushService(): PushService = PushService()

    @Bean
    open fun itemRepository(@Autowired itemMongoRepository: ItemMongoRepository): ItemRepository =
            ItemRepository(itemMongoRepository)

    @Bean
    open fun updateComponent(@Autowired itemRepository: ItemRepository, @Autowired pushService: PushService): UpdateComponent =
            UpdateComponent(itemRepository, pushService)
}