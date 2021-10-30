package com.fp.devfantasypowerxi.app.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.BuildConfig
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.extraLib.ConnectionDetector
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.ActivitySplashBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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
    var playStoreVersionCode = 0
    var currentAppVersionCode = 0
    var forceUpdate = false

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

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                var parameter: Bundle? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    parameter = pendingDynamicLinkData.extensions
                }
                //
                // If the user isn't signed in and the pending Dynamic Link is
                // an invitation, sign in the user anonymously, and record the
                // referrer's UID.
                //
                val user = Firebase.auth.currentUser
                if (user == null &&
                    deepLink != null
                ) {
                    Log.e("uid linkssssss", deepLink.toString())
                    Log.e("uid user", parameter.toString())
                }
            }
        playStoreVersionCode = FirebaseRemoteConfig.getInstance().getString(
            "android_latest_version_code").toInt()
        currentAppVersionCode = BuildConfig.VERSION_CODE
        forceUpdate = FirebaseRemoteConfig.getInstance().getBoolean(
            "force_update")
        whatNewText = FirebaseRemoteConfig.getInstance().getString(
            "what_new")

        if (checkPermission())
        {
            if (playStoreVersionCode > currentAppVersionCode) {
                showPopup(forceUpdate)
            } else {
                snack()
            }
        }else{
            ActivityCompat.requestPermissions(this@SplashActivity,
                arrayOf(Manifest.permission.READ_CONTACTS), 100)
        }


        MyApplication.preferenceDB!!.putString(Constants.DEVICE_ID, BuildConfig.VERSION_NAME)
        MyApplication.preferenceDB!!.putString(Constants.APP_VERSION,
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString())

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        Log.e("permission", "enter")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (playStoreVersionCode > currentAppVersionCode) {
            showPopup(forceUpdate)
        } else {
            snack()
        }
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) !== PackageManager.PERMISSION_GRANTED
        ) {


            Log.e("permission", "yes")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        } else {
            Log.e("permission", "no")

        }*/
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


    fun checkPermission(): Boolean {
        val permission1 = "android.permission.READ_CONTACTS"
        val res1 = applicationContext.checkCallingOrSelfPermission(permission1)

        return res1 == PackageManager.PERMISSION_GRANTED
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
