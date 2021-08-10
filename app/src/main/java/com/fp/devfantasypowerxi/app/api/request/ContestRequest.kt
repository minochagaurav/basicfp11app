package com.fp.devfantasypowerxi.app.api.request

data class ContestRequest(
    var matchkey: String = "",
    var user_id: String = "",
    var challenge_id: String = "",
    var page: String = "",
    var category_id: String = "",
    var entryfee: String = "",
    var winning: String = "",
    var contest_type: String = "",
    var contest_size: String = "",
    var sport_key: String = "",
    var fantasy_type: Int = 0,
    var showLeaderBoard: Boolean = false
)
