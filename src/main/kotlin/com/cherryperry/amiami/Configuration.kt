package com.cherryperry.amiami

import com.cherryperry.amiami.model.currency.CurrencyRepository
import com.cherryperry.amiami.model.mongodb.ItemMongoRepository
import com.cherryperry.amiami.model.mongodb.ItemRepository
import com.cherryperry.amiami.model.mongodb.ItemRepositoryImpl
import com.cherryperry.amiami.model.push.PushService
import com.cherryperry.amiami.model.push.PushServiceImpl
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
    open fun pushService(): PushService = PushServiceImpl()

    @Bean
    open fun itemRepository(@Autowired itemMongoRepository: ItemMongoRepository): ItemRepository =
        ItemRepositoryImpl(itemMongoRepository)

    @Bean
    open fun updateComponent(@Autowired itemRepository: ItemRepositoryImpl, @Autowired pushService: PushServiceImpl): UpdateComponent =
        UpdateComponent(itemRepository, pushService)
}