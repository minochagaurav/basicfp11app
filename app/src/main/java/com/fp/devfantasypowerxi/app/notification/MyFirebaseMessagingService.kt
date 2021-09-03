package com.fp.devfantasypowerxi.app.notification

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fp.devfantasypowerxi.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var x = 0
    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {

                if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo: ComponentName? = taskInfo[0].topActivity
            if (componentInfo!!.packageName == context.packageName) {
                isInBackground = false
            }
        }
        return isInBackground
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val params = remoteMessage.data
        val `object` = JSONObject(params as Map<*, *>)
        Log.e("JSON_OBJECT", `object`.toString())
        val notificationChannelIid = "gaurav"

        if (isAppIsInBackground(this))
        {
            Log.e("JSON_OBJECT_back", `object`.toString())
        }else
        {
            Log.e("JSON_OBJECT_not_back", `object`.toString())
        }
        val pattern = longArrayOf(0, 1000, 500, 1000)

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelIid, "Your Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = ""
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = pattern
            notificationChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        // to display notification in DND Mode

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = mNotificationManager.getNotificationChannel(notificationChannelIid)
            channel.canBypassDnd()
        }

        val click_action= remoteMessage.notification!!.clickAction

        val notificationIntent = Intent(click_action)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val intent = PendingIntent.getActivity(
            this, 101,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelIid)

        notificationBuilder.setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setContentTitle("Fantasy Power 11")
            .setContentText(remoteMessage.notification!!.body)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.logo_without_name)
            .setAutoCancel(true)
            .setContentIntent(intent)
        x = Random().nextInt(101)
        mNotificationManager.notify(101, notificationBuilder.build())

        //  sendNotification()
     //   super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        if (token.isNotEmpty()) {
            Log.e("NEW_TOKEN", token)
        }
    }

    /* private fun sendNotification() {
         val intent = Intent(applicationContext, NotificationActivity::class.java)
         intent.flags = FLAG_ACTIVITY_NEW_TASK
         startActivity(intent)
     }*/
}
