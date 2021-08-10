package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.extraLib.ConnectionDetector
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivitySplashBinding

// made by Gaurav Minocha
class SplashActivity : AppCompatActivity() {
    var currentVersion = 0
    private val onlineVersion = 0
    lateinit var cd: ConnectionDetector
    lateinit var mainBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        cd = ConnectionDetector(applicationContext)

        // wait for 1.5 second then move to other activity
      /*  Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }, 1500)*/

        snack()
    }


    @SuppressLint("WrongConstant", "SetTextI18n")
    fun snack() {
        if (Build.VERSION.SDK_INT < 21) {
            val d = AlertDialog.Builder(this@SplashActivity).create()
            d.setCanceledOnTouchOutside(false)
            d.setTitle("Android version not supported")
            d.setMessage("FantasyPower11 doesn't support version lower than android LOLLIPOP")
            d.setButton(
                AlertDialog.BUTTON_POSITIVE, "Ok"
            ) { _, _ -> finish() }
            d.show()
        } else {

                if (cd.isConnectingToInternet) {
                    if (currentVersion >= onlineVersion) {
                        openMainActivity()
                    }
                } else {
                    val inflater1 = layoutInflater
                    val layout: View = inflater1.inflate(
                        R.layout.custom_toast1,
                        findViewById<View>(R.id.toast_layout_root) as ViewGroup
                    )
                    val text = layout.findViewById<View>(R.id.text) as TextView
                    text.text = "No Internet connection !!"
                    val toast = Toast(applicationContext)
                    toast.setGravity(Gravity.BOTTOM, 0, +150)
                    toast.duration = 2000
                    toast.setView(layout)
                    toast.show()
                }
            }
        }

private fun openMainActivity() {
      Handler(Looper.getMainLooper()).postDelayed({
          if (MyApplication.preferenceDB!!.getBoolean(
                  Constants.SHARED_PREFERENCES_IS_LOGGED_IN,
                  false
              )
          ) startActivity(
              Intent(
                  this@SplashActivity,
                  HomeActivity::class.java
              )
          ) else startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
          finish()
        }, 1500)

}

}
