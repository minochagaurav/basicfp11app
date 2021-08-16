package com.fp.devfantasypowerxi.app.api.request

data class JoinContestRequest(

    var matchkey: String = "",
    var user_id: String = "",
    var challengeid: String = "",
    var teamid: String = "",
    var sport_key: String = "",
    var fantasy_type: Int = 0
)
