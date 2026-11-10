package com.bungbung.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * 내비 화면 위에 잠깐 뜨는 상단 알림(헤드업 알림).
 * 도감 획득·레벨업처럼 특별한 순간에만 띄우고, 한 줄 문구 + 붕붕이 얼굴 이미지로 구성한다.
 */
class Notifier(private val context: Context) {
    private var nextId = 100

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = context.getString(R.string.channel_desc) }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun popup(title: String, text: String, emoji: String?) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bung_small)
            .setLargeIcon(faceBitmap(emoji))
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setTimeoutAfter(6_000)
            .setContentIntent(open)
            .build()
        NotificationManagerCompat.from(context).notify(nextId++, notification)
    }

    /** 알림 아이콘용 붕붕이 얼굴. */
    private fun faceBitmap(emoji: String?): Bitmap {
        val size = 192
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = 0xFFFFF1C7.toInt()
        c.drawCircle(size / 2f, size / 2f, size / 2f, p)
        p.color = 0xFFFFC93C.toInt()
        c.drawRoundRect(RectF(36f, 56f, 156f, 150f), 36f, 36f, p)
        p.color = 0xFF2B2D42.toInt()
        c.drawCircle(76f, 96f, 9f, p)
        c.drawCircle(116f, 96f, 9f, p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 6f
        p.strokeCap = Paint.Cap.ROUND
        c.drawArc(RectF(82f, 102f, 110f, 124f), 20f, 140f, false, p)
        p.style = Paint.Style.FILL
        c.drawCircle(64f, 156f, 14f, p)
        c.drawCircle(128f, 156f, 14f, p)
        if (emoji != null) {
            p.textSize = 54f
            p.textAlign = Paint.Align.CENTER
            c.drawText(emoji, size / 2f, 58f, p)
        }
        return bmp
    }

    companion object {
        const val CHANNEL_ID = "bung_moments"
    }
}
