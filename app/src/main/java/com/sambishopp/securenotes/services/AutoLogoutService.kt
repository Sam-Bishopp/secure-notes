package com.sambishopp.securenotes.services

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.view.View
import androidx.core.app.NotificationCompat
import com.sambishopp.securenotes.R
import com.sambishopp.securenotes.activities.LoginActivity
import com.sambishopp.securenotes.activities.MainActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AutoLogoutService: Service()
{
    private var lockTimer = Timer()
    private val appLockTime: Long = 600000 //5000
    private val dateFormatter: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private lateinit var nManager: NotificationManager
    private lateinit var nChannel: NotificationChannel
    private val nChannelId = "Logout Service Notification"
    private val nDescription = "Service to logout user"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val lockTimeMillis: Long = System.currentTimeMillis() + 600000
        val lockTime = dateFormatter.format(lockTimeMillis)

        nManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nChannel = NotificationChannel(
            nChannelId,
            nDescription,
            NotificationManager.IMPORTANCE_NONE
        )
        nChannel.enableVibration(false)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        nManager.createNotificationChannel(nChannel)

        val notificationBuilder = NotificationCompat.Builder(this, nChannelId)
        val serviceNotification = notificationBuilder.setOngoing(true)
            .setContentTitle("Auto-Logout Service")
            .setContentText("App will auto lock at " + lockTime)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        val serviceRunnable = Runnable { startUserSession() }
        val thread = Thread(serviceRunnable)
        thread.start()

        startForeground(1, serviceNotification)

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder?
    {
        return null
    }

    private fun startUserSession()
    {
            lockTimer.schedule(object : TimerTask()
            {
                override fun run()
                {
                    logoutUser()
                }

            }, appLockTime)
    }

    fun logoutUser()
    {
        val logoutIntent = Intent(this, LoginActivity::class.java)
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(logoutIntent)

        stopForeground(true)
        stopSelf()
    }
}