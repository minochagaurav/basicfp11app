package com.fp.devfantasypowerxi.app.api.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class CategoryByContestResponse(
    val result: CategoryByContestResult? = CategoryByContestResult(),
    val status: Int = 0
)

data class CategoryByContestResult(
    val categories: ArrayList<CategoryByContestCategory> = ArrayList(),
    val joined_leagues: Int = 0,
    val match_announcement: String = "",
    val total_contest: Int = 0,
    val user_teams: Int = 0
)

data class CategoryByContestCategory(
    val contest_image_url: String = "",
    val contest_sub_text: String = "",
   // val contest_type_image: String = "",
   // val fantasy_type: Int = 0,
    val id: Int = 0,
    val leagues: ArrayList<League> = ArrayList(),
    val name: String = "",
//    val sort_order: Int = 0,
//    val sport_type: Int = 0,
//    val status: Int = 0,
//    val total_category_leagues: Int = 0
)
@Parcelize
data class League(
    var bonus_percent: String = "",
   // var challenge_category_id: Int = 0,
    var challenge_type: String = "",
    var confirmed_challenge: Int = 0,
    var entryfee: Int = 0,
    var firstprize: Int = 0,
    var getjoinedpercentage: String = "",
    var id: Int = 0,
    var image: String = "",
    var image_description: String = "",
    var is_bonus: Int = 0,
  //  var is_free_entry: Int = 0,
  //  var is_running: Int = 0,
  //  var isselected: Boolean = false,
    var isjoined: Boolean = false,
   // var isselectedid: String = "",
    var joinedusers: Int = 0,
    var matchkey: String = "",
    var max_multi_entry_user: Int = 0,
    var maximum_user: Int = 0,
    var multi_entry: Int = 0,
    //var name: String = "",
  //  var status: Int = 0,
    var totalwinners: String = "",
    var pdf: String = "",
    var refercode: String = "",
    var win_amount: Int = 0,
    var winning_percentage: @RawValue Any? =null,
    var winningpercentage: String = ""
):Parcelable

{

    fun showJoinAmount(): String {
        return if (isjoined) {
            if (multi_entry == 1) "JOIN+" else "INVITE"
        } else "FC $entryfee"
    }
    fun showJoinAmountForDetail(): String {
        return "FC $entryfee"
    }
    fun gadgetOrPriceLeagueText(): String {
        return if (image != "") {
            image_description
        } else {
            "Prize Pool"
        }
    }
    fun showTopRankerPrice(): String {
        return "FC $firstprize"
    }

    fun showWinPrice(): String {
        return "$winningpercentage Team Win"
    }

    fun isShowCTag(): Boolean {
        return confirmed_challenge == 1
    }
    fun isShowMTag(): Boolean {
        return multi_entry == 1
    }
    fun isShowBTag(): Boolean {
        return is_bonus == 1
    }


}