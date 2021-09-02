package com.fp.devfantasypowerxi.app.api.response

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class MatchListResponse(
    var result: ArrayList<MatchListResult> = ArrayList(),
    var status: Int = 0,
    var message: String = ""
)

data class MatchListResult(
    val final_status: String = "",
    val format: String = "",
    val id: Int = 0,
    val joined_count: Int = 0,
    val launch_status: String = "",
    val lineup: Int = 0,
   // val locktime: LockTime = LockTime(),
    val match_status: String = "",
    val match_status_key: Int = 0,
    val matchindexing: String = "",
    val matchkey: String = "",
    val matchopenstatus: String = "",
    val name: String = "",
    val series: Int = 0,
    val seriesname: String = "",
    val short_name: String = "",
    val team1color: String = "",
    val team1display: String = "",
    val team1logo: String = "",
    val team2color: String = "",
    val team2display: String = "",
    val team1fdisplay: String = "",
    val team2fdisplay: String = "",
    val team2logo: String = "",
    val time_start: String = "",
    val url: String = "",
    val winnerstatus: String = "",


    ) {
    fun getStartDate(): String {
        val string: String = time_start
        var date1: Date? = null
        return try {
            date1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string)
            val simpleDateFormat = SimpleDateFormat("dd MMM,yyyy")
            simpleDateFormat.format(date1)
        } catch (e: ParseException) {
            ""
        }
    }
    fun getJoinedCount(): String {
        return "$joined_count Contests Joined"
    }
}

data class LockTime(
    val date: String = "",
    val timezone: String = "",
    val timezone_type: Int = 0
)
