package com.fp.devfantasypowerxi.app.api.response

data class JoinedContestResponse(
    val result: JoinedContestResult = JoinedContestResult(),
    val status: Int = 0
)

data class JoinedContestResult(
    val contest: ArrayList<JoinedContestContest> = ArrayList(),
    val joined_leagues: Int = 0,
    val match_announcement: String = "",
    val user_teams: Int = 0
)

data class JoinedContestContest(
    val bonus_percent: String = "",
    val challenge_id: Int = 0,
    val challenge_type: String = "",
    val confirmed_challenge: Int = 0,
    val entryfee: Int = 0,
    val firstprize: Int = 0,
    val getjoinedpercentage: Int = 0,
    val id: Int = 0,
    val image: String = "",
    val image_description: String = "",
    val is_bonus: Int = 0,
    val is_free_entry: Int = 0,
    val is_running: Int = 0,
    val isjoined: Boolean = false,
    val isselected: Boolean = false,
    val isselectedid: String = "",
    val join_id: Int = 0,
    val join_with: Int = 0,
    val joinedleagueskey: Int = 0,
    val joinedusers: Int = 0,
    val matchkey: String = "",
    val max_multi_entry_user: Int = 0,
    val maximum_user: Int = 0,
    val multi_entry: Int = 0,
    val name: String = "",
    val points: Int = 0,
    val refercode: String = "",
    val team1display: String = "",
    val team2display: String = "",
    val team_id: Int = 0,
    val totalwinners: Int = 0,
    val totalwinnerss: Any? = Any(),
    val user_image: String = "",
    val userid: Int = 0,
    val confirmed: Int = 0,
    val userrank: Int = 0,
    val win_amount: Int = 0,
    val winners: Int = 0,
    val winning_amount: String = "",
    val winning_percentage: String = "",
    val winningpercentage: String = ""
)
{
    fun gadgetOrPriceLeagueText(): String {
        return if (image != "") {
            image_description
        } else {
            "Prize Pool"
        }
    }

    fun showWinningAmount(): String {
        return "₹$win_amount"
    }

    fun showJoinAmount(): String {
        return if (isjoined) if (multi_entry == 1) "JOIN+" else "INVITE" else "₹$entryfee"
    }
    fun showTopRankerPrice(): String {
        return "₹$firstprize"
    }

    fun showWinPrice(): String {
        return "$winning_percentage Team Win"
    }
    fun isShowCTag(): Boolean {
        return confirmed == 1
    }
    fun isShowMTag(): Boolean {
        return multi_entry == 1
    }
    fun isShowBTag(): Boolean {
        return is_bonus == 1
    }

}