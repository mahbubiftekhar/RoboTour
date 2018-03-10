package com.example.david.robotour

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.widget.Button
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException


@Suppress("DEPRECATION")
class ChooseLevel : AppCompatActivity() {
    /*This activity will be shown to the user when they cancel or finish the tour */
    private lateinit var listenIn: String
    private lateinit var controlRoboTour: String
    private var user1 = false
    private var user2 = false
    private var twoUsers = false
    private var language: String = ""
    private var controlButton: Button? = null
    private var listenButton: Button? = null
    private lateinit var t: Thread
    private var error_control = ""
    private var error_listen = ""

    override fun onBackPressed() {
        checkerThread.interrupt()
        t.interrupt()
        super.onBackPressed()
    }

    override fun onResume() {
        checkerThread.start()
        t.start() /*Start to run the thread*/
        super.onResume()
    }

    override fun onDestroy() {
        checkerThread.interrupt()
        t.interrupt()
        super.onDestroy()
    }

    override fun onStop() {
        checkerThread.interrupt()
        t.interrupt()
        super.onStop()
    }

    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getInt(key, 0)
    }

    private val checkerThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            while (!isInterrupted) {
                try {
                    async {
                        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/receiver.php").readText()
                        val b = URL("http://homepages.inf.ed.ac.uk/s1553593/user1.php").readText()
                        uiThread {
                            if (a[16] == 'T') {
                                user1 = true
                            }
                            if (a[17] == 'T') {
                                user2 = true
                            }
                            if ("T" == b) {
                                twoUsers = true
                            }
                        }
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedByTimeoutException) {
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                    Thread.currentThread().interrupt()
                }
                Thread.currentThread().interrupt()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        language = intent.getStringExtra("language")
        window.decorView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        t = object : Thread() {
            /*This thread will check if the user has selected at least one picture, if they haven't then it will change the background
            * colour of the start button to grey*/
            @SuppressLint("PrivateResource")
            override fun run() {
                while (!Thread.currentThread().isInterrupted) {
                    try {
                        if (!user1 && loadInt("user") == 1) {
                            runOnUiThread {
                                controlButton?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                            }
                        } else if (user1 && loadInt("user") == 1) {
                            //set background as grey
                            runOnUiThread {
                                controlButton?.background = ColorDrawable(resources.getColor(R.color.material_grey_100))
                            }
                        }

                        if (!user2 && loadInt("user") == 2) {
                            //set background as grey
                            runOnUiThread {
                                controlButton?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                            }
                        } else if (user2 && loadInt("user") == 2) {
                            //set background as grey
                            runOnUiThread {
                                controlButton?.background = ColorDrawable(resources.getColor(R.color.material_grey_100))
                            }
                        }

                        if (twoUsers) {
                            if (user1 && user2) {
                                //set to green
                                runOnUiThread {
                                    listenButton?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                                }
                            } else {
                                runOnUiThread {
                                    listenButton?.background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                                }
                            }
                        } else {
                            if (!user1 && !user2) {
                                runOnUiThread {
                                    listenButton?.background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                                }
                            } else if (user1 && !user2) {
                                //set to green
                                runOnUiThread {
                                    listenButton?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                                }
                            } else if (user2 && !user1) {
                                //Set to green
                                runOnUiThread {
                                    listenButton?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                                }
                            } else {
                                runOnUiThread {
                                    listenButton?.background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                                }

                            }
                        }
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
            }
        }
        val message: String = when (language) {
            "French" -> "Voulez-vous le contrôle ou préférez-vous simplement suivre la tournée?"
            "German" -> "Möchten Sie die Kontrolle haben oder möchten Sie lieber der Tour folgen?"
            "Spanish" -> "¿Quieres control o prefieres simplemente seguir el recorrido?"
            "Chinese" -> "你想要控制吗，还是只想跟着游览？"
            else -> "Do you want control or would you prefer to just follow the tour?"
        }
        when (language) {
            "French" -> {
                error_control = "RoboTour en tournée, veuillez patienter ou suivre le tour"
                error_listen = "RoboTour n'est pas en tournée, veuillez sélectionner Control RoboTour"
            }
            "German" -> {
                error_control = "RoboTour auf Tour, bitte warten oder Tour folgen"
                error_listen = "RoboTour ist nicht auf Tour, bitte wählen Sie RoboTour steuern"
            }
            "Spanish" -> {
                error_control = "RoboTour de gira, por favor espere o siga el recorrido"
                error_listen = "RoboTour no gira, seleccione Control RoboTour"
            }
            "Chinese" -> {
                error_control = "RoboTour巡演时，请等待或关注巡演"
                error_listen = "RoboTour不参观，请选择Control RoboTour\n"
            }
            else -> {
                error_control = "RoboTour on tour, please wait or follow tour"
                error_listen = "RoboTour not touring, please select Control RoboTour"
            }

        }

        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        when (language) {
            "French" -> {
                controlRoboTour = "Contrôler RoboTour"
                listenIn = "Suivez la visite"
            }
            "German" -> {
                controlRoboTour = "Steuern Sie die RoboTour"
                listenIn = "Steuern Sie die RoboTour"
            }
            "Spanish" -> {
                controlRoboTour = "Controlar RoboTour"
                listenIn = "Sigue la gira"
            }
            "Chinese" -> {
                controlRoboTour = "控制RoboTour"
                listenIn = "按照旅程"
            }
            else -> {
                controlRoboTour = "Control RoboTour"
                listenIn = "Follow the tour"
            }
        }
        verticalLayout {
            imageView(R.drawable.robotour_small) {
                background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                onClick {
                    val i = baseContext.packageManager
                            .getLaunchIntentForPackage(baseContext.packageName)
                    i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
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
            verticalLayout {
                controlButton = button(controlRoboTour) {
                    textSize = 20f
                    background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                    onClick {
                        if (twoUsers) {
                            if (!user1 && loadInt("user") == 1) {
                                t.interrupt()
                                checkerThread.interrupt()
                                startActivity<PicturesActivity>("language" to language)
                            } else if (loadInt("user") == 2 && !user2) {
                                t.interrupt()
                                checkerThread.interrupt()
                                startActivity<PicturesActivity>("language" to language)
                            } else {
                                toast(error_control)
                            }
                        } else {
                            if (!user1 && loadInt("user") == 1 && !user2) {
                                t.interrupt()
                                checkerThread.interrupt()
                                startActivity<PicturesActivity>("language" to language)
                            } else if (loadInt("user") == 2 && !user2 && !user1) {
                                t.interrupt()
                                checkerThread.interrupt()
                                startActivity<PicturesActivity>("language" to language)
                            } else {
                                toast(error_control)
                            }
                        }
                    }
                }
            }
            verticalLayout {
                button {
                    textSize = 20f
                    background = ColorDrawable(Color.parseColor("#FFFFFF"))

                }
            }
            verticalLayout {
                listenButton = button(listenIn) {
                    textSize = 20f
                    background = ColorDrawable(Color.parseColor("#505050"))
                    onClick {
                        if (twoUsers) {
                            if (user1 && user2) {
                                t.interrupt()
                                checkerThread.interrupt()
                                startActivity<ListenInActivity>("language" to language)
                            } else {
                                toast(error_listen)
                            }
                        } else {
                            when {
                                (!user1 && !user2) -> {
                                    toast(error_listen)
                                }
                                user1 -> {
                                    t.interrupt()
                                    checkerThread.interrupt()
                                    startActivity<ListenInActivity>("language" to language)
                                }
                                user2 -> {
                                    t.interrupt()
                                    checkerThread.interrupt()
                                    startActivity<ListenInActivity>("language" to language)
                                }
                                else -> toast(error_listen)
                            }
                        }
                    }
                    onLongClick {
                        startActivity<FinishActivity>("language" to intent.getStringExtra("language"))
                        true
                    }
                }
            }
            t.start() /*Start to run the thread*/
        }
    }
}