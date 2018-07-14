package com.cherryperry.amiami.model.mongodb

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ItemMongoRepository : MongoRepository<Item, String> {

    @Query(value = "{ url: { \$nin: ?0 } }", delete = true)
    fun deleteWhereIdNotInList(ids: Collection<String>): Long
}
