package com.fp.devfantasypowerxi.app.api.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class BannerListResponse(
    val all_announcement: String = "",
    //val api_base_url: String = "",
   // val app_download_url: String = "",
   // val app_referral_url: String = "",
    //val base_url: String = "",
    val in_app_image: String = "",
    val result: ArrayList<Banner> = ArrayList(),
    val sport_types: ArrayList<SportType> = ArrayList(),
    val status: Int = 0,
    //val version: Int = 0,
    //val version_code: String = ""
)
/*

data class BannerResult(
    val id: Int = 0,
    val image: String = "",
    val link: String = "",
    val title: String = ""
)
*/

data class SportType(
    val fantasy_type: ArrayList<FantasyType> = ArrayList(),
    val id: Int = 0,
    val sport_key: String = "",
    val sport_name: String = "",
    val status: Int = 0
)

@Parcelize
data class FantasyType(
    val name: String = "",
    val type: Int = 0
): Parcelable