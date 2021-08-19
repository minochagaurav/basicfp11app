package com.fp.devfantasypowerxi.app.api.response

data class ImageUploadResponse(
    val message: String = "",
    val status: Int = 0,
    val result: ArrayList<ImageUploadResponseItem> = ArrayList()
)

data class ImageUploadResponseItem(
    val image: String = "",
    val status: String = ""
)
