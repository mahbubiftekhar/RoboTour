package com.example.david.robotour

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.support.v4.content.res.ResourcesCompat
import android.view.WindowManager
import android.widget.Button
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.IOException
import java.io.InterruptedIOException
import java.net.URL
import java.util.ArrayList

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
    private var error_Control = ""
    private var error_Listen = ""
    private var userID = 0
    private var controlProgress = false
    private var listenProgress = false
    private val url = "https://proparoxytone-icing.000webhostapp.com/receiver.php"


    override fun onBackPressed() {
        val a = loadInt("user")
        async {
            when (a) {
                1 -> {
                    sendPUTNEW(16, "F")
                }
                2 -> {
                    sendPUTNEW(17, "F")
                }
                else -> {
                    //Do nothing
                }
            }
        }
        //checkerThread.interrupt()
        t.interrupt()
        super.onBackPressed()
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

    override fun onDestroy() {
        //    checkerThread.interrupt()
        t.interrupt()
        super.onDestroy()
    }

    override fun onResume() {
        //if (checkerThread.state == Thread.State.NEW) {
        //   checkerThread.start()
        //}
        Thread.sleep(100)
        if (t.state == Thread.State.NEW) {
            t.start()
        }
        super.onResume()
    }

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

    private val t: Thread = object : Thread() {
        /*This thread will check if the user has selected at least one picture, if they haven't then it will change the background
        * colour of the start button to grey*/
        @SuppressLint("PrivateResource")
        override fun run() {
            while (!Thread.currentThread().isInterrupted) try {
                Thread.sleep(200)
                async {
                    val a = URL(url).readText()
                    uiThread{
                        val b = a[16]
                        val c = a[17]
                        println("a[16]: $b , a[17]: $c")
                        if ('T' == a[18]) {
                            runOnUiThread {
                                twoUsers = true
                            }
                        }
                        when {

                            a[16] == 'F' && userID == -1 -> {
                                println("+++++++ONE")
                                runOnUiThread { userID = 1 }
                                async {
                                    sendPUTNEW(16, "O")
                                }
                                runOnUiThread { saveInt("user", 1) }
                            }
                            a[17] == 'F' && userID == -1 && twoUsers-> {
                                println("+++++++++TWO")
                                runOnUiThread { userID = 2 }
                                async {
                                    sendPUTNEW(17, "O")
                                }
                                runOnUiThread { saveInt("user", 2) }
                            }
                        }
                        println("++++userid: $userID")

                        //Server must be extended for this functionality
                        //controlProgress = a[19]=='T'

                        println("&&&& user1: $user1, user2: $user2, twoUsers: $twoUsers")
                        if (twoUsers) {
                            when (userID) {
                                1 -> {
                                    println("++++1")
                                    runOnUiThread {
                                        controlProgress = true
                                        controlButton?.background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml2, null)
                                    }
                                }
                                2 -> {
                                    println("++++2")
                                    runOnUiThread {
                                        controlProgress = true
                                        controlButton?.background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml2, null)
                                    }
                                }
                                else -> {
                                    println("++++3")
                                    runOnUiThread {
                                        controlProgress = false
                                    }
                                }
                            }
                        } else {
                            val y = userID == 1
                            println("boolean $y ")
                            println("boolean: this is the actual userid $userID")
                            if (userID == 1) {
                                println("++++4")
                                runOnUiThread {
                                    controlProgress = true
                                    controlButton?.background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml2, null)
                                }
                            } else if(userID==2 || userID==-1) {
                                println("++++6")
                                runOnUiThread {
                                    controlProgress = false
                                    controlButton?.background = ResourcesCompat.getDrawable(resources, R.drawable.buttonsgreyed, null)
                                }
                            }
                        }
                    }
                }

                Thread.sleep(200)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (e: InterruptedIOException) {
                Thread.currentThread().interrupt()
            }
            Thread.currentThread().interrupt()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        language = intent.getStringExtra("language")
        saveInt("user", -1)
        userID = loadInt("user")
        println("first instance $userID")
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        val message: String = when (language) {
            "French" -> "Voulez-vous le contrôle ou préférez-vous simplement suivre la tournée?"
            "German" -> "Möchten Sie die Kontrolle haben oder möchten Sie lieber der Tour folgen?"
            "Spanish" -> "¿Quieres control o prefieres simplemente seguir el recorrido?"
            "Chinese" -> "你想要控制吗，还是只想跟着游览？"
            else -> "Do you want control or would you prefer to just follow the tour?"
        }
        when (language) {
            "French" -> {
                error_Control = "RoboTour en tournée, veuillez patienter ou suivre le tour"
                error_Listen = "RoboTour n'est pas en tournée, veuillez sélectionner Control RoboTour"
            }
            "German" -> {
                error_Control = "RoboTour auf Tour, bitte warten oder Tour folgen"
                error_Listen = "RoboTour ist nicht auf Tour, bitte wählen Sie RoboTour steuern"
            }
            "Spanish" -> {
                error_Control = "RoboTour de gira, por favor espere o siga el recorrido"
                error_Listen = "RoboTour no gira, seleccione Control RoboTour"
            }
            "Chinese" -> {
                error_Control = "RoboTour巡演时，请等待或关注巡演"
                error_Listen = "RoboTour不参观，请选择Control RoboTour\n"
            }
            else -> {
                error_Control = "RoboTour on tour, please wait or follow tour"
                error_Listen = "RoboTour not touring, please select Control RoboTour"
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
            background = ColorDrawable(resources.getColor(R.color.androidsBackground))
            imageView(R.drawable.robotour_small) {
                background = ColorDrawable(resources.getColor(R.color.androidsBackground))
            }
            textView {
                textSize = 24f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = Gravity.CENTER
                text = message
                setTextColor(resources.getColor(R.color.roboTourTeal))
                background = ColorDrawable(resources.getColor(R.color.androidsBackground))
            }
            verticalLayout {
                controlButton = button(controlRoboTour) {
                    textSize = 20f
                    background = ResourcesCompat.getDrawable(resources, R.drawable.buttonsgreyed, null)
                    onClick {
                        if (controlProgress) {
                            t.interrupt()
                            // checkerThread.interrupt()
                            startActivity<PicturesActivity>("language" to language)
                        } else {
                            toast(error_Control)
                        }
                    }
                }
            }
            verticalLayout {
                button {
                    textSize = 10f
                    background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                }
            }
            verticalLayout {
                listenButton = button(listenIn) {
                    textSize = 20f
                    background = ResourcesCompat.getDrawable(resources, R.drawable.buttonsgreyed, null)
                    onClick {
                        if (listenProgress) {
                            t.interrupt()
                            //checkerThread.interrupt()
                            startActivity<ListenInActivity>("language" to language)
                        } else {
                            toast(error_Listen)
                        }
                    }
                }
                onLongClick {
                    startActivity<FinishActivity>("language" to intent.getStringExtra("language"))
                    true
                }
            }
        }
    }

}
