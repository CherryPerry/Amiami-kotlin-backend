package com.cherryperry.amiami.model.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "items")
data class Item(
    @Id val url: String,
    val name: String,
    val image: String,
    val price: String,
    val discount: String,
    val time: Long
) {

    fun equalsNoTimestamp(other: Any?): Boolean {
        if (other == null || other !is Item) {
            return false
        }
        return other.url == url &&
            other.name == name &&
            other.image == image &&
            other.price == price &&
            other.discount == discount
    }
}
