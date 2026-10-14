package com.radheradhe.ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var output: TextView
    private lateinit var tts: TextToSpeech
    private lateinit var apiKey: EditText
    private lateinit var prompt: EditText
    private val voiceRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildScreen()
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 2001)
        }
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(0xFFFFF8E1.toInt())
        }
        val title = TextView(this).apply {
            text = "🙏 Radhe Radhe AI"
            textSize = 28f
            gravity = Gravity.CENTER
            setTextColor(0xFF4A148C.toInt())
            setPadding(0, 12, 0, 16)
        }
        val subtitle = TextView(this).apply {
            text = "Voice assistant • Internet AI"
            textSize = 15f
            gravity = Gravity.CENTER
        }
        apiKey = EditText(this).apply {
            hint = "DeepSeek API key (optional)"
            singleLine = true
            inputType = 129
        }
        prompt = EditText(this).apply {
            hint = "Type a message or tap Speak"
            minLines = 2
            gravity = Gravity.TOP
        }
        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val speak = Button(this).apply { text = "🎙 Speak"; setOnClickListener { startVoiceInput() } }
        val ask = Button(this).apply { text = "Ask AI"; setOnClickListener { askAssistant(prompt.text.toString()) } }
        buttons.addView(speak, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        buttons.addView(ask, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        val scroll = ScrollView(this)
        output = TextView(this).apply {
            text = "Namaste! Radhe Radhe 🙏\n\nSpeak or type a message. For online AI answers, add your own DeepSeek API key above."
            textSize = 17f
            setTextColor(0xFF212121.toInt())
            setPadding(8, 20, 8, 20)
        }
        scroll.addView(output)
        root.addView(title)
        root.addView(subtitle)
        root.addView(apiKey)
        root.addView(prompt)
        root.addView(buttons)
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
    }

    private fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Voice recognition is not available on this device.", Toast.LENGTH_LONG).show(); return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Boliye, Radhe Radhe AI sun raha hai")
        }
        try { startActivityForResult(intent, voiceRequestCode) }
        catch (_: Exception) { Toast.makeText(this, "Voice input could not start.", Toast.LENGTH_LONG).show() }
    }

    @Deprecated("Deprecated by Android, retained for broad compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == voiceRequestCode && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = matches?.firstOrNull().orEmpty()
            prompt.setText(spoken)
            if (spoken.isNotBlank()) askAssistant(spoken)
        }
    }

    private fun askAssistant(question: String) {
        if (question.isBlank()) { Toast.makeText(this, "Pehle message bolo ya type karo.", Toast.LENGTH_SHORT).show(); return }
        output.text = "You: $question\n\nRadhe Radhe AI: Soch raha hoon..."
        val key = apiKey.text.toString().trim()
        if (key.isBlank()) {
            val answer = localReply(question)
            showAnswer(question, answer)
            return
        }
        Thread {
            val answer = try {
                val conn = (java.net.URL("https://api.deepseek.com/chat/completions").openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 15000
                    readTimeout = 30000
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer $key")
                    doOutput = true
                }
                val safeQuestion = question.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
                val body = """{"model":"deepseek-chat","messages":[{"role":"system","content":"You are Radhe Radhe AI, a helpful Android voice assistant. Reply simply in the language the user uses."},{"role":"user","content":"$safeQuestion"}]}"""
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
                val raw = stream.bufferedReader().use { it.readText() }
                if (conn.responseCode !in 200..299) "AI API error (${conn.responseCode}). Check API key and internet."
                else Regex("\\\"content\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"").find(raw)?.groupValues?.get(1)
                    ?.replace("\\n", "\n")?.replace("\\\"", "\"")?.replace("\\\\", "\\") ?: "AI response parse nahi ho paya."
            } catch (e: Exception) { "Internet/API error: ${e.message ?: "connection failed"}" }
            runOnUiThread { showAnswer(question, answer) }
        }.start()
    }

    private fun localReply(question: String): String {
        val q = question.lowercase(Locale.ROOT)
        return when {
            "radhe radhe" in q -> "Radhe Radhe! 🙏 Main aapka assistant hoon. Online AI ke liye DeepSeek API key add karein."
            "time" in q || "samay" in q -> "Phone par current time dekh sakte hain. Online AI ke liye API key add karein."
            "hello" in q || "hi" in q || "namaste" in q -> "Namaste! Bataiye, main kis tarah madad karun?"
            else -> "Abhi basic mode chal raha hai. Online intelligent answers ke liye DeepSeek API key add karke Ask AI dabayein."
        }
    }

    private fun showAnswer(question: String, answer: String) {
        output.text = "You: $question\n\nRadhe Radhe AI: $answer"
        if (::tts.isInitialized) tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "radhe_answer")
    }

    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts.language = Locale("hi", "IN") }
    override fun onDestroy() { if (::tts.isInitialized) { tts.stop(); tts.shutdown() }; super.onDestroy() }
}
