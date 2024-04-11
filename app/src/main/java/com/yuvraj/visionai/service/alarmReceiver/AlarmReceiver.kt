package com.yuvraj.visionai.service.alarmReceiver
//
//import android.app.AlarmManager
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.icu.util.Calendar
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//
//class AlarmReceiver : BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        val alarmTime = intent.getStringExtra("alarmTime")
//        sendAlarmNotification(context, alarmTime)
//
//        Log.d("TAG", "onReceive: Alarm is triggered")
//
//        // DEBUG: Alarm is triggered
//        // Set the alarm again
//        setAlarmAgain(1, context)
//    }
//
//    private fun playAlarmSound(context: Context) {
////        val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
////        val alarmSound = RingtoneManager.getRingtone(context, alarmSoundUri)
////        alarmSound.play()
//    }
//
//
//    private fun sendAlarmNotification(context: Context, alarmTime: String?) {
//        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
//            .setContentTitle("Alarm")
//            .setContentText("$alarmTime has been triggered!")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "alarm_channel",
//                "Alarm channel",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0, notificationBuilder.build())
//    }
//
//
//    private fun setAlarmAgain(alarmTime: Int, context: Context) {
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
//        Log.d("TAG", "Alarm is set for ${calendar.time} with alarm time $alarmTime hours and ${calendar.timeInMillis / 1000} seconds")
//    }
//}