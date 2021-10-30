package com.fp.devfantasypowerxi.app.api.response

import com.fp.devfantasypowerxi.common.utils.Constants

data class MyTeamResponse(
    val result: MyTeamResult = MyTeamResult(),
    val status: Int = 0,
    val message: String = "",
)

data class MyTeamResult(
    val joined_leagues: Int = 0,
    //   val limit: MyTeamLimit = MyTeamLimit(),
    val max_multi_entry_user: Int = 0,
    val teams: ArrayList<Team> = ArrayList(),
    val user_teams: Int = 0,
)
/*
data class MyTeamLimit(
    val max: MyTeamMax = MyTeamMax(),
    val maxplayers: Int = 0,
    val min: MyTeamMin = MyTeamMin(),
    val team_max_player: Int = 0,
    val total_credits: Int = 0
)*/

data class Team(
     val fantasy_type: Int = 0,
    val is_joined: Int = 0,
    //val join_id: Int = 0,
    //val matchkey: String = "",
    // val max_team_count: Int = 0,
    val players: ArrayList<PlayerListResult> = ArrayList(),
    //   val slotes: Int = 0,
    val sport_key: String = "",
    val teamid: Int = 0,
    var isSelected: Boolean = false,
    val teamnumber: Int = 0,
) {

    fun showTeamName(): String {
        return "Team $teamnumber"
    }


    fun captainName(): String {
        var captionName = ""
        for (player in players) {
            if (player.captain == 1) {
                captionName = player.name
                break
            }
        }
        return captionName
    }


    fun batsmanCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_BAT,ignoreCase = true)) i++
        }
        return "BAT ($i)"
    }

    private fun defCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals (Constants.KEY_PLAYER_ROLE_DEF,ignoreCase = true)) i++
        }
        return "DEF ($i)"
    }


    fun shoothingGuardCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals (Constants.KEY_PLAYER_ROLE_SG,ignoreCase = true)) i++
        }
        return "SG ($i)"
    }
    //	android:text="@{team.checkIsFootballFantasy ? team.defCount : team.batsmanCount}"
    fun getPlayerRoleComboTag2(): String {
        var tag: String? = ""
        tag = when {
            sport_key.equals(Constants.TAG_FOOTBALL, ignoreCase = true) -> {
                defCount()
            }
            sport_key.equals(Constants.TAG_BASKETBALL, ignoreCase = true) -> {
                shoothingGuardCount()
            }
            else -> {
                batsmanCount()
            }
        }
        return tag
    }

    fun midCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_MID)) i++
        }
        return "MID ($i)"
    }

    fun smallForwardCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_SF, ignoreCase = true)) i++
        }
        return "SF ($i)"
    }


    fun bolwerCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_BOL, ignoreCase = true)) i++
        }
        return "BOWL ($i)"
    }
    fun allRounderCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_ALL_R, ignoreCase = true)) i++
        }
        return "AR ($i)"
    }
    fun getPlayerRoleComboTag3(): String {
        var tag: String? = ""
        if (sport_key.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
            tag = midCount()
        } else if (sport_key.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
            tag = smallForwardCount()
        } else {
            tag = allRounderCount()
        }
        return tag
    }

    fun vcCaptainName(): String {
        var vcCaptionName = ""
        for (player in players) {
            if (player.vicecaptain == 1) {
                vcCaptionName = player.name
                break
            }
        }
        return vcCaptionName
    }


    fun getCaptionUrl(): String {
        var captionImage = ""
        for (player in players) {
            if (player.captain == 1) {
                captionImage = player.image
                break
            }
        }
        return captionImage
    }

    fun getVcCaptionUrl(): String {
        var vcCaptionImage = ""
        for (player in players) {
            if (player.vicecaptain == 1) {
                vcCaptionImage = player.image
                break
            }
        }
        return vcCaptionImage
    }
    fun goalKeeperCount(): String {
        var i = 0
        for (player in players) {
            if (player.role == (Constants.KEY_PLAYER_ROLE_GK)) i++
        }
        return "GK ($i)"
    }
    private fun keeperCount(): String {
        var i = 0
        for (player in players) {
            if (player.role == (Constants.KEY_PLAYER_ROLE_KEEP)) i++
        }
        return "WK ($i)"
    }

    fun getPlayerRoleComboTag1(): String {
        var tag: String? = ""
        if (sport_key.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
            tag = goalKeeperCount()
        } else {
            tag = keeperCount()
        }
        return tag
    }
    fun stCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_ST, ignoreCase = true)) i++
        }
        return "ST ($i)"
    }

    private fun powerForwardCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_PF, ignoreCase = true)) i++
        }
        return "PF ($i)"
    }


    //	android:text="@{team.checkIsFootballFantasy ? team.stCount() : team.bolwerCount}"
    fun getPlayerRoleComboTag4(): String {
        var tag: String? = ""
        if (sport_key.equals(Constants.TAG_FOOTBALL, ignoreCase = true)) {
            tag = stCount()
        } else if (sport_key.equals(Constants.TAG_BASKETBALL, ignoreCase = true)) {
            tag = powerForwardCount()
        } else {
            tag = bolwerCount()
        }
        return tag
    }
    fun showCentre(): Boolean {
        return sport_key.equals(Constants.TAG_BASKETBALL, ignoreCase = true)
    }

    fun centreCount(): String {
        var i = 0
        for (player in players) {
            if (player.role.equals(Constants.KEY_PLAYER_ROLE_C, ignoreCase = true)) i++
        }
        return "C ($i)"
    }

}

data class MyTeamMax(
    val ar: Int = 0,
    val bat: Int = 0,
    val bowl: Int = 0,
    val wk: Int = 0,
)

data class MyTeamMin(
    val ar: Int = 0,
    val bat: Int = 0,
    val bowl: Int = 0,
    val wk: Int = 0,
)/*
@Parcelize
data class Player(
    val captain: Int = 0,
    val captain_selected_by: String = "",
    val credit: String = "",
    val id: Int = 0,
    val image: String = "",
    val is_playing: Int = 0,
    val is_playing_show: Int = 0,
    val matchkey: String = "",
    val name: String = "",
    val role: String = "",
    val selected_by: String = "",
    val series_points: String = "",
    val team: String = "",
    val teamcode: String = "",
    val teamcolor: String = "",
    val vice_captain_selected_by: String = "",
    val vicecaptain: Int = 0
):Parcelable*/