package com.example.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dicodingevent.R
import com.example.dicodingevent.data.remote.api.ApiConfig
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.data.remote.response.EventResponse
import com.example.dicodingevent.ui.detail.DetailActivity
import retrofit2.Call
import retrofit2.Response

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "Dicoding channel"
        const val EXTRA_NAME = "extra_name"
    }

    private var resultStatus: Result? = null

    override fun doWork(): Result {
        return getUpcomingEvent()
    }

    private fun getUpcomingEvent(): Result {
        val client = ApiConfig.getApiService().getUpcomingEvent()
        client.enqueue(object : retrofit2.Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val eventList = response.body()?.listEvents
                    if(eventList == null) {
                        resultStatus = Result.failure()
                    } else {
                        val event = eventList[eventList.size-1]
                        showNotification(event)
                        resultStatus = Result.success()
                    }
                } else {
                    resultStatus = Result.failure()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                resultStatus = Result.failure()
            }
        })
        return resultStatus as Result
    }

    private fun showNotification(event: EventItem) {
        val notifyDetailIntent = Intent(applicationContext, DetailActivity::class.java)
        notifyDetailIntent.putExtra(DetailActivity.EXTRA_INDEX, event.id)
        notifyDetailIntent.putExtra(DetailActivity.EXTRA_TITLE, event.name)

        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(notifyDetailIntent)
            getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification =  NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(event.ownerName)
            .setContentText(event.name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSubText("Upcoming event")
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${event.name} will be held at ${event.beginTime} WIB"))
            .setAutoCancel(true)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

}