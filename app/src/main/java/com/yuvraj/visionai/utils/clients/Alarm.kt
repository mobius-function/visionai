package com.yuvraj.visionai.utils.clients

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import com.yuvraj.visionai.service.alarmReceiver.AlarmReceiver

class Alarm {
    companion object{
        private fun Activity.setAlarm(alarmTime: String) {
            // Create a Calendar instance for the alarm time
            val calendar = Calendar.getInstance().apply {
                val time = alarmTime.split(":").map { it.toInt() }
                set(Calendar.HOUR_OF_DAY, time[0])
                set(Calendar.MINUTE, time[1])
                set(Calendar.SECOND, 0)
            }

            // Create a PendingIntent to start the AlarmReceiver when the alarm triggers
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra("alarmTime", alarmTime)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

            // Schedule the alarm
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
        }
    }
}