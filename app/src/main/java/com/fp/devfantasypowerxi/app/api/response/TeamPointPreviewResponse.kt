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

    val teamnumber: Double = 0.0,


    val points: Double = 0.0,


    val status: Int = 0,

    val forwardList: ArrayList<PlayerListResult> = ArrayList(),


    val goalKeeperList: ArrayList<PlayerListResult> = ArrayList(),


    val midfielderList: ArrayList<PlayerListResult> = ArrayList(),

    val defenderList: ArrayList<PlayerListResult> = ArrayList(),


    val pgList: ArrayList<PlayerListResult> = ArrayList(),


    val sgList: ArrayList<PlayerListResult> = ArrayList(),


    val smallForwardList: ArrayList<PlayerListResult> = ArrayList(),


    val powerForwardList: List<PlayerListResult> = ArrayList(),


    val centreList: List<PlayerListResult> = ArrayList()


)
