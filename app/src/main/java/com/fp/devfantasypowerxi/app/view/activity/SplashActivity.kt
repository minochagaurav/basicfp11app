package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.BuildConfig
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.extraLib.ConnectionDetector
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivitySplashBinding
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*


// made by Gaurav Minocha
class SplashActivity : AppCompatActivity() {
    private var currentVersion = 0
    private val onlineVersion = 0
    private var liveVersion = ""
    var appVersion = ""
    private lateinit var cd: ConnectionDetector
    var builder: AlertDialog.Builder? = null
    lateinit var mainBinding: ActivitySplashBinding
    var whatNewText = ""

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        cd = ConnectionDetector(applicationContext)

        // wait for 1.5 second then move to other activity
        /*  Handler(Looper.getMainLooper()).postDelayed({
              startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
              finish()
          }, 1500)*/
        val playStoreVersionCode: Int = FirebaseRemoteConfig.getInstance().getString(
            "android_latest_version_code").toInt()
        val currentAppVersionCode = BuildConfig.VERSION_CODE
        val forceUpdate = FirebaseRemoteConfig.getInstance().getBoolean(
            "force_update")
        whatNewText = FirebaseRemoteConfig.getInstance().getString(
            "what_new")
        if (playStoreVersionCode > currentAppVersionCode) {
            showPopup(forceUpdate)
        } else {
            snack()
        }
        MyApplication.preferenceDB!!.putString(Constants.DEVICE_ID, BuildConfig.VERSION_NAME)
        MyApplication.preferenceDB!!.putString(Constants.APP_VERSION,
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString())

    }

    private fun showPopup(forceUpdate: Boolean) {
      //  Log.e("check data", whatNewText)
        builder = AlertDialog.Builder(this)
        builder!!.setMessage("What's New" +
                "\n" + "\n" +
                Html.fromHtml(whatNewText, Html.FROM_HTML_MODE_LEGACY)

        )
            .setTitle("Update Alert!!!")

            .setCancelable(false)
            .setPositiveButton("Update") { dialog, id ->
                dialog.cancel()
                val shareLink =
                    "https://play.google.com/store/apps/details?id=com.fp.devfantasypowerxi"
                val share = Intent(Intent.ACTION_VIEW)
                share.data = Uri.parse(shareLink)
                startActivity(share)
            }
        if (!forceUpdate) {
            builder!!.setNegativeButton("Cancel") { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
                snack()
            }
        }
        //Creating dialog box
        val alert = builder!!.create()
        alert.setTitle("Update Alert!")
        alert.show()
    }


    @SuppressLint("WrongConstant", "SetTextI18n")
    fun snack() {
        if (cd.isConnectingToInternet) {
            if (currentVersion >= onlineVersion) {
                openMainActivity()
            }
        } else {
            Toast.makeText(applicationContext, "No Internet connection !!", Toast.LENGTH_SHORT)
                .show()
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
