package com.fp.devfantasypowerxi.app.api.request

data class MyTeamRequest(
    var matchkey: String = "",
    var challenge_id: String = "",
    var user_id: String = "",
    var sport_key: String = "",
    var fantasy_type: Int = 0
)
