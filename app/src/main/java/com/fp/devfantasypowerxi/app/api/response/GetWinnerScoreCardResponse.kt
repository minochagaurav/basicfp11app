package com.fp.devfantasypowerxi.app.api.response

data class GetWinnerScoreCardResponse(
    val result: ArrayList<GetWinnerScoreCardResult> = ArrayList(),
    val status: Int = 0
)

data class GetWinnerScoreCardResult(
    val description: Any? = Any(),
    val id: Int = 0,
    val image: String = "",
    val image_description: String = "",
    val price: Int = 0,
    val start_position: String = "",
    val total: Int = 0,
    val winners: Int = 0
){
    fun showRank(): String {
        return "Rank $start_position"
    }
    fun showPrice(): String {
        return "₹$price"
    }
}