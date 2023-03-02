package com.enoch02.nekoscompose.data.model

import com.google.gson.annotations.SerializedName

data class NekoImage(
    @SerializedName("artist_href") val artistHref: String,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("source_url") val sourceUrl: String,
    val url: String
)

data class NekoResult(
    val results: List<NekoImage>
)

data class Category(
    val format: String,
    val min: String,
    val max: String
)
