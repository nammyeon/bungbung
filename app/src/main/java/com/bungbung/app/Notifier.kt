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
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bungbung.app.ui.PixelArt
import com.bungbung.app.ui.Sprite
import com.bungbung.app.ui.bungSprite
import com.bungbung.core.Mood

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

    fun popup(title: String, text: String, regionId: String?) {
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
            .setLargeIcon(faceBitmap(regionId))
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

    /** 알림 아이콘용 도트 붕붕이(+ 얻은 아이템). */
    private fun faceBitmap(regionId: String?): Bitmap {
        val size = 192
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = 0xFFB9C7A0.toInt()
        c.drawCircle(size / 2f, size / 2f, size / 2f, p)
        val dot = 6f
        val body = bungSprite(Mood.HAPPY)
        val x = (size - body.width * dot) / 2
        val y = size * 0.42f
        drawSprite(c, p, body, x, y, dot)
        regionId?.let { PixelArt.items[it] }?.let { drawSprite(c, p, it, x + 5.5f * dot, y - 7.5f * dot, dot) }
        return bmp
    }

    private fun drawSprite(c: Canvas, p: Paint, s: Sprite, x: Float, y: Float, dot: Float) {
        s.rows.forEachIndexed { r, row ->
            row.forEachIndexed { col, ch ->
                p.color = when (ch) {
                    '#' -> 0xFF2E3A2B.toInt()
                    'o' -> 0xFF7E8F6C.toInt()
                    else -> return@forEachIndexed
                }
                c.drawRect(x + col * dot, y + r * dot, x + (col + 1) * dot - 1, y + (r + 1) * dot - 1, p)
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "bung_moments"
    }
}
