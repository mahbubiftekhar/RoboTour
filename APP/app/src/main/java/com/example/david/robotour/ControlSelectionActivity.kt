package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity.CENTER
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.*
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import java.io.IOException
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException

class ControlSelectionActivity : AppCompatActivity() {
    private var language = ""
    private var message = ""
    private var waitingForListen = false
    private var text2: TextView? = null

    private fun saveInt(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getInt(key, -1)
    }

    private fun sendPUTNEW(identifier: Int, command: String) {
        /*DISCLAIMER: When calling this function, if you don't run in an async, you will get
        * as security exception - just a heads up */
        val httpclient = DefaultHttpClient()
        val httPpost = HttpPost(url)
        try {
            val nameValuePairs = ArrayList<NameValuePair>(4)
            nameValuePairs.add(BasicNameValuePair("command$identifier", command))
            httPpost.entity = UrlEncodedFormEntity(nameValuePairs)
            httpclient.execute(httPpost)
        } catch (e: ClientProtocolException) {
        } catch (e: IOException) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        language = intent.getStringExtra("language") //Getting the language from the previous activity
        message = when (language) {
            "German" -> "Die nächste RoboTour finden\n"
            "French" -> "Trouver le RoboTour le plus proche\n"
            "Spanish" -> "Encontrar el RoboTour más cercano\n"
            "Chinese" -> "寻找最近的RoboTour\n"
            else -> "Finding closest RoboTour"
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        verticalLayout {
            webView {
                loadUrl("file:///android_asset/robotour_spinning_grey.gif")
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
            text2 = textView {
                textSize = 34f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = CENTER
                text = message
            }
            background = ColorDrawable(Color.parseColor("#EEEEEE"))
        }

        async {
            val a = URL(url).readText()
            if (a[24] == 'T') {
                println(">>>>>0")
                switchBackToMain()
            } else if (a[16] == 'F') {
                println(">>>>>1")
                sendPUTNEW(16, "T")
                saveInt("user", 1)
                Thread.sleep(4000)
                switchToPictures()
            } else if (a[17] == 'F') {
                println(">>>>>2")
                sendPUTNEW(17, "T")
                saveInt("user", 2)
                Thread.sleep(4000)
                switchToPictures()
            } else {
                println(">>>>>3")
                Thread.sleep(4000)
                if (t.state == Thread.State.NEW) {
                    t.start()
                }
            }
        }
    }


    private fun updatetext2() {

        when (language) {
            "German" -> {
                text2?.text = "RoboTour nicht verfügbar, bitte warten Sie, um der Tour zu folgen\n"
            }
            "French" -> {
                text2?.text = "RoboTour non disponible, veuillez patienter pour le tour\n"
            }
            "Spanish" -> {
                text2?.text = "RoboTour no disponible, espere para seguir la gira\n"
            }
            "Chinese" -> {
                text2?.text = "RoboTour无法使用，请等待跟随游览\n"
            }
            else -> {
                text2?.text = "RoboTour not available, please wait to follow tour"
            }
        }
    }

    private fun switchBackToMain() {
        clearFindViewByIdCache()
        toast("error occred")
        startActivity<MainActivity>("language" to language) // now we can switch the activity
    }

    private fun switchToPictures() {
        clearFindViewByIdCache()
        startActivity<PicturesActivity>("language" to language) // now we can switch the activity
    }

    private fun switchToListen() {
        clearFindViewByIdCache()
        startActivity<ListenInActivity>("language" to language) // now we can switch the activity
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
        if (waitingForListen) {
            val a = loadInt("user")
            async {
                when (a) {
                    1 -> {
                        sendPUTNEW(24, "F")
                        sendPUTNEW(16, "F")
                    }
                    2 -> {
                        sendPUTNEW(24, "F")
                        sendPUTNEW(17, "F")
                    }
                    else -> {
                        //Do nothing
                    }
                }
            }
            super.onBackPressed()
        } else {
            toast("please wait")
        }
    }

    private val t: Thread = object : Thread() {
        /*This thread will check if the other use has made their selection*/
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            runOnUiThread {
                updatetext2()
            }
            while (!Thread.currentThread().isInterrupted) {
                println("++++ t thread WaitingActivity")
                try {
                    val a = URL(url).readText()
                    if (a[24] == 'T') {
                        switchBackToMain()
                    } else if (a[21] == 'T') {
                        switchToListen()
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedByTimeoutException) {
                    Thread.currentThread().interrupt()
                }
            }
            Thread.currentThread().interrupt()
        }
    }
}