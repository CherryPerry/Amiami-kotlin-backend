package com.cherryperry.amiami.model.update

data class UpdateItem(
        val url: String,
        val name: String,
        val image: String,
        val price: String,
        val discount: String
)