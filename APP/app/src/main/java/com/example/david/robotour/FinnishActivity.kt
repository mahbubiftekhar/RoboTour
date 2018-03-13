package com.example.david.robotour

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.speech.tts.TextToSpeech
import android.support.v4.content.res.ResourcesCompat
import kotlinx.android.synthetic.*
import java.io.File
import java.util.*


@Suppress("DEPRECATION")
class FinishActivity : AppCompatActivity() {
    /*This activity will be shown to the user when they cancel or finish the tour */
    private lateinit var closeApp: String
    private lateinit var restartApp: String
    private var tts: TextToSpeech? = null
    private var speechText = ""

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
    fun onInit(status: Int) {
        println("status code: $status")
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val language = intent.getStringExtra("language")
            val result: Int
            when (language) {
                "French" -> {
                    result = tts!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts!!.setLanguage(Locale.UK)
                }
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {

        }
    }
    override fun onStop() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onStop()
    }

    override fun onPause() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onPause()
    }

    override fun onBackPressed() {
        //Restart the app cleanly
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
        }
    }

    private fun deleteDir(dir: File): Boolean {
        return when {
            dir.isDirectory -> {
                val children = dir.list()
                children.indices
                        .map { deleteDir(File(dir, children[it])) }
                        .filterNot { it }
                        .forEach { return false }
                dir.delete()
            }
            dir.isFile -> dir.delete()
            else -> false
        }
    }
    private fun speakOutThanks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            when (language) {
                "French" -> {
                    text = "Merci d'utiliser RoboTour"
                }
                "Chinese" -> {
                    text = "感谢您使用RoboTour"
                }
                "Spanish" -> {
                    text = "Gracias por usar RoboTour"
                }
                "German" -> {
                    text = "Vielen Dank für die Verwendung von RoboTour"
                }
                else -> {
                    text = "Thanks for using Ro-bow-Tour"
                    //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
                }
            }
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val language = intent.getStringExtra("language")
        val message: String
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        tts = TextToSpeech(this, null)
        onInit(0)
        async{
            Thread.sleep(1500)
            speakOutThanks()
        }
        message = when (language) {
            "French" -> "Merci d'utiliser RoboTour.\nNous espérons que vous avez apprécié votre visite."
            "German" -> "Vielen Dank für die Verwendung von RoboTour.\nWir hoffen, Sie haben Ihre Tour genossen."
            "Spanish" -> "Gracias por usar RoboTour.\nEsperamos que hayas disfrutado tu recorrido."
            "Chinese" -> "感谢您使用萝卜途\n希望您喜欢这次旅程"
            else -> "Thank you for using RoboTour.\nWe hope you enjoyed your tour."
        }
        when (language) {
            "French" -> {
                speechText = "Thank you for using Ro-bow-tour"
                restartApp = "START"
                closeApp = "FERMER APP"
            }
            "German" -> {
                restartApp = "ANFANG"
                closeApp = "SCHLIEßE APP"
            }
            "Spanish" -> {
                restartApp = "COMIENZO"
                closeApp = "CERRAR APP"
            }
            "Chinese" -> {
                restartApp = "重新开始"
                closeApp = "关闭"
            }
            else -> {
                restartApp = "START"
                closeApp = "CLOSE APP"
            }
        }

        verticalLayout {
            imageView(R.drawable.robotour_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                onClick {

                }
            }
            textView {
                textSize = 24f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = Gravity.CENTER
                text = message
                setTextColor(resources.getColor(R.color.roboTourTeal))
            }
            button(restartApp) {
                textSize = 20f
                background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                onClick {
                    //Restart the app cleanly
                    deleteCache(applicationContext)
                    val i = baseContext.packageManager
                            .getLaunchIntentForPackage(baseContext.packageName)
                    i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                }
            }
            verticalLayout {
                button {
                    textSize = 20f
                    background = ColorDrawable(Color.parseColor("#FFFFFF"))
                }
            }
            button(closeApp) {
                textSize = 20f
                background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                onClick {
                    //Kill the app
                    clearFindViewByIdCache()
                    val closeTheApp = Intent(Intent.ACTION_MAIN)
                    closeTheApp.addCategory(Intent.CATEGORY_HOME)
                    closeTheApp.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(closeTheApp)
                }
            }
        }
    }
}