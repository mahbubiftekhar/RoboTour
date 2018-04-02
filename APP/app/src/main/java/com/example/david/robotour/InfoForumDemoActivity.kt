package com.example.david.robotour

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*
import java.io.InterruptedIOException
import java.net.URL
import java.util.*
import android.widget.TextView
import android.util.DisplayMetrics



@Suppress("DEPRECATION")
class InfoForumDemoActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var tts2: TextToSpeech? = null
    private var tts3: TextToSpeech? = null
    private var tts4: TextToSpeech? = null
    private var tts6: TextToSpeech? = null
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
        t.interrupt()
        super.onDestroy()
    }

    public override fun onStop() {
        //Shutdown TTS
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
        if (tts6 != null) {
            tts6!!.stop()
            tts6!!.shutdown()
        }
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.DONUT)
    override fun onInit(status: Int) {
        println("status code: $status")
        if (status == TextToSpeech.SUCCESS) {
            val result: Int = tts!!.setLanguage(Locale.UK)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {
        }
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result: Int = tts5!!.setLanguage(Locale.CHINESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {

        }
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result: Int
            val spanish = Locale("es", "ES")
            result = tts2!!.setLanguage(spanish)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {

        }
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result: Int = tts4!!.setLanguage(Locale.GERMAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {
        }
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result: Int = tts3!!.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {

        }

    }

    override fun onPause() {
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
        if (tts6 != null) {
            tts6!!.stop()
            tts6!!.shutdown()
        }
        super.onPause()
    }

    override fun onResume() {
        //This ensures that when the nav activity is minimized and reloaded up, the speech still works
        tts = TextToSpeech(this, this)
        tts2 = TextToSpeech(this, this)
        tts3 = TextToSpeech(this, this)
        tts4 = TextToSpeech(this, this)
        tts6 = TextToSpeech(this, this)
        tts5 = TextToSpeech(this, this)
        onInit(0)
        super.onResume()
        if (t.state == Thread.State.NEW) {
            t.start()
        }
    }

    private val t: Thread = object : Thread() {
        /*This thread will check if the other use has made their selection*/
        @RequiresApi(Build.VERSION_CODES.O)
        var last = -1

        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                println("++++ t thread WaitingActivity")
                try {
                    async {
                        val position = (URL("http://www.mahbubiftekhar.co.uk/forum.php").readText()[0]).toString().toInt()
                        if (position != last) {
                            println(">>> position $position")
                            println(">>> last $last")
                            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
                            //If not equal to the last, speak, this is to ensure the same command isnt repeated continously
                            runOnUiThread {
                                speakOut(position)
                            }
                            last = position
                        } else {
                        }
                    }
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {

                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                }

            }
            Thread.currentThread().interrupt()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vibrate()
        val metrics = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(metrics)

        val yInches = metrics.heightPixels / metrics.ydpi
        val xInches = metrics.widthPixels / metrics.xdpi
        val diagonalInches = Math.sqrt((xInches * xInches + yInches * yInches).toDouble())
        if (diagonalInches >= 6.5) {
            // 6.5inch device or bigger
            verticalLayout {
                imageView(R.drawable.robotourlarge) {
                    backgroundColor = Color.TRANSPARENT //Removes gray border
                }
                text2 = textView {
                    textSize = 62f
                    typeface = Typeface.DEFAULT_BOLD
                    padding = dip(5)
                    topPadding = dip(20)
                    gravity = Gravity.CENTER
                }
            }
        } else {
            // smaller device
            verticalLayout {
                imageView(R.drawable.robotour_img_small) {
                    backgroundColor = Color.TRANSPARENT //Removes gray border
                }
                text2 = textView {
                    textSize = 40f
                    typeface = Typeface.DEFAULT_BOLD
                    padding = dip(5)
                    topPadding = dip(20)
                    gravity = Gravity.CENTER
                }
            }
        }

    }

    fun updateTextView2(text: String) {
        text2?.text = text
    }

    private fun speakOut(position: Int) {
        when (position) {
            0 -> {
                updateTextView2("Welcome to RoboTour")
                tts!!.speak("Welcome to RoboTour", TextToSpeech.QUEUE_FLUSH, null)
            }
            1 -> {
                //Spanish
                updateTextView2("Bienvenido a RoboTour")
                val e = "Bienvenido a RoboTour"
                tts2!!.speak(e, TextToSpeech.QUEUE_FLUSH, null)
            }
            2 -> {
                //German
                val e = "Willkommen bei RoboTour"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts3!!.speak(e, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    tts3!!.speak(e, TextToSpeech.QUEUE_FLUSH, null)
                }
                updateTextView2("Willkommen bei RoboTour")
            }
            3 -> {
                //French
                updateTextView2("Bienvenue sur RoboTour")
                tts4!!.speak("Bienvenue sur RoboTour", TextToSpeech.QUEUE_FLUSH, null)
            }
            4 -> {
                //Chinese
                updateTextView2("欢迎来到RoboTour")
                tts5!!.speak("欢迎来到RoboTour", TextToSpeech.QUEUE_FLUSH, null)
            }
        }

    }

    override fun onBackPressed() {
        alert("Are you sure?") {
            positiveButton {
                super.onBackPressed()
            }
            negativeButton {

            }
        }.show()
    }


    private fun vibrate() {
        if (Build.VERSION.SDK_INT > 25) { /*Attempt to not use the deprecated version if possible, if the SDK version is >25, use the newer one*/
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(300, 10))
        } else {
            /*for backward comparability*/
            @Suppress("DEPRECATION")
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(300)
        }
    }
}