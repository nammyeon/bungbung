package com.bungbung.app

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * 붕붕이의 목소리. 지금은 안드로이드 기본 TTS를 쓰고, Pleos TextToSpeech SDK로 바꿔 끼울 수 있게 한 곳에 모았다.
 * 괄호 안 안내 문구는 읽지 않는다.
 */
class Speaker(context: Context) : TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var pending: String? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        tts.language = Locale.KOREAN
        tts.setPitch(1.4f)
        tts.setSpeechRate(1.05f)
        ready = true
        pending?.let { speak(it) }
        pending = null
    }

    fun speak(text: String) {
        val spoken = text.replace(Regex("\\(.*?\\)"), "").trim()
        if (!ready) {
            pending = spoken
            return
        }
        tts.speak(spoken, TextToSpeech.QUEUE_FLUSH, null, "bung-${System.currentTimeMillis()}")
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
