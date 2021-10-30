package com.fp.devfantasypowerxi.app.api.response

import android.os.Parcelable
import com.fp.devfantasypowerxi.common.utils.Constants
import kotlinx.android.parcel.Parcelize


data class PlayerListResponse(
    val limit: Limit = Limit(),
    val message: String = "",
    val result: ArrayList<PlayerListResult> = ArrayList(),
    val status: Int = 0
)

data class Limit(
  //  val max: Max = Max(),
    val maxplayers: Int = 0,
   // val min: Min = Min(),
    val team_max_player: Int = 0,
    val total_credits: Double = 0.0
)

@Parcelize
data class PlayerListResult(
    var captain: Int = 0,
    val captain_selected_by: String = "",
    val credit: Double = 0.0,
    val id: Int = 0,
    val image: String = "",
    val is_playing: Int = 0,
    val is_playing_show: Int = 0,
    val points: Double = 0.0,
    val matchkey: String = "",
    val name: String = "",
    val role: String = "",
    val selected_by: String = "",
    val series_points: String = "",
    val team: String = "",
    val teamcode: String = "",
    val teamcolor: String = "",
    val vice_captain_selected_by: String = "",
    val vicecaptain: Int = 0,
    var isSelected: Boolean = false,
    var isHeader: Boolean = false,
    var isCaptain: Boolean = false,
    var isVcCaptain: Boolean = false
) : Parcelable {
    fun getSelectedBy(): String {
        return "$selected_by %"
    }

    fun getShortName(): String {
        val finalName = name.trim { it <= ' ' }
        val s1 = StringBuilder()
        return run {
            val args = finalName.split(" ").toTypedArray()
            if (args.size > 1) {
                for (i in args.indices) {
                    if (i == 0) {
                        s1.append(args[i][i])
                    } else if (i == args.size - 1) {
                        s1.append(" " + args[args.size - 1])
                    }
                }
                s1.toString()
            } else finalName
        }
    }

    fun getCaptainSelectedBy(): String {
        return "$captain_selected_by %"
    }


    fun getViceCaptainSelectedBy(): String {
        return "$vice_captain_selected_by %"
    }

    fun getTeamNameWithRole(): String {
        var roleTag = ""
        if (role.equals(Constants.KEY_PLAYER_ROLE_KEEP, ignoreCase = true)) {
            roleTag = "WK"
        } else if (role.equals(Constants.KEY_PLAYER_ROLE_BAT, ignoreCase = true)) {
            roleTag = "BAT"
        } else if (role.equals(Constants.KEY_PLAYER_ROLE_ALL_R, ignoreCase = true)) {
            roleTag = "ALL"
        } else if (role.equals(Constants.KEY_PLAYER_ROLE_BOL, ignoreCase = true)) {
            roleTag = "BOWL"
        } else if (role.equals(Constants.KEY_PLAYER_ROLE_GK, ignoreCase = true)) {
            roleTag = "GK"
        } else if (role.equals(Constants.KEY_PLAYER_ROLE_DEF, ignoreCase = true)) {
            roleTag = "DEF"
        } else if (role.equals(Constants.KEY_PLAYER_ROLE_MID, ignoreCase = true)) {
            roleTag = "MID"
        } else if (role.equals(Constants.KEY_PLAYER_ROLE_ST, ignoreCase = true)) {
            roleTag = "ST"
        }
        return roleTag
    }
}

data class Max(
    val ar: Int = 0,
    val bat: Int = 0,
    val bowl: Int = 0,
    val wk: Int = 0
)

data class Min(
    val ar: Int = 0,
    val bat: Int = 0,
    val bowl: Int = 0,
    val wk: Int = 0
)