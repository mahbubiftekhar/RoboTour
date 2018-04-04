package com.example.david.robotour

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.*
import org.jetbrains.anko.*
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException

class WaitingActivity : AppCompatActivity() {
    private var user = 1
    private var language = ""
    private var message = ""
    private var imageView: ImageView? = null
    private var descriptionView: TextView? = null
    private var textView2: TextView? = null
    private var transfered = true
    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        return sharedPreferences.getInt(key, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        language = intent.getStringExtra("language") //Getting the language from the previous activity
        user = loadInt("user") //Set the user number

        message = when (language) {
            "German" -> {
                "Senden Sie Ihre Auswahl an RoboTour\n"
            }
            "French" -> {
                "Envoi de votre sélection à RoboTour\n"
            }
            "Spanish" -> {
                "Envío de su selección a RoboTour\n"
            }
            "Chinese" -> {
                "将您的选择发送到RoboTour\n"
            }
            else -> {
                "Sending your selection to RoboTour"
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        verticalLayout {
            webView {
                loadUrl("file:///android_asset/robotour_spinning_grey.gif")
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
            textView2 = textView {
                textSize = 34f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = CENTER
                text = message
            }
            imageView = imageView {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams {
                topMargin = dip(20)
            }
            descriptionView = textView {
                text = ""
                textSize = 20f
                typeface = Typeface.DEFAULT
                this.gravity = CENTER
                padding = dip(10)
            }
            background = ColorDrawable(Color.parseColor("#EEEEEE"))
        }
        async {
            Thread.sleep(3000)
            val a = URL(url).readText()
            uiThread {
                if (a[18] == 'F' && user == 1) {
                    println(">>>>>in here first if")
                    startActivity<NavigatingActivity>("language" to language)
                } else {
                    runOnUiThread {
                        println(">>>>>in here starting thread")
                        updateText()
                        startThread()
                    }
                }
            }
        }
    }

    private fun startThread() {
        if (t.state == Thread.State.NEW) {
            t.start()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateText() {
        when (language) {
            "German" -> {
                textView2?.text = "Bitte Warten Sie Auf den Anderen User..."
            }
            "French" -> {
                textView2?.text = "Veuillez Attendre l'Autre Utilisateur..."
            }
            "Spanish" -> {
                textView2?.text = "Por Favor Espere al Otro Usuario..."
            }
            "Chinese" -> {
                textView2?.text = "请等待其他用户..."
            }
            else -> {
                textView2?.text = "Please Wait For the Other User..."
            }
        }
    }

    fun switchToNavigate() {
        t.interrupt()
        pictureThread.interrupt()
        interruptAllThreads() //Interrupt all the threads
        clearFindViewByIdCache()
        startActivity<NavigatingActivity>("language" to language) // now we can switch the activity
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
        toast(message)
    }

    private val t: Thread = object : Thread() {
        /*This thread will check if the other use has made their selection*/
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                println("++++ t thread WaitingActivity defo")
                try {
                    val a = URL(url).readText()
                    if (a[16] == 'T' && a[17] == 'T' && transfered) {
                        transfered = false
                        switchToNavigate()
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedByTimeoutException) {
                    Thread.currentThread().interrupt()
                }
                try {
                    Thread.sleep(750)
                } catch (e: InterruptedException) {
                }
            }
            Thread.currentThread().interrupt()
        }
    }

    private fun interruptAllThreads() {
        //This function interrupts all the threads
        pictureThread.interrupt()
        t.interrupt()
    }

    override fun onDestroy() {
        t.interrupt()
        if (pictureThread.state == Thread.State.RUNNABLE) {
            pictureThread.interrupt()
        }
        super.onDestroy()
    }


    override fun onResume() {
        super.onResume()
        if (pictureThread.state == Thread.State.NEW) {
            pictureThread.start()
        }
    }

    private val pictureThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        var a = 0

        override fun run() {
            while (!isInterrupted) {
                println("++++ picture thread WaitingActivity")
                if (a > 9) {
                    //Reset A to avoid null pointers
                    a = 0
                }
                try {
                    //UI thread MUST be updates on the UI thread, other threads may not update the UI thread
                    runOnUiThread {
                        imageView?.setImageResource(allArtPieces[a].imageID)
                        descriptionView?.text = allArtPieces[a].name
                        when (language) {
                            "French" -> {
                                descriptionView?.text = allArtPieces[a].nameFrench

                            }
                            "German" -> {
                                descriptionView?.text = allArtPieces[a].nameGerman

                            }
                            "Spanish" -> {
                                descriptionView?.text = allArtPieces[a].nameSpanish

                            }
                            "Chinese" -> {
                                descriptionView?.text = allArtPieces[a].nameChinese

                            }
                            else -> {
                                descriptionView?.text = allArtPieces[a].name
                            }
                        }
                    }
                    Thread.sleep(1500)
                    a++
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                }
            }
            Thread.currentThread().interrupt()
        }
    }
}