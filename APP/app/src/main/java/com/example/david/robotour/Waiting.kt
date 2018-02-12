package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*
import java.net.URL

class Waiting : AppCompatActivity() {

    val user = 1
    var language = ""
    var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_waiting)
        supportActionBar?.hide() //hide actionbar
        language = intent.getStringExtra("language") //Getting the language from the previous activity

        when (language) {
            "English" -> message = "Please Wait For the Other User..."
            "German" -> message = "Bitte Warten Sie Auf den Anderen User..."
            "French" -> message = "Veuillez Attendre l'Autre Utilisateur..."
            "Spanish" -> message = "Por Favor Espere al Otro Usuario..."
            "Chinese" -> message = "請等待其他用戶"
            else -> message = "Please Wait For the Other User..."
        }

        verticalLayout {
            webView {
                loadUrl("file:///android_asset/robotour_spinning_grey.gif")
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
            textView {
                textSize = 32f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = Gravity.CENTER
                text = message
            }
            background = ColorDrawable(Color.parseColor("#EEEEEE"))
        }
        //Commented to work on splash view
        /*async {
            //Running the thread
            t.run()
        }
        //This part is temp until you make me the php files david
        t.interrupt()
        Thread.sleep(10000)
        switchToNavigate()
        //This above part is temproary*/
    }

    fun switchToNavigate() {
        t.interrupt() // Stop the thread
        startActivity<NavigatingActivity>("language" to language) // now we can switch the activity
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
    }

    fun checkUser1(): Boolean {
        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user1.php").readText()
        if (a == "Y") {
            return true
        }
        return false
    }

    fun checkUser2(): Boolean {
        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user2.php").readText()
        if (a == "Y") {
            return true
        }
        return false
    }

    val t: Thread = object : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    Thread.sleep(1000) //1000ms = 1 sec
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            async {
                                if (user == 1) {
                                    if (checkUser1()) {
                                        //If user 1 has made their selection and you are not user 1
                                        switchToNavigate()
                                    }
                                } else {
                                    //If user 2 has made their selection and you are not user 2
                                    if (checkUser1()) {
                                        switchToNavigate()
                                    }
                                }
                            }
                        }
                    })
                } catch (e: InterruptedException) {
                }
            }
        }
    }
}
