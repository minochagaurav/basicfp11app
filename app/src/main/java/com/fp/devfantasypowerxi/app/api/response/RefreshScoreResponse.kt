package com.fp.devfantasypowerxi.app.api.response

data class RefreshScoreResponse(
    val message: String = "",
    val status: Int = 0,
    val result: RefreshScoreItem = RefreshScoreItem()
)

data class RefreshScoreItem(
    val contest: ArrayList<LiveFinishedContestData> = ArrayList(),
    val total_investment: String = "",
    val total_winnings: String = "",
    val total_profilt: String = "",
    val match_announcement: String = ""

)

data class LiveFinishedContestData(
    var winingamount: Int = 0,
    val is_private: Int = 0,
    val totalwinners: Int = 0,
    val challenge_id: Int = 0,
    val id: Int = 0,
    val minimum_user: Int = 0,
    val recycler_item_price_card: ArrayList<PriceCardItem>? = ArrayList(),
    val multi_entry: Int = 0,
    val confirmed: Int = 0,
    val points: String = "",
    val matchstatus: String = "",
    val teamid: Int = 0,
    val userrank: Int = 0,
    val maximum_user: Int = 0,
    val can_invite: Int = 0,
    val entryfee: Int = 0,
    val challenge_type: String = "",
    val joinedusers: Int = 0,
    val joinid: Int = 0,
    val grand: Int = 0,
    val winning_percentage: Any? = null,
    val pdf: String = "",
    val join_with: String = "",
    val name: String = "",
    val team_number_get: Int = 0,
    val win_amount: Int = 0,
    val winning_amount: String = "",
    val pdf_created: Int = 0,
    val status: Int = 0,
    val getjoinedpercentage: String = "",
    val firstprize: String = "",
    val winningpercentage: String = "",
    val image: String = "",
    val image_description: String = ""
){
    fun gadgetOrPriceLeagueText(): String {
        return if (image != "") {
            image_description
        } else {
            "Prize Pool"
        }
    }


    fun showWinningAmount(): String {
        return "FC $win_amount"
    }
    fun showUserRank(): String {
        return "#$userrank"
    }

}

data class PriceCardItem(
    private var start_position: Int = 0,
    val price: Int = 0,
    val id: Int = 0,
)
