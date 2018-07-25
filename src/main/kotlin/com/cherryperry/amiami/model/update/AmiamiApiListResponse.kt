package com.cherryperry.amiami.model.update

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AmiamiApiListResponse(
    @get:JsonProperty("RSuccess") val success: Boolean = false,
    val items: List<AmiamiListItem>? = null
)
