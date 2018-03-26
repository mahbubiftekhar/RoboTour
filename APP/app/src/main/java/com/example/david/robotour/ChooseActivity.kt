package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.*
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
import org.jetbrains.anko.design.floatingActionButton


@Suppress("DEPRECATION")
class ChooseActivity : AppCompatActivity() {
    /*This activity will be shown to the user when they cancel or finish the tour */
    private lateinit var listenIn: String
    private lateinit var controlRoboTour: String
    private var user1 = false
    private var user2 = false
    private var twoUsers = false
    private var language: String = ""
    private var error_Control = ""
    private var error_Listen = ""
    private var userID = 0
    private var controlProgress = false
    private var listenProgress = false
    private lateinit var listenButton: Button
    private lateinit var controlButton:Button

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
                    println("+++++ $a")
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
                            a[21] == 'T' -> {
                                listenProgress = true
                                listenButton.background = roundBackground()
                                listenButton.setTextColor(Color.BLACK)
                            }
                            a[21] == 'F' -> {
                                listenProgress = false
                                listenButton.background = roundBackgroundUnclickable()
                                listenButton.setTextColor(resources.getColor(R.color.greyedOutText))
                            }

                        }
                        println("++++userid: $userID")
                        println("&&&& user1: $user1, user2: $user2, twoUsers: $twoUsers")
                        if (twoUsers) {
                            when (userID) {
                                1 -> {
                                    println("++++1")
                                    runOnUiThread {
                                        controlProgress = true
                                        controlButton.background = roundBackground()
                                        controlButton.setTextColor(Color.BLACK)
                                    }
                                }
                                2 -> {
                                    println("++++2")
                                    runOnUiThread {
                                        controlProgress = true
                                        controlButton.background = roundBackground()
                                        controlButton.setTextColor(Color.BLACK)
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
                                    controlButton.background = roundBackground()
                                    controlButton.setTextColor(Color.BLACK)
                                }
                            } else if(userID==2 || userID==-1) {
                                println("++++6")
                                runOnUiThread {
                                    controlProgress = false
                                    controlButton.background = roundBackgroundUnclickable()
                                    controlButton.setTextColor(resources.getColor(R.color.greyedOutText))
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
        if(url=="http://www.mahbubiftekhar.co.uk/receiver2.php"){
            toast("Warning, in receiver2 mode")
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // add back button to actionbar
        when (language) {
            "French" -> {
                controlRoboTour = "Contrôler\nRoboTour"
                listenIn = "Suivre\nune Visite"
                supportActionBar?.title = "Contrôler Ou Suivre un Tour"
            }
            "German" -> {
                controlRoboTour = "RoboTour\nSteuern"
                listenIn = "Tour\nFolgen"
                supportActionBar?.title = "Eine Tour Steuern Oder Folgen"
            }
            "Spanish" -> {
                controlRoboTour = "Controlar\nRoboTour"
                listenIn = "Seguir\nuna Gira"
                supportActionBar?.title = "Controlar O Seguir una Gira"
            }
            "Chinese" -> {
                controlRoboTour = "控制\nRoboTour"
                listenIn = "按照旅程"
                supportActionBar?.title = "控制或跟随游览"
            }
            else -> {
                controlRoboTour = "Control\nRoboTour"
                listenIn = "Follow\na Tour"
                supportActionBar?.title = "Control Or Follow a Tour"
            }
        }
        relativeLayout {
            floatingActionButton {
                //UI
                imageResource = R.drawable.icon_q_mark
                //ColorStateList usually requires a list of states but this works for a single color
                backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.roboTourTeal))
                lparams { alignParentRight(); rightMargin = dip(10); centerVertically()  }
                onClick {
                    alert {
                        customView {
                            linearLayout {
                                orientation = LinearLayout.VERTICAL
                                horizontalPadding = dip(4)
                                textView {
                                    text = "Help"
                                    textSize = 30f
                                    typeface = Typeface.DEFAULT_BOLD
                                    verticalPadding = dip(7)
                                    lparams { gravity = Gravity.CENTER_HORIZONTAL; }
                                }
                                textView {
                                    text = "Control RoboTour allows you to select the paintings you want RoboTour to go to, and allows you to control RoboTour during the tour.\nIf the button is grey, this means that no robot is available right now to control."
                                    textSize = 16f
                                    verticalPadding = dip(5)
                                }
                                textView {
                                    text = "Follow A Tour allows you to listen into an existing tour in your native language.\nIf the button is grey, this means that there is no active tour right now."
                                    textSize = 16f
                                    verticalPadding = dip(5)
                                }
                            }
                        }
                    }.show()
                }
            }
            verticalLayout {
                background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                controlButton = button(controlRoboTour) {
                    textSize = 20f
                    textColor = resources.getColor(R.color.greyedOutText)
                    //background = ResourcesCompat.getDrawable(resources, R.drawable.buttonsgreyed, null)
                    background = roundBackgroundUnclickable()
                    lparams {height = dip(200); width = dip(200); gravity = Gravity.CENTER_HORIZONTAL; verticalMargin = dip(30)}
                    onClick {
                        if (controlProgress) {
                            t.interrupt()
                            clearFindViewByIdCache()
                            startActivity<PicturesActivity>("language" to language)
                        } else {
                            toast(error_Control)
                        }
                    }
                }
                listenButton = button(listenIn) {
                    textSize = 20f
                    textColor = resources.getColor(R.color.greyedOutText)
                    background = roundBackgroundUnclickable()
                    lparams {height = dip(200); width = dip(200); gravity = Gravity.CENTER_HORIZONTAL; verticalMargin = dip(10)}
                    onClick {
                        if (listenProgress) {
                            t.interrupt()
                            startActivity<ListenInActivity>("language" to language)
                        } else {
                            toast(error_Listen)
                        }
                    }
                }
            }.lparams { height = matchParent; width = matchParent ; gravity = Gravity.CENTER_HORIZONTAL }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        //Back button moves back to MainActivity
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun roundBackground() = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(resources.getColor(R.color.roboTourTeal))
        setStroke(2, Color.BLACK)
    }

    private fun roundBackgroundUnclickable() = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(resources.getColor(R.color.greyedOut))
        setStroke(2, Color.BLACK)
    }

}
