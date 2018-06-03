package com.cherryperry.amiami.model.mongodb

import com.cherryperry.amiami.model.lastmodified.LastModifiedSupported

interface ItemRepository : LastModifiedSupported {

    fun items(): Collection<Item>

    fun compareAndSave(item: Item): Boolean

    fun deleteOther(ids: Collection<String>)
}
