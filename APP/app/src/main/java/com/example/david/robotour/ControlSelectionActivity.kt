package com.example.david.robotour

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
    private var imageView: ImageView? = null
    private var descriptionView: TextView? = null

    private fun saveInt(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
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
        /*message = when (language) {
            "German" -> "Die nächste RoboTour finden\n"
            "French" -> "Trouver le RoboTour le plus proche\n"
            "Spanish" -> "Encontrar el RoboTour más cercano\n"
            "Chinese" -> "寻找最近的RoboTour\n"
            else -> "Finding closest RoboTour"
        }*/
        message = when (language) {
            "German" -> "Wir Suchen Nach Einer Verfügbaren RoboTour"
            "French" -> "Recherche d'un RoboTour disponible"
            "Spanish" -> "Buscando un RoboTour disponible"
            "Chinese" -> "正在寻找可用的萝卜途"
            else -> "Searching For An Available RoboTour"
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
            val a = URL(url).readText()
            if (a[24] == 'T') {
                switchBackToMain() //Switch to the main activity for safety
            }  else if (a[16] == 'F' && a[21] != 'T') {
                //User 1 is not online hence take that position
                sendPUTNEW(16, "O")
                saveInt("user", 1)
                Thread.sleep(4000)
                switchToPictures()
            } else if (a[17] == 'F' && a[18] == 'T' && a[21] != 'T') {
                //user 2 is not online, hence take that position
                sendPUTNEW(17, "O")
                saveInt("user", 2)
                Thread.sleep(4000)
                switchToPictures()
            } else {
                println(">>>> in the else clause")
                waitingForListen = true
                t.start()
            }
        }
    }

    private fun updatetext2() {
        text2?.text = when (language) {
            "German" -> {
                "Keine RoboTour Verfügbar, Bitte Warten Sie, Um Einer Tour Zu folgen"
            }
            "French" -> {
                "Aucun RoboTour Disponible, Veuillez Attendre Pour Suivre Un Tour"
            }
            "Spanish" -> {
                "No RoboTour Disponible, Por Favor Espere Para Seguir Un Recorrido"
            }
            "Chinese" -> {
                "暂时没有可用的萝卜途，请等候别的萝卜途结束旅行"
            }
            else -> {
                "Sorry, No RoboTour Available, Please Wait To Be Assigned To An Existing Tour"
            }
        }
    }

    private fun switchBackToMain() {
        clearFindViewByIdCache()
        toast("Timed Out")
        waitingForListen = false
        t.interrupt()
        startActivity<MainActivity>("language" to language) // now we can switch the activity
    }

    private fun switchToPictures() {
        clearFindViewByIdCache()
        t.interrupt()
        startActivity<PicturesActivity>("language" to language) // now we can switch the activity
    }

    private fun switchToListen() {
        clearFindViewByIdCache()
        t.interrupt()
        startActivity<ListenInActivity>("language" to language) // now we can switch the activity
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
        if (waitingForListen) {
            switchBackToMain()
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
            try{
                Thread.sleep(5000)
            }catch (e:Exception){

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