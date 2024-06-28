package com.yuvraj.visionai.utils.clients

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.yuvraj.visionai.service.alarmReceiver.Notification
import com.yuvraj.visionai.service.alarmReceiver.channelID
import com.yuvraj.visionai.service.alarmReceiver.messageExtra
import com.yuvraj.visionai.service.alarmReceiver.notificationID
import com.yuvraj.visionai.service.alarmReceiver.titleExtra
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.Constants.DEFAULT_REGULAR_REMINDER_TIME
import com.yuvraj.visionai.utils.helpers.Permissions.allPermissionsGranted

object NotificationHelper {
    fun Activity.isRegularReminderEnabled() : Boolean {
        val sharedPreferences = this.getSharedPreferences(
            Constants.NOTIFICATION_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean(Constants.REGULAR_REMINDER, false)
    }

    fun Activity.getRegularReminderTime() : Int {
        val sharedPreferences = this.getSharedPreferences(
            Constants.NOTIFICATION_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getInt(Constants.REGULAR_REMINDER_TIME,
            DEFAULT_REGULAR_REMINDER_TIME)
    }

    fun Activity.setRegularReminderTime(time: Int) {
        val sharedPreferences = this.getSharedPreferences(
            Constants.NOTIFICATION_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putInt(Constants.REGULAR_REMINDER_TIME, time)
        sharedPreferencesEditor.apply()
    }


    fun Activity.scheduleRegularNotification(){
        if(isRegularReminderEnabled()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                Log.d("NotificationDebug",
                    "initViews: Notification channel is not created")
                Log.d("NotificationDebug",
                    "Build version is ${Build.VERSION.SDK_INT} and Name is ${Build.VERSION.CODENAME}")
            }
            scheduleNotification(getRegularReminderTime() * 60)

            Log.d("NotificationDebug",
                "Notification scheduled after ${getRegularReminderTime()} hours.")
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    fun Activity.scheduleNotification(alarmTimeInMinutes: Int) {
        // checking whether the SCHEDULE_EXACT_ALARM permission has been granted.


        // Create an intent for the Notification BroadcastReceiver
        val intent = Intent(applicationContext, Notification::class.java)

        // Extract title and message from user input
        val title = "Regular Notification Title"
        val message = "Regular Notification Message"

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

        // Check if the device can schedule exact alarms (For Android 14 and above)
        if(Build.VERSION.SDK_INT >= 31){
            if(alarmManager.canScheduleExactAlarms()) {
                // Get the selected time and schedule the notification
                val time = getTime(alarmTimeInMinutes)

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time.timeInMillis,
                    pendingIntent
                )

                Log.d("NotificationDebug", "NotificationHelper is set for ${time.time} " +
                        "with alarm time $alarmTimeInMinutes minutes " +
                        "and ${time.timeInMillis / 1000} seconds")
            } else {
                startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun areNotificationPermissionsGranted(context: Context): Boolean {

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