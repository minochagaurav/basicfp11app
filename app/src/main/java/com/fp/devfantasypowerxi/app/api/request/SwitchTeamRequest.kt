package com.fp.devfantasypowerxi.app.api.request

data class SwitchTeamRequest(
    var challenge_id: String = "",
    var teamid: String = "",
    var matchkey: String = "",
    var joinid: String = "",
    var userid: String = "",
    var sport_key: String = "",
    var fantasy_type: Int = 0,
)
