package com.fp.cricbull.common.utils

import android.app.Activity
import android.widget.Toast


interface Flag {
    fun isValid(): Boolean
}

class GetFlag : Flag {
    override fun isValid(): Boolean = true
}

class FlagValidations(
    private val context: Activity,
    private val msg: String,
    private val flag: String
) : Flag {
    private val getFlag = GetFlag()

    // var flag: String = ""
    override fun isValid(): Boolean {
        return when (flag) {
            "1" -> {
                getFlag.isValid()
            }
            "3" -> {
             //   MyApplication.logout(context)
                false
            }
            else -> {
                if (msg != "")
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

}