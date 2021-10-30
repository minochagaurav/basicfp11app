package com.fp.devfantasypowerxi.app.api.response

data class PlayerInfoResponse(
    val result: PlayerInfoResult = PlayerInfoResult(),
    val status: Int = 0,
    val message:String=""
)

data class PlayerInfoResult(
    val battingstyle: String = "",
    val bowlingstyle: String = "",
    val country: String = "",
    val dob: String = "",
    //val id: Int = 0,
    //val per: Int = 0,
    val playercredit: String = "",
    //val playerimage: String = "",
   // val playerkey: String = "",
    val playername: String = "",
    val playerpoints: Int = 0,
    //val playerrole: String = "",
    //val teams: String = "",
   // val total_points: String = "",
    val matches: ArrayList<PlayerInfoMatchesItem> = ArrayList(),
)

data class PlayerInfoMatchesItem(
    val matchdate: String = "",
  //  val matchname: String = "",
    val total_points: Double = 0.0,
    val selectper: String = "",
    val short_name: String = "",
  //  val playername: String = "",
)