package com.yuvraj.visionai.utils.clients

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
//import com.yuvraj.visionai.service.alarmReceiver.AlarmReceiver
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.yuvraj.visionai.service.alarmReceiver.Notification
import com.yuvraj.visionai.service.alarmReceiver.channelID
import com.yuvraj.visionai.service.alarmReceiver.messageExtra
import com.yuvraj.visionai.service.alarmReceiver.notificationID
import com.yuvraj.visionai.service.alarmReceiver.titleExtra

object NotificationHelper {
//    fun setAlarm(alarmTime: Int, context: Context) {
//        // Create a Calendar instance for the alarm time
//        val calendar = Calendar.getInstance().apply {
////                val time = alarmTime.split(":").map { it.toInt() }
//            val currentHour = get(Calendar.HOUR_OF_DAY)
//            val currentMinute = get(Calendar.MINUTE)
//            set(Calendar.HOUR_OF_DAY, currentHour)
//            set(Calendar.MINUTE, currentMinute + alarmTime)
//            set(Calendar.SECOND, 0)
//        }
//
//        // Create a PendingIntent to start the AlarmReceiver when the alarm triggers
//        val intent = Intent(context, AlarmReceiver::class.java)
//        intent.putExtra("alarmTime", alarmTime.toString())
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        // Schedule the alarm
//        AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
//        Log.d("TAG", "NotificationHelper is set for ${calendar.time} with alarm time $alarmTime minutes and ${calendar.timeInMillis / 1000} seconds")
//    }


    @SuppressLint("ScheduleExactAlarm")
    fun Activity.scheduleNotification(alarmTime: Int) {
        // Create an intent for the Notification BroadcastReceiver
        val intent = Intent(applicationContext, Notification::class.java)

        // Extract title and message from user input
        val title = "Notification Title"
        val message = "Notification Message"

        // Add title and message as extras to the intent
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        // Create a PendingIntent for the broadcast
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Get the AlarmManager service
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Get the selected time and schedule the notification
        val time = getTime(alarmTime)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.timeInMillis,
            pendingIntent
        )

        Log.d("TAG", "NotificationHelper is set for ${time.time} with alarm time $alarmTime minutes and ${time.timeInMillis / 1000} seconds")

        // Show an alert dialog with information
        // about the scheduled notification
//        showAlert(time.timeInMillis, title, message)
    }

//    private fun Activity.showAlert(time: Long, title: String, message: String) {
//        // Format the time for display
//        val date = Date(time)
//        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
//        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)
//
//        // Create and show an alert dialog with notification details
//        AlertDialog.Builder(this)
//            .setTitle("Notification Scheduled")
//            .setMessage(
//                "Title: $title\nMessage: $message\nAt: ${dateFormat.format(date)} ${timeFormat.format(date)}"
//            )
//            .setPositiveButton("Okay") { _, _ -> }
//            .show()
//    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun Activity.createNotificationChannel() {
        // Create a notification channel for devices running
        // Android Oreo (API level 26) and above
        val name = "Notify Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc

        // Get the NotificationManager service and create the channel
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun checkNotificationPermissions(context: Context): Boolean {
        // Check if notification permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val isEnabled = notificationManager.areNotificationsEnabled()

            if (!isEnabled) {
                // Open the app notification settings if notifications are not enabled
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)

                return false
            }
        } else {
            val areEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

            if (!areEnabled) {
                // Open the app notification settings if notifications are not enabled
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)

                return false
            }
        }

        // Permissions are granted
        return true
    }
}