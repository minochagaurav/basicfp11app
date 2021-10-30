package com.fp.devfantasypowerxi.app.api.response

import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.common.utils.Constants

data class ContestDetailResponse(
    val result: ContestDetailResult = ContestDetailResult(),
    val status: Int = 0,
)

data class ContestDetailResult(
    val contest: ArrayList<Contest> = ArrayList(),
//    val joinedleauges: Int = 0,
//    val user_teams: Int = 0,
    val joinedusers: Int = 0,
)

data class Contest(
 //   val bonus_percent: String = "",
    val challenge_id: Int = 0,
   // val challenge_type: String = "",
   // val confirmed_challenge: Int = 0,
   // val contest_worth_free_team: Int = 0,
   // val contest_worth_paid_team: Int = 0,
  //  val contest_worth_text: String = "",
   // val discount_expiry_time: Int = 0,
  //  val discount_percentage: Int = 0,
  //  val entryfee: Int = 0,
    // val firstprize: ArrayList<Any> = ArrayList(),
  //  val getjoinedpercentage: Int = 0,
  //  val id: Int = 0,
  //  val image: String = "",
    val teamname: String = "",
//    val image_description: String = "",
//    val is_bonus: Int = 0,
//    val is_contest_worth_entry: Int = 0,
//    val is_contest_worth_free: Int = 0,
//    val is_discount: Int = 0,
//    val is_free_entry: Int = 0,
//    val is_free_entry_on_refer: Int = 0,
//    val is_running: Int = 0,
//    val is_spcl_bonus_active: Int = 0,
    val isjoined: Boolean = false,
   // val isselected: Boolean = false,
   // val isselectedid: String = "",
    val join_id: Int = 0,
//    val join_with: Int = 0,
//    val joinedleagueskey: Int = 0,
//    val joinedusers: Int = 0,
//    val matchkey: String = "",
//    val max_multi_entry_user: String = "",
//    val maximum_user: Int = 0,
//    val min_join_for_refer_free: Int = 0,
//    val multi_entry: String = "",
    val name: String = "",
    val points: String = "",
//    val refer_free_entry_text: String = "",
//    val refer_joined: Int = 0,
//    val refercode: String = "",
//    val refercode_for_free: String = "",
//    val spcl_bonus_expiry_time: Int = 0,
//    val spcl_bonus_percent: Int = 0,
//    val team1display: String = "",
//    val team2display: String = "",
    val team_id: Int = 0,
//    val totalwinners: Int = 0,
//    val totalwinnerss: ArrayList<Any> = ArrayList(),
    val user_image: String = "",
    val userid: Int = 0,
    val userrank: String = "",
    val win_amount: Int = 0,
   // val winners: Int = 0,
    val winning_amount: String = "",
   // val winning_percentage: String = "",
   // val winningpercentage: String = "",
)
{
    fun isCurrentTeam(): Boolean {
        return MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)==
                userid.toString()
    }
    fun showWiningAmount(): Boolean {
        return win_amount != 0
    }

    fun getWiningAmountShow(): String {
        return "Winning FC $win_amount"
    }


    fun showRank(): String {
        return "#$userrank"
    }
}