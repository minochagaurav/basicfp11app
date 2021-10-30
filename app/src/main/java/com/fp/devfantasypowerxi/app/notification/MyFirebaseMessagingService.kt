package com.fp.devfantasypowerxi.app.notification

import android.annotation.SuppressLint
import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.TimeUnit
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fp.devfantasypowerxi.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var x = 0
    lateinit var chronometerCountDown: Chronometer

    private var counter = 10

    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
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
        return isInBackground
    }


    @SuppressLint("RemoteViewLayout")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val params = remoteMessage.data
        //  val `object` = JSONObject(params as Map<*, *>)

        val notificationChannelIid = "gaurav"

        if (isAppIsInBackground(this)) {
            Log.e("JSON_OBJECT_back", params.toString())
        } else {
            Log.e("JSON_OBJECT_not_back", params.toString())
        }
        val pattern = longArrayOf(0, 1000, 500, 1000)

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("check channel", "yes")
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

        val click_action = remoteMessage.notification!!.clickAction

        val notificationIntent = Intent(click_action)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val intent = PendingIntent.getActivity(
            this, 101,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelIid)
        notificationBuilder.setAutoCancel(true)
            .setPriority(10)
            //.setContentTitle(remoteMessage.notification!!.title)
            //.setContentText(remoteMessage.notification!!.body)
            // .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.logo_without_name)
            .setAutoCancel(true)
            .setContentIntent(intent)
            //   .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setColor(Color.parseColor("#000000"))
        // .setColorized(true)

        if (params.isEmpty()) {
            notificationBuilder.setContentTitle(remoteMessage.notification!!.title)
                .setContentText(remoteMessage.notification!!.body)
                .setDefaults(Notification.DEFAULT_ALL)
        } else {
            val notificationView = RemoteViews(packageName, R.layout.custom_notification_layout)

            val startTime = SystemClock.elapsedRealtime()

            val endTime = Calendar.getInstance() // time in future

            endTime[Calendar.HOUR_OF_DAY] = 17
            endTime[Calendar.MINUTE] = 28
            endTime[Calendar.SECOND] = 0

            val now = Date()
            val elapsed = now.time - endTime.timeInMillis
            val remainingTime = startTime - elapsed
            Log.e("remaining time ", (remainingTime).toString())
            notificationView.setChronometerCountDown(R.id.timerView, true)
            notificationView.setChronometer(R.id.timerView, remainingTime, "%tM:%tS", true)

            //notificationView.setChronometer(R.id.timerView,doStart(),null,true)

            ContextCompat.getMainExecutor(this).execute {
                val h = Handler()
                val delayInMilliseconds = ((remainingTime / 1000))
                Log.e("remaining time ", (delayInMilliseconds).toString())
                h.postDelayed({ mNotificationManager.cancelAll() }, delayInMilliseconds)
            }
            notificationView.setTextColor(R.id.description, resources.getColor(R.color.white))
            notificationView.setTextColor(R.id.titleNotification, resources.getColor(R.color.white))
            notificationView.setTextColor(R.id.timerView, resources.getColor(R.color.white))
            if (params.containsKey("title")) {
                notificationView.setTextViewText(R.id.titleNotification, params.getValue("title"))
            } else {
                notificationView.setTextViewText(R.id.titleNotification, "title")
            }
            if (params.containsKey("des_data")) {
                notificationView.setTextViewText(R.id.description, params.getValue("des_data"))
            } else {
                notificationView.setTextViewText(R.id.description, "")
            }
            if (params.containsKey("color_code")) {
                notificationView.setInt(R.id.layout,
                    "setBackgroundColor", Color.parseColor(params.getValue("color_code")))
            } else {
                notificationView.setInt(R.id.layout,
                    "setBackgroundColor", Color.parseColor("#000000"))
            }
            notificationBuilder.setCustomContentView(notificationView)
        }
        //    notificationBuilder.setContent(notificationView)


        x = Random().nextInt(101)
        mNotificationManager.notify(101, notificationBuilder.build())

        //  sendNotification()
        //   super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        Log.e("NEW_TOKEN", token)
        if (token.isNotEmpty()) {
            Log.e("NEW_TOKEN", token)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onChronometerTickHandler() {
        if (counter < 0) {
            counter = 10
        }
        chronometerCountDown.text = counter.toString() + ""
        counter--
    }


    private fun doStart() {
        chronometerCountDown.start()
    }
    /* private fun sendNotification() {
         val intent = Intent(applicationContext, NotificationActivity::class.java)
         intent.flags = FLAG_ACTIVITY_NEW_TASK
         startActivity(intent)
     }*/
}
