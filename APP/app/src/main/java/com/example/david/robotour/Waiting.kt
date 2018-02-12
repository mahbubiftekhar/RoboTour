package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import kotlinx.android.synthetic.*
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
            "English" -> message = "Waiting for other user..."
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
        async {
            t.start()
        }
    }

    fun switchToNavigate() {
        Thread.sleep(3500)
        t.interrupt() // Stop the thread
        clearFindViewByIdCache()
        startActivity<NavigatingActivity>("language" to language) // now we can switch the activity
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
    }

    val t: Thread = object : Thread() {
        /*This thread will check if the other use has made their selection*/
        override fun run() {
            while (!isInterrupted) {
                try {
                    if (user == 1) {
                        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user2.php").readText()
                        if (a == "Y") {
                            //If user 1 has made their selection and you are not user 1
                            switchToNavigate()
                        }
                    } else {
                        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/use1.php").readText()
                        println("USERS ID IS 2")
                        //If user 2 has made their selection and you are not user 2
                        if (a == "Y") {
                            switchToNavigate()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
    }
}
