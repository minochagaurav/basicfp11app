package com.fp.devfantasypowerxi.app.dataModel

// Created on Gaurav Minocha
data class CategoriesItem(
    var contestSubText: String = "",
    var contestTypeImage: String = "",
    var leagues: ArrayList<String> = ArrayList(),
    var contest_image_url: String = "",
    var name: String = "",
    var id: Int?= null,
    var totalCategoryLeagues: Int? =null,
    var sortOrder: Int? =null,
    var status: Int? =null,
)
