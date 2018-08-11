package com.cherryperry.amiami.model.update

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.math.min

@JsonIgnoreProperties(ignoreUnknown = true)
data class AmiamiListItem(
    @JsonProperty("gcode") val url: String,
    @JsonProperty("thumb_title") val name: String?,
    @JsonProperty("thumb_url") val image: String,
    @JsonProperty("min_price") val minPrice: Int,
    @JsonProperty("max_price") val maxPrice: Int
) {

    val price: Int
        get() {
            if (minPrice == 0) {
                return maxPrice
            }
            return min(minPrice, maxPrice)
        }

    val hasPrice: Boolean
        get() = minPrice > 0 || maxPrice > 0
}
