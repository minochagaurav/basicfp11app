package com.fp.devfantasypowerxi

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.FacebookSdk
import com.facebook.drawee.backends.pipeline.Fresco
import com.fp.devfantasypowerxi.app.di.AppComponent
import com.fp.devfantasypowerxi.app.di.DaggerAppComponent
import com.fp.devfantasypowerxi.app.di.module.AppModule
import com.fp.devfantasypowerxi.app.view.activity.LoginActivity
import com.fp.devfantasypowerxi.common.di.module.NetModule
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.common.utils.SharePreferenceDB
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Fresco.initialize(this)

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
            } else {
                Log.e("Installations", "Unable to get Installation ID")
            }
        }
        baseUrl = "https://fp11games.in/fp11_practice_app/api/v1/"
        val spPrivate = getSharedPreferences("private", Context.MODE_PRIVATE)
        val spPrivateTwo = getSharedPreferences("privateTwo", Context.MODE_PRIVATE)
        preferenceDB = SharePreferenceDB(spPrivate)
        preferenceDBTwo = SharePreferenceDB(spPrivateTwo)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("token failed", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            firebase_token = task.result!!
            preferenceDBTwo!!.putString(Constants.SHARED_PREFERENCE_USER_FIREBASE_TOKEN,
                firebase_token)
            Log.d("firebase_token", firebase_token)
        })

        FacebookSdk.sdkInitialize(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // set in-app defaults

        // set in-app defaults
        val remoteConfigDefaults: MutableMap<String, Any> = HashMap()
        remoteConfigDefaults["android_latest_version_code"] = 1
        remoteConfigDefaults["force_update"] = false
        remoteConfigDefaults["what_new"] = "- bug Fix"
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults)
        firebaseRemoteConfig.fetch(60) // fetch every minutes
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseRemoteConfig.activate()
                }
            }
        component = DaggerAppComponent.builder().appModule(AppModule(this)).netModule(
            NetModule(
                baseUrl
            )
        ).build()
    }

    fun getComponent(): AppComponent? {
        return component
    }

    companion object {
        var firebase_token = ""

        @JvmField
        var appContext: Context? = null

        @JvmField
        var baseUrl = ""
        var baseUrlSecond = ""
        var component: AppComponent? = null

        //   var componentSecond: AppComponent? = null
        @JvmField
        var preferenceDB: SharePreferenceDB? = null

        @JvmField
        var preferenceDBTwo: SharePreferenceDB? = null

        fun getAppComponent(): AppComponent? {
            return component
        }

        fun logout(context: Activity) {
            Toast.makeText(
                context,
                "Your session has been expired please login again",
                Toast.LENGTH_LONG
            ).show()
            preferenceDB!!.clear()
            val i = Intent(context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.finish()
            context.startActivity(i)
        }

    }

}