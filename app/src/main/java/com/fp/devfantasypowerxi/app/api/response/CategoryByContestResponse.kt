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
    val contest_type_image: String = "",
    val fantasy_type: Int = 0,
    val id: Int = 0,
    val leagues: ArrayList<League> = ArrayList(),
    val name: String = "",
    val sort_order: Int = 0,
    val sport_type: Int = 0,
    val status: Int = 0,
    val total_category_leagues: Int = 0
)
@Parcelize
data class League(
    val bonus_percent: String = "",
    val challenge_category_id: Int = 0,
    val challenge_type: String = "",
    val confirmed_challenge: Int = 0,
    val entryfee: Int = 0,
    val firstprize: Int = 0,
    val getjoinedpercentage: String = "",
    val id: Int = 0,
    val image: String = "",
    val image_description: String = "",
    val is_bonus: Int = 0,
    val is_free_entry: Int = 0,
    val is_running: Int = 0,
    val isselected: Boolean = false,
    val isjoined: Boolean = false,
    val isselectedid: String = "",
    val joinedusers: Int = 0,
    val matchkey: String = "",
    val max_multi_entry_user: Int = 0,
    val maximum_user: Int = 0,
    val multi_entry: Int = 0,
    val name: String = "",
    val status: Int = 0,
    val totalwinners: String = "",
    val pdf: String = "",
    val win_amount: Int = 0,

     val winning_percentage: @RawValue Any? =null,
    val winningpercentage: String = ""
):Parcelable

{

    fun showJoinAmount(): String {
        return if (isjoined) {
            if (multi_entry == 1) "JOIN+" else "INVITE"
        } else "₹$entryfee"
    }
    fun showJoinAmountForDetail(): String {
        return "₹$entryfee"
    }
    fun gadgetOrPriceLeagueText(): String {
        return if (image != "") {
            image_description
        } else {
            "Prize Pool"
        }
    }
    fun showTopRankerPrice(): String {
        return "₹$firstprize"
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