package com.cherryperry.amiami.model.mongodb

import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Indexed

@Entity("items")
class Item {
    @Id
    var _id: String? = null
    @Indexed
    var url: String? = null
    var name: String? = null
    var image: String? = null
    var price: String? = null
    var discount: String? = null
    @Indexed
    var time: Long? = null

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Item) {
            return false
        }
        return other._id.equals(_id)
                && other.url.equals(url)
                && other.name.equals(name)
                && other.image.equals(image)
                && other.price.equals(price)
                && other.discount.equals(discount)
    }
}