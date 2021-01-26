package com.sambishopp.securenotes.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sambishopp.securenotes.R
import com.sambishopp.securenotes.activities.LoginActivity
import com.sambishopp.securenotes.activities.MainActivity
import java.util.*

class AutoLogoutService: Service()
{
    private var lockTimer = Timer()
    private val appLockTimer: Long = 300000

    private val nChannelId = "Logout Service Notification"

    override fun onCreate()
    {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val serviceNotification = NotificationCompat.Builder(this, nChannelId)
            .setContentTitle("Secure Notes")
            .setContentText("App will auto lock soon.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVibrate(null)
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

        }, appLockTimer)
    }

    private fun resetTimer()
    {
        lockTimer.cancel()
        lockTimer = Timer()

        lockTimer.schedule(object: TimerTask()
        {
            override fun run()
            {
                logoutUser()
            }
        }, appLockTimer)
    }

    private fun stopTimer()
    {
        lockTimer.cancel()
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