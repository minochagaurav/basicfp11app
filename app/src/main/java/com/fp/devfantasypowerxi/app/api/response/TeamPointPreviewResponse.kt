package com.fp.devfantasypowerxi.app.api.response

import com.google.gson.annotations.SerializedName

data class TeamPointPreviewResponse(

    var result: TeamPointResponseItem = TeamPointResponseItem(),
    val message: String = "",
    val status: Int = 0

)

data class TeamPointResponseItem(
    var bowler: ArrayList<PlayerListResult> = ArrayList(),
    val keeper: ArrayList<PlayerListResult> = ArrayList(),
    val allrounder: ArrayList<PlayerListResult> = ArrayList(),
    val batsman: ArrayList<PlayerListResult> = ArrayList(),
    val teamname: String = "",
    val team1name: String = "",
    val team2name: String = "",
    val team2players: ArrayList<String> = ArrayList(),
    val team1players: ArrayList<String> = ArrayList(),
    val teamnumber: Double = 0.0,
    val points: Double = 0.0,
    val status: Int = 0,
    val Forward: ArrayList<PlayerListResult> = ArrayList(),
    val Goalkeeper: ArrayList<PlayerListResult> = ArrayList(),
    val Defender: ArrayList<PlayerListResult> = ArrayList(),
    val Midfielder: ArrayList<PlayerListResult> = ArrayList(),
    val pgList: ArrayList<PlayerListResult> = ArrayList(),
    val sgList: ArrayList<PlayerListResult> = ArrayList(),
    val smallForwardList: ArrayList<PlayerListResult> = ArrayList(),
    val powerForwardList: List<PlayerListResult> = ArrayList(),
    val centreList: List<PlayerListResult> = ArrayList()


)
