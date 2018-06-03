package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository

class DataControllerItemRepositoryTestImpl : ItemRepository {

    override val lastModified: Long
        get() = 1527276677164

    override fun items(): Collection<Item> = arrayListOf(
        Item("1", "2", "3", "4", "5", 6),
        Item("7", "8", "9", "1", "2", 3))

    override fun compareAndSave(item: Item): Boolean {
        throw NotImplementedError()
    }

    override fun deleteOther(ids: Collection<String>) {
        throw NotImplementedError()
    }
}
