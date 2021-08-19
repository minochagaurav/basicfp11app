package com.fp.devfantasypowerxi.app.api.response

data class BalanceResponse(
    val result: BalanceResult = BalanceResult(),
    val status: Int = 0,
    val message:String=""
)

data class BalanceResult(
    val usertotalbalance: String = ""
)