package com.fp.devfantasypowerxi.app.api.response

data class PlayingHistoryResponse(
    val result: PlayingHistoryResult? = PlayingHistoryResult(),
    val status: Int = 0,
    val message:String
)

data class PlayingHistoryResult(
    val banner: ArrayList<Banner> = ArrayList(),
    val total_contest_win: Int = 0,
    val total_league_play: Int = 0,
    val total_match_play: Int = 0,
    val total_winning: String = ""
)
