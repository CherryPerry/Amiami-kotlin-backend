package com.cherryperry.amiami.model.mongodb

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories
class MongoConfiguration : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String = "v2"

}
