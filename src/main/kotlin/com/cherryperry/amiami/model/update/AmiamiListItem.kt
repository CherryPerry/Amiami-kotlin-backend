package com.cherryperry.amiami.model.update

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AmiamiListItem(
    @JsonProperty("gcode") val url: String,
    @JsonProperty("thumb_title") val name: String?,
    @JsonProperty("thumb_url") val image: String,
    @JsonProperty("min_price") val minPrice: Int,
    @JsonProperty("max_price") val maxPrice: Int
)
