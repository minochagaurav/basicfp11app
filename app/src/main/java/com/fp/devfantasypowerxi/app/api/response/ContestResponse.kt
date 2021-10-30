package com.fp.devfantasypowerxi.app.api.response

data class ContestResponse(
    val result: ContestResult? = ContestResult(),
    val status: Int = 0,
    val message:String=""
)

data class ContestResult(
    val contestArrayList: ArrayList<League> = ArrayList(),
    val contest: ArrayList<League> = ArrayList(),
    val joined_leagues: Int = 0,
    val match_announcement: String = "",
//    val user_teams: Int = 0
)