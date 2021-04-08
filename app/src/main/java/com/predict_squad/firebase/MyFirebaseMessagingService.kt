package com.predict_squad.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.predict_squad.MainActivity
import com.predict_squad.common_helper.BundleKey
import com.predict_squad.common_helper.DefaultHelper.decrypt
import com.predict_squad.R
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var bitmap: Bitmap? = null
    private var title: String = ""
    private var description: String = ""
    private var image: String = ""
    private var notificationType: String = ""
    private var matchId: String = ""
    private var externalUrl: String = ""

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            if (remoteMessage.data.getValue("title").isNotEmpty()) {
                title = decrypt(remoteMessage.data.getValue("title"))
            }

            if (remoteMessage.data.getValue("body").isNotEmpty()) {
                description = decrypt(remoteMessage.data.getValue("body"))
            }

            if (remoteMessage.data.getValue("notification_type").isNotEmpty()) {
                notificationType = decrypt(remoteMessage.data.getValue("notification_type"))
            }
            if (remoteMessage.data.getValue("match_id").isNotEmpty()) {
                matchId = remoteMessage.data.getValue("match_id")
            }


            if (remoteMessage.data.getValue("image").isNotEmpty()) {
                image = remoteMessage.data.getValue("image")
                //val notificationType = "0"
                bitmap = getBitmapFromUrl(decrypt(image))
            }

            if (remoteMessage.data.getValue("url").isNotEmpty()) {
                externalUrl = decrypt(remoteMessage.data.getValue("url"))
            }

            if (bitmap != null) {
                showImageNotification(bitmap, title, description, notificationType, externalUrl, matchId)
            } else {
                sendNotification(
                    title, description, notificationType, externalUrl, matchId
                )
            }

            /*if (*//* Check if data needs to be processed by long running job *//* true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }*/
        }
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

    }


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(
        title: String,
        description: String,
        notificationType: String,
        externalUrl: String,
        matchId: String,
    ) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(BundleKey.NotificationType.toString(), notificationType)
        intent.putExtra(BundleKey.MatchId.toString(), matchId)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )//PendingIntent.FLAG_ONE_SHOT

        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(decrypt(title)).setContentText(decrypt(description)).setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        /*val random = Random()
        val notificationId = random.nextInt(9999 - 1000) + 1000*/
        val notificationId = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun showImageNotification(
        bitmap: Bitmap?, title: String, message: String, notificationType: String, externalUrl: String, matchId: String
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(BundleKey.NotificationType.toString(), notificationType)
        intent.putExtra(BundleKey.MatchId.toString(), matchId)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )//PendingIntent.FLAG_ONE_SHOT

        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
        bigPictureStyle.bigPicture(bitmap)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, channelId).setTicker(title).setWhen(0).setAutoCancel(true).setContentTitle(decrypt(title)).setContentText(decrypt(message)).setContentIntent(pendingIntent).setSound(defaultSoundUri).setStyle(bigPictureStyle).setColor(resources.getColor(R.color.colorPrimary)).setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)).setContentText(message)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = message
            notificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        val notificationId = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
        notificationManager.notify(notificationId, mBuilder.build())
    }


    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL("" + imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            null
        }
    }


}
