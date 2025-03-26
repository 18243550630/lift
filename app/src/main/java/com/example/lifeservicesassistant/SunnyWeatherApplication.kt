package com.example.lifeservicesassistant

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class SunnyWeatherApplication : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val TOKEN ="Vczm2e343HdWRIH4"

/*        lateinit var database: HealthTipDatabase
            private set*/
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
/*        database = Room.databaseBuilder(
            applicationContext,
            HealthTipDatabase::class.java,
            "health-db"
        ).build()*/
    }


}


fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Event Reminders"
        val descriptionText = "Channel for event reminder notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("event_reminder_channel", name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
