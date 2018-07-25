package com.cherryperry.amiami.model.update

import com.fasterxml.jackson.annotation.JsonProperty

data class AmiamiApiItemResponse(
    @JsonProperty("RSuccess") val RSuccess: Boolean,
    val item: AmiamiListItem
)
