package com.yuvraj.visionai.service.alarmReceiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.yuvraj.visionai.R

// Constants for notification
const val notificationID = 121
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

// BroadcastReceiver for handling notifications
class Notification : BroadcastReceiver() {

    // Method called when the broadcast is received
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("TAG", "onReceive: Notification is triggered")

        // Build the notification using NotificationCompat.Builder
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra)) // Set title from intent
            .setContentText(intent.getStringExtra(messageExtra)) // Set content text from intent
            .build()

        // Get the NotificationManager service
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Show the notification using the manager
        manager.notify(notificationID, notification)


        // Schedule next alarm
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        } else {
            Log.d("TAG", "initViews: Notification channel is not created")
            Log.d("TAG", "Build version is ${Build.VERSION.SDK_INT} and Name is ${Build.VERSION.CODENAME}")
        }
        scheduleNotification(1, context)

        //kill current broadcast receiver
        this.clearAbortBroadcast()

        Log.d("TAG", "onReceive: Broadcast is killed")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        // Create a notification channel for devices running
        // Android Oreo (API level 26) and above
        val name = "Notify Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc

        // Get the NotificationManager service and create the channel
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(alarmTime: Int, context: Context) {
        // Create an intent for the Notification BroadcastReceiver
        val intent = Intent(context.applicationContext, Notification::class.java)

        // Extract title and message from user input
        val title = "Notification Title"
        val message = "Notification Message"

        // Add title and message as extras to the intent
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        // Create a PendingIntent for the broadcast
        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Get the AlarmManager service
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Get the selected time and schedule the notification
        val time = getTime(alarmTime)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.timeInMillis,
            pendingIntent
        )

        Log.d("TAG", "NotificationHelper is set for ${time.time} with alarm time $alarmTime minutes and ${time.timeInMillis / 1000} seconds")
    }

    fun getTime(alarmTime: Int): Calendar {
        // Get selected time from TimePicker and DatePicker

//            val minute = binding.timePicker.minute
//            val hour = get(Calendar.HOUR_OF_DAY)
//            val day = binding.datePicker.dayOfMonth
//            val month = binding.datePicker.month
//            val year = binding.datePicker.year
//
//            // Create a Calendar instance and set the selected date and time
//            val calendar = Calendar.getInstance()
//            calendar.set(year, month, day, hour, minute)

        val calendar = Calendar.getInstance().apply {
//                val time = alarmTime.split(":").map { it.toInt() }
            val currentHour = get(Calendar.HOUR_OF_DAY)
            val currentMinute = get(Calendar.MINUTE)
            set(Calendar.HOUR_OF_DAY, currentHour)
            set(Calendar.MINUTE, currentMinute + alarmTime)
            set(Calendar.SECOND, 0)
        }


        return calendar
    }
}