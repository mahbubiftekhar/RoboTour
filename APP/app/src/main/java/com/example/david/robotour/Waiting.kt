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
import org.jetbrains.anko.*
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException

class Waiting : AppCompatActivity() {
    private var user = 1
    private var language = ""
    private var message = ""
    private var imageView: ImageView? = null
    private var descriptionView: TextView? = null

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
            "English" -> "Waiting for other user..."
            "German" -> "Bitte Warten Sie Auf den Anderen User..."
            "French" -> "Veuillez Attendre l'Autre Utilisateur..."
            "Spanish" -> "Por Favor Espere al Otro Usuario..."
            "Chinese" -> "请等待其他用户..."
            else -> "Please Wait For the Other User..."
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

            switchToNavigate() //This should be removed in the final implementation
        }

    }

    fun switchToNavigate() {
        Thread.sleep(6500) //This should be removed in the final implementation
        interruptAllThreads() //Interrupt all the threads
        clearFindViewByIdCache()
        startActivity<NavigatingActivity>("language" to language) // now we can switch the activity
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
    }

    private val t: Thread = object : Thread() {
        /*This thread will check if the other use has made their selection*/
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                println("++++ t thread Waiting")
                try {
                    if (user == 1) {
                        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user2.php").readText()
                        if (a == "Y") {
                            //If user 1 has made their selection and you are not user 1
                            switchToNavigate()
                        }
                    } else {
                        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user1.php").readText()
                        println("USERS ID IS 2")
                        //If user 2 has made their selection and you are not user 2
                        if (a == "Y") {
                            switchToNavigate()
                        }
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

    private fun interruptAllThreads() {
        //This function interrupts all the threads
        pictureThread.interrupt()
        t.interrupt()
        pictureThread.interrupt()
        t.interrupt()
    }

    override fun onPause() {
        pictureThread.interrupt()
        t.interrupt()
        pictureThread.interrupt()
        t.interrupt()
        super.onPause()
    }

    override fun onResume() {
        pictureThread.start()
        t.start()
        super.onResume()
    }

    private val pictureThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        var a = 0

        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            while (!isInterrupted) {
                println("++++ picture thread Waiting")
                if (a > 9) {
                    //Reset A to avoid null pointers
                    a = 0
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
