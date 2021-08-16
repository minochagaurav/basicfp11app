package com.fp.devfantasypowerxi.app.api.response

data class JoinContestResponse(

    val message: String = "",
    val status: Int = 0,
    val result: ArrayList<JoinItem> = ArrayList(),
)

data class JoinItem(
    val status: Boolean = false,
    val join_message:String =""
)
