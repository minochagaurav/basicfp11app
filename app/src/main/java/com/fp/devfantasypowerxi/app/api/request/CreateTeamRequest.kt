package com.fp.devfantasypowerxi.app.api.request

data class CreateTeamRequest(
    var matchkey:String="",
    var userid:String="",
    var players:String="",
    var vicecaptain:String="",
    var captain:String="",
    var teamid:Int=0,
    var sport_key:String="",
    var fantasy_type:Int=0,

)
