package com.fp.devfantasypowerxi.app.api.request

data class TeamPreviewPointRequest(
    var user_id: String = "",
    var joinid: String = "",
    var teamid: String = "",
    var challenge: String = "",
    var sport_key: String = ""
)
