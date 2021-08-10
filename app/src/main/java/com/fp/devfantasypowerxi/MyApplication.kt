package com.fp.devfantasypowerxi

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.fp.devfantasypowerxi.app.di.AppComponent
import com.fp.devfantasypowerxi.app.di.DaggerAppComponent
import com.fp.devfantasypowerxi.app.di.module.AppModule
import com.fp.devfantasypowerxi.app.view.activity.LoginActivity
import com.fp.devfantasypowerxi.common.di.module.NetModule
import com.fp.devfantasypowerxi.common.utils.SharePreferenceDB

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        //  FirebaseMessaging.getInstance().isAutoInitEnabled = true
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        /*  FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
              if (!task.isSuccessful) {
                  Log.w("token failed", "Fetching FCM registration token failed", task.exception)
                  return@OnCompleteListener
              }

              // Get new FCM registration token
              firebase_token = task.result!!
              Log.d("firebase_token", firebase_token)
          })
  */
        baseUrl = "http://52.66.253.117/fantasypower11_api/api/auth/v2/"
        // baseUrl = "http://rest.entitysport.com/"
        //  baseUrlSecond = "https://cricbull.com/"
        //  FirebaseApp.initializeApp(applicationContext)
        val spPrivate = getSharedPreferences("private", Context.MODE_PRIVATE)
        val spPrivateTwo = getSharedPreferences("privateTwo", Context.MODE_PRIVATE)
        //   FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        preferenceDB = SharePreferenceDB(spPrivate)
        preferenceDBTwo = SharePreferenceDB(spPrivateTwo)

        component = DaggerAppComponent.builder().appModule(AppModule(this)).netModule(
            NetModule(
                baseUrl
            )
        ).build()

        /*  componentSecond = DaggerAppComponent.builder().appModule(AppModule(this)).netModule(
              NetModule(
                  baseUrlSecond
              )
          ).build()*/
    }

    fun getComponent(): AppComponent? {
        return component
    }

    /* fun getAppComponentSecond(): AppComponent? {
         return componentSecond
     }*/
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

        /*  fun getAppComponentSecond(): AppComponent? {
              return componentSecond
          }*/
        fun logout(context: Activity) {
            Toast.makeText(
                context,
                "Your session has been expired please login again",
                Toast.LENGTH_LONG
            ).show()
            MyApplication.preferenceDB!!.clear()
            val i = Intent(context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.finish()
            context.startActivity(i)
        }

    }

}