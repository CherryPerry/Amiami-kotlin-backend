package com.cherryperry.amiami.model.mongodb

interface ItemRepository {

    fun items(): Collection<Item>

    fun compareAndSave(item: Item): Boolean

    fun deleteOther(ids: Collection<String>)
}
