package com.cherryperry.amiami.model.mongodb

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
open class ItemRepositoryImpl @Autowired constructor(
    private val itemMongoRepository: ItemMongoRepository
) : ItemRepository {

    private val log = LogManager.getLogger(ItemRepositoryImpl::class.java)

    override fun items(): Collection<Item> {
        log.trace("items")
        return itemMongoRepository.findAll()
    }

    override fun compareAndSave(item: Item): Boolean {
        log.trace("compareAndSave item = $item")
        val optional = itemMongoRepository.findById(item.url)
        if (optional.isPresent && optional.get().equalsNoTimestamp(item)) {
            log.info("Old one not changed, old one = ${optional.get()}")
            return false
        }
        log.info("Old one not found or not changed, old one = ${optional.orElse(null)}")
        itemMongoRepository.save(item)
        return true
    }

    override fun deleteOther(ids: Collection<String>) {
        log.trace("deleteOther size = ${ids.size}")
        val result = itemMongoRepository.deleteWhereIdNotInList(ids)
        log.info("Deleted = $result")
    }
}