package com.fp.devfantasypowerxi.common.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException

class SharePreferenceDB(private var preferences: SharedPreferences) {

    // Get Methods
    fun getString(key: String?): String? {
        return preferences.getString(key, "")
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return preferences.getInt(key, defaultValue)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }
    // Put Methods
     fun clear() {
        preferences.edit().clear().apply()
    }
    fun putString(key: String?, value: String?) {
        preferences.edit().putString(key, value).apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun putInt(key: String?, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }
   /* var bannerArrayList: ArrayList<Banner>
        get() {
            val bannerArrayList = ArrayList<Banner>()
            val str = preferences.getString(Constants.BANNER, null)
            try {

                if (str == null) {

                } else {
                    val jsonarr = JSONArray(str)

                    for (i in 0 until jsonarr.length()) {
                        val banner = Gson().fromJson<Banner>(
                            jsonarr.get(i).toString(),
                            Banner::class.java
                        )
                        bannerArrayList.add(banner)
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return bannerArrayList
        }

        set(bannerArrayList) {
            preferences.edit()
                .putString(Constants.BANNER, Gson().toJson(bannerArrayList)).apply()
        }*/


}