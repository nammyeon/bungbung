package com.bungbung.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.bungbung.app.ui.bungBox
import com.bungbung.app.ui.drawBung
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

    /** 알림 아이콘용 붕붕이 얼굴(+ 얻은 아이템). 화면과 같은 그림 데이터를 비트맵에 그린다. */
    private fun faceBitmap(regionId: String?): Bitmap {
        val size = 192f
        val image = ImageBitmap(size.toInt(), size.toInt())
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(image), Size(size, size)) {
            drawCircle(Color(0xFFFFE3EA), radius = size / 2, center = Offset(size / 2, size / 2))
            val box = bungBox(cx = size / 2, feetY = size * 0.9f, height = size * 0.78f)
            drawBung(box, Mood.HAPPY, regionId, Color(0xFFFFE9A8))
        }
        return image.asAndroidBitmap()
    }

    companion object {
        const val CHANNEL_ID = "bung_moments"
    }
}
