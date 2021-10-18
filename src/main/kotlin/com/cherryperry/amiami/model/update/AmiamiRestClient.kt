package com.cherryperry.amiami.model.update

interface AmiamiRestClient {
    fun items(category: Int, perPage: Int, page: Int): AmiamiApiListResponse
}
