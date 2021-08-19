package com.fp.devfantasypowerxi.app.api.response

data class SwitchTeamResponse(
    val message: String = "",
    val status: Int = 0,
    val result: ArrayList<SwitchTeamItem> = ArrayList()

)

data class SwitchTeamItem(
    val msg: String = "",
    val status: Int = 0,
    val teamnumber: Int = 0,
)
