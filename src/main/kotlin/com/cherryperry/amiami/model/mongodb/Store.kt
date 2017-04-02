package com.cherryperry.amiami.model.mongodb

import com.cherryperry.amiami.model.update.UpdateItem
import com.mongodb.MongoClient
import org.apache.logging.log4j.LogManager
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia

object Store {
    private val log = LogManager.getLogger(Store::class.java)!!
    private val dataStore: Datastore

    init {
        val morphia = Morphia()
        morphia.map(Item::class.java)
        val mongoClient = MongoClient("localhost")
        dataStore = morphia.createDatastore(mongoClient, "v2")
        dataStore.ensureIndexes()
    }

    fun items(): Collection<Item> {
        log.trace("items")
        return dataStore.createQuery(Item::class.java).asList()
    }

    fun compareAndSave(item: UpdateItem, timestamp: Long): Boolean {
        log.trace("compareAndSave item = $item, timestamp = $timestamp")
        val currentItem = dataStore.createQuery(Item::class.java)
                .field("_id").equal(item.url)
                .get()
        val newItem = mapUpdateItemToDatabaseItem(item, timestamp)
        if (newItem != currentItem) {
            log.info("Changed, save updated")
            dataStore.save(newItem)
            return true
        } else {
            log.info("No changes")
            return false
        }
    }

    fun deleteOther(ids: Collection<String>) {
        log.trace("deleteOther size = ${ids.size}")
        val deleteQuery = dataStore.createQuery(Item::class.java).field("_id").notIn(ids)
        val result = dataStore.delete(deleteQuery)
        log.info("Deleted = ${result.n}")
    }

    private fun insertOrUpdate(item: UpdateItem, timestamp: Long) {
        dataStore.save(mapUpdateItemToDatabaseItem(item, timestamp))
    }

    private fun mapUpdateItemToDatabaseItem(item: UpdateItem, timestamp: Long = 0): Item {
        val dbItem = Item()
        dbItem._id = item.url
        dbItem.url = item.url
        dbItem.discount = item.discount
        dbItem.name = item.name
        dbItem.price = item.price
        dbItem.image = item.image
        dbItem.time = timestamp
        return dbItem
    }
}