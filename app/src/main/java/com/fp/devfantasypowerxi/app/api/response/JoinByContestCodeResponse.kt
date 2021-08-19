package com.fp.devfantasypowerxi.app.api.response

data class JoinByContestCodeResponse(

    val message: String = "",
    val status: Int = 0,
    val result: ArrayList<JoinContestByCodeItem> = ArrayList()
)

data class JoinContestByCodeItem(
    val entryfee: Int = 0,
    val challengeid: Int = 0,
    val marathon: Int = 0,
    val message: String = ""

)
