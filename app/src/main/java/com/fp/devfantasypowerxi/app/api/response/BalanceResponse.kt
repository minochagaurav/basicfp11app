package com.fp.devfantasypowerxi.app.api.response

data class BalanceResponse(
    val result: BalanceResult = BalanceResult(),
    val status: Int = 0,
    val message:String=""
)

data class BalanceResult(
    val usertotalbalance: String = "",
    val total_match_play: String = "",
    val total_league_play: String = "",
    val total_contest_win: String = "",
    val total_winning: String = "",
    val balance: String = "",
    val winning: String = "",
    val bonus: String = "",
)