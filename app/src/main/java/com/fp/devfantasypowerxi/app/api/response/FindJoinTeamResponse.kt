package com.fp.devfantasypowerxi.app.api.response

data class FindJoinTeamResponse(
    val message: String = "",
    val status: Int = 0,
    val result: ArrayList<TeamFindItem> = ArrayList()
)

data class TeamFindItem(
    val teamid: Int = 0,
    val teamnumber: Int = 0

)
