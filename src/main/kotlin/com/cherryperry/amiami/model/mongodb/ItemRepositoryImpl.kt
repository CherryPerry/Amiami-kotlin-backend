package com.cherryperry.amiami.model.mongodb

import com.cherryperry.amiami.model.lastmodified.LastModifiedValue
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Repository

@Repository
class ItemRepositoryImpl constructor(
    private val itemMongoRepository: ItemMongoRepository
) : ItemRepository {

    private val log = LogManager.getLogger(ItemRepositoryImpl::class.java)
    private val lastModifiedValue = LastModifiedValue()

    override val lastModified: Long
        get() = lastModifiedValue.value

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
        lastModifiedValue.update()
        return true
    }

    override fun deleteOther(ids: Collection<String>) {
        log.trace("deleteOther size = ${ids.size}")
        val result = itemMongoRepository.deleteWhereIdNotInList(ids)
        log.info("Deleted = $result")
        lastModifiedValue.update()
    }
}