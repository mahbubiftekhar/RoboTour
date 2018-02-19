package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.*
import org.jetbrains.anko.*
import java.net.URL

class Waiting : AppCompatActivity() {
    val user = 1
    private var language = ""
    private var message = ""
    private var imageView: ImageView? = null
    private var descriptionView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        language = intent.getStringExtra("language") //Getting the language from the previous activity

        when (language) {
            "English" -> message = "Waiting for other user..."
            "German" -> message = "Bitte Warten Sie Auf den Anderen User..."
            "French" -> message = "Veuillez Attendre l'Autre Utilisateur..."
            "Spanish" -> message = "Por Favor Espere al Otro Usuario..."
            "Chinese" -> message = "请等待其他用户..."
            else -> message = "Please Wait For the Other User..."
        }

        verticalLayout {
            webView {
                loadUrl("file:///android_asset/robotour_spinning_grey.gif")
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
            textView {
                textSize = 34f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = Gravity.CENTER
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
                this.gravity = Gravity.CENTER
                padding = dip(10)
            }
            background = ColorDrawable(Color.parseColor("#EEEEEE"))
        }
        async {
            t.start()
            pictureThread.start()
        }
    }

    fun switchToNavigate() {
        Thread.sleep(6500)
        pictureThread.interrupt() //Stop the thread advertising all the art pieces
        t.interrupt() // Stop the thread looking for the other use
        clearFindViewByIdCache()
        startActivity<NavigatingActivity>("language" to language) // now we can switch the activity
    }


    override fun onBackPressed() {
        /*Overridden onBackPressed*/
    }

    private val t: Thread = object : Thread() {
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

    private val pictureThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        var a = 0
        override fun run() {
            while (!isInterrupted) {
                if(a>9){
                    //Reset A to avoid null pointers
                    a=0
                }
                try {
                    //UI thread MUST be updates on the UI thread, other threads may not update the UI thread
                    runOnUiThread {
                        imageView?.setImageResource(allArtPieces[a].imageID)
                        descriptionView?.text = allArtPieces[a].name
                    }
                    Thread.sleep(2000)
                    a++
                } catch (e: InterruptedException) {
                }
            }
        }
    }
}
