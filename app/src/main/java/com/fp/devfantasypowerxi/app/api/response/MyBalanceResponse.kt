package com.fp.devfantasypowerxi.app.api.response

data class MyBalanceResponse(
    val result: ArrayList<MyBalanceResult> = ArrayList(),
    val status: Int = 0,
    val message: String = ""
)

data class MyBalanceResult(
    val balance: String = "",
    val banner: ArrayList<Banner> = ArrayList(),
    val bonus: String = "",
    val expireamount: Int = 0,
    val total: String = "",
    val totalamount: String = "",
    val winning: String = ""
)

data class Banner(
    val date_created: String = "",
    val id: Int = 0,
    val image: String = "",
    val title: String = "",
    val type: String = "",
    val link: String = "",
    val url: String = ""
)