package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.TextView
import org.jetbrains.anko.*
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException
import java.util.*

@Suppress("DEPRECATION")
class InfoForumDemoActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var tts2: TextToSpeech? = null
    private var tts3: TextToSpeech? = null
    private var tts4: TextToSpeech? = null
    private var tts5: TextToSpeech? = null
    private var text2: TextView? = null


    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if (tts2 != null) {
            tts2!!.stop()
            tts2!!.shutdown()
        }
        if (tts3 != null) {
            tts3!!.stop()
            tts3!!.shutdown()
        }
        if (tts4 != null) {
            tts4!!.stop()
            tts4!!.shutdown()
        }
        if (tts5 != null) {
            tts5!!.stop()
            tts5!!.shutdown()
        }
        t.interrupt()
        super.onDestroy()
    }

    public override fun onStop() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if (tts2 != null) {
            tts2!!.stop()
            tts2!!.shutdown()
        }
        if (tts3 != null) {
            tts3!!.stop()
            tts3!!.shutdown()
        }
        if (tts4 != null) {
            tts4!!.stop()
            tts4!!.shutdown()
        }
        if (tts5 != null) {
            tts5!!.stop()
            tts5!!.shutdown()
        }
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.DONUT)
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts!!.setLanguage(Locale.ENGLISH)
            val spanish = Locale("es", "ES")
            tts2!!.setLanguage(spanish)
            tts3!!.setLanguage(Locale.GERMAN)
            tts4!!.language = Locale.FRENCH
            tts5!!.language = Locale.CHINESE
        }

    }

    override fun onResume() {
        tts = TextToSpeech(this, this)
        tts2 = TextToSpeech(this, this)
        tts3 = TextToSpeech(this, this)
        tts4 = TextToSpeech(this, this)
        tts5 = TextToSpeech(this, this)
        onInit(0)
        if (t.state == Thread.State.NEW) {
            t.start()
        }
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            imageView(R.drawable.robotour_img_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
            }
            text2 = textView {
                textSize = 34f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = Gravity.CENTER
            }
        }
    }


    fun updateTextView2(text: String) {
        text2?.text = text
    }

    fun speakOut(position: Int) {
        when (position) {
            0 -> {
                updateTextView2("Welcome to RoboTour")
                tts!!.speak("Welcome to RoboTour", TextToSpeech.QUEUE_FLUSH, null)
                //English
            }
            1 -> {
                //Spanish
                updateTextView2("Bienvenue sur RoboTour")
                val e = "Bienvenue sur RoboTour"
                tts2!!.speak(e, TextToSpeech.QUEUE_FLUSH, null)
            }
            2 -> {
                //German
                println(">>> before")
                val e = "Willkommen bei RoboTour"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts3!!.speak(e, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    tts3!!.speak(e, TextToSpeech.QUEUE_FLUSH, null)
                }
                println(">>> after")
                updateTextView2("Willkommen bei RoboTour")
            }
            3 -> {
                //French
                updateTextView2("Bienvenido a RoboTour")
                tts4!!.speak("Bienvenido a RoboTour", TextToSpeech.QUEUE_FLUSH, null)

            }
            4 -> {
                //Chinese
                updateTextView2("欢迎来到RoboTour")
                tts5!!.speak("欢迎来到RoboTour", TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }


    private val t: Thread = object : Thread() {
        /*This thread will check if the other use has made their selection*/
        @RequiresApi(Build.VERSION_CODES.O)
        var position = -1
        var last = -1
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                println("++++ t thread WaitingActivity")
                try {
                    val a = URL("http://www.mahbubiftekhar.co.uk/forum.php").readText()
                    position = a[0].toString().toInt()
                    if (position != last) {
                        println(">>> in else")
                        println(">>> position $position")
                        println(">>> last $last")
                        //If not equal to the last, speak, this is to ensure the same command isnt repeated continously
                        runOnUiThread {
                            speakOut(position)
                        }
                        last = position
                    } else {
                        println(">>> in else")
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                }
               try{
                   Thread.sleep(2000)
               } catch (e:Exception){

               }
            }
            Thread.currentThread().interrupt()
        }
    }
}
