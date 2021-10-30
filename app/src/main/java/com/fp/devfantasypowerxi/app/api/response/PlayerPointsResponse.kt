package com.fp.devfantasypowerxi.app.api.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PlayerPointsResponse(
    val message: String = "",
    val status: Int = 0,
    val result: ArrayList<PlayerPointItem> = ArrayList()
)

@Parcelize
data class PlayerPointItem(
    val actual_century: Int = 0,
    val not_out: Int = 0,
    val actual_point150: Int = 0,
    val halcentury: Int = 0,
    val catch_points: Int = 0,
    val strike_rate: Int = 0,
   // val negative: Int = 0,
   // val player_id: Int = 0,
    val is_topplayer: Int = 0,
    val maidens: Int = 0,
    val isSelected: Int = 0,
    val economy_rate: Int = 0,
    val credit: Int = 0,
    val actual_halcentury: Int = 0,
    val sixs: Int = 0,
    val point200: Int = 0,
    val century: Int = 0,
    val startingpoints: Int = 0,
   // val negative_points_actual: Int = 0,
    val actual_point200: Int = 0,
    val wickets: Int = 0,
    val point150: Int = 0,
    val stumping: Int = 0,
    val winner_point: Int = 0,
    val total_points: Double = 0.0,
    val actual_economy_rate: Double = 0.0,
    val fours: Double = 0.0,
    val selected_by: Double = 0.0,
    val actual_strike_rate: Double = 0.0,
    val runs: Double = 0.0,
    val actual_notout: String? = "",
    val actual_sixs: String = "",
    val actual_wicket: String = "",
    val actual_runouts: String = "",
    val actual_maidens: String = "",
    val duck: String = "",
    val player_name: String = "",
    val runouts: String = "",
    val actual_duck: String = "",
    val actual_stumping: String = "",
    val team: String = "",
    val actual_runs: String = "",
  //  val player_key: String = "",
    val actual_winning: String = "",
    val actual_fours: String = "",
    val actual_catch: String = "",
    val actual_startingpoints: String = "",
    val image: String = "",
    val field_label: ArrayList<PlayerBreakUpItem> = ArrayList()
) : Parcelable
{

    fun showSelectedBy(): String {
        return "$selected_by %"
    }
}

@Parcelize
data class PlayerBreakUpItem(
    val actual: String = "",
    val event: String = "",
    val points: String = "",
) : Parcelable