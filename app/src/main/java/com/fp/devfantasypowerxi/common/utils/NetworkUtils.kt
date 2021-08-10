package com.fp.devfantasypowerxi.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.fp.devfantasypowerxi.MyApplication
import java.text.SimpleDateFormat
import java.util.*

object NetworkUtils {
    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivity.activeNetworkInfo
        if (null != activeNetwork && activeNetwork.isConnected && activeNetwork.isAvailable) {
            return java.lang.Boolean.TRUE
        }
        return java.lang.Boolean.FALSE
    }

    val isNetworkAvailable: Boolean
        get() {
            val connectivity = MyApplication.appContext
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivity.activeNetworkInfo
            if (null != activeNetwork && activeNetwork.isConnected) {
                return true
            }
            return false
        }

  /*  fun getConnectivityStatus(context: Context): Int {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }*/

   /* fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return (start..end).random()
    }
*/
    @SuppressLint("SimpleDateFormat")
    fun getDate(timestamp: Int): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val date = Date(simpleDateFormat.format(timestamp.toLong() * 1000L))
        var output = ""
        val c = Calendar.getInstance()
        c.time = date
        output = SimpleDateFormat("EEEE").format(date)+", "+ simpleDateFormat.format(timestamp.toLong() * 1000L)
        return output
    }

    fun getTime(timestamp: Int): String {
        val simpleDateFormat = SimpleDateFormat("hh:mma", Locale.ENGLISH)
        return simpleDateFormat.format(timestamp.toLong() * 1000L)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateWithoutDay(timestamp: Int): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val date = Date(simpleDateFormat.format(timestamp.toLong() * 1000L))
        var output = ""
        val c = Calendar.getInstance()
        c.time = date
        output = simpleDateFormat.format(timestamp.toLong() * 1000L)
        return output
    }


/*    @SuppressLint("SimpleDateFormat")
    fun getDateFormatter(dateValue: String): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: Date
        var output = ""
        try {
            date = dateFormat.parse(dateValue)!!
            output = simpleDateFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return output
        }
        return output
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateFormatterWithoutDate(dateValue: String): String {
        val simpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: Date
        var output = ""
        try {
            date = dateFormat.parse(dateValue)!!
            output = simpleDateFormat.format(date)

        } catch (e: ParseException) {
            e.printStackTrace()
            return output
        }
        return output
    }*/


}

