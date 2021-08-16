package com.fp.devfantasypowerxi.app.api.response

data class CreateTeamResponse(
    val message: String = "",
    val result: CreateTeamResult = CreateTeamResult(),
    val status: Int = 0
)

data class CreateTeamResult(
    val marathonstatus: Int = 0,
    val status: Int = 0,
    val teamid: Int = 0
)