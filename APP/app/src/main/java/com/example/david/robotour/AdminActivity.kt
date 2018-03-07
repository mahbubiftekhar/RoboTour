package com.example.david.robotour

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_admin.*
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.IOException
import java.util.ArrayList
import org.jetbrains.anko.*
import java.io.InterruptedIOException
import java.net.URL


class AdminActivity : AppCompatActivity() {

    /*
    THE PURPOSE OF THIS ACTIVITY IS FOR DEBUGGING AND TESTING PURPOSES

    NOT FOR THE CLIENT TO BE SEEN

    M IFTEKHAR

    28/02/2018
     */

    private fun sendPUTNEW(identifier: Int, command: String) {
        val url = "http://homepages.inf.ed.ac.uk/s1553593/receiver.php"
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

    private val updateTextThread: Thread = object : Thread() {
        /*This thread is used to update the header*/
        /*Will update the header automatically*/
        override fun run() {
            while (!isInterrupted) {
                try {
                    async {
                        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/receiver.php").readText()
                        runOnUiThread {
                            setActionBar(a)
                        }
                    }
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    private fun setActionBar(heading: String) {
        val actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowHomeEnabled(false)
        actionBar.title = heading
        actionBar.show()
    }

    override fun onBackPressed() {
        updateTextThread.interrupt()
        super.onBackPressed()
    }

    private fun messageValid(message: String): Boolean {
        /*This is a validity check to ensure no malarkey is put on the server*/
        return when (message) {
            "A" -> true
            "F" -> true
            "T" -> true
            "N" -> true
            "" -> true
            " " -> true
            else -> false
        }
    }

    private fun destination(destination: Int): Boolean {
        /*This is a validity check to ensure no malarkey is put on the server*/
        return when (destination) {
            0 -> true
            1 -> true
            2 -> true
            3 -> true
            4 -> true
            5 -> true
            6 -> true
            7 -> true
            8 -> true
            9 -> true
            10 -> true
            11 -> true
            12 -> true
            14 -> true
            13 -> true
            15 -> true
            16 -> true
            17 -> true
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings

        sendy.setOnClickListener {
            val destination = destination.text.toString()
            var message = ""
            try {
                message = messageToSend.text.toString().toUpperCase()
            } catch (e: NumberFormatException) {
                toast("Error!! Please don't do that!")
            }
            if (destination(destination.toInt()) && messageValid(message) && message != "") {
                vibrate()
                async {
                    try {
                        sendPUTNEW(destination.toInt(), message)
                    } catch (e: Exception) {
                        toast("Error!! Please don't do that!")
                    } catch (e: NumberFormatException) {
                        toast("Error!! Please don't do that!")
                    }
                    runOnUiThread {
                        toast("SENT $message TO $destination SUCCESSFULLY")
                    }
                }
            } else {
                toast("Invalid input, try again, any issues consulate Mahbub")
            }
        }
        STOP_ROBOTOUR.setOnClickListener {
            /*Sets Robot stop to true*/
            async {
                sendPUTNEW(11, "T")
            }
            vibrate()
        }
        AUX_RESET.setOnClickListener {
            /*Resets all the AUX, This means that the stuff from stuff such as skip etc will be reset, this
            excludes the user data */
            for (i in 10..15) {
                async {
                    sendPUTNEW(i, "F")
                }
            }
            vibrate()
        }

        RESET_EVERYTHING.setOnClickListener {
            //Resets all from 0 .. 17
            for (i in 0..17) {
                async {
                    sendPUTNEW(i, "F")
                }
            }
            vibrate()
        }
        USER1_ONLINE.setOnClickListener {
            //Set user one as online
            async {
                sendPUTNEW(16, "T")
            }
            vibrate()
        }

        USER2_ONLINE.setOnClickListener {
            //Set user two as online
            async {
                sendPUTNEW(17, "T")
            }
            vibrate()
        }

        USER_1_OFF.setOnClickListener {
            //Set user one as offline
            async {
                sendPUTNEW(16, "F")
            }
            vibrate()
        }

        USER_2_OFF.setOnClickListener {
            //Set user two as offline
            async {
                sendPUTNEW(17, "F")
            }
            vibrate()
        }

        SWITCH_USER.setOnClickListener {
            /*This will change the user, This is defaulted as 1, user two must be selected itself*/
            val a = loadInt("user")
            when (a) {
                0 -> {
                    saveInt("user", 1)
                    Toast.makeText(applicationContext, "User 1 mode", Toast.LENGTH_LONG).show()
                    vibrate()
                }
                1 -> {
                    saveInt("user", 2)
                    Toast.makeText(applicationContext, "User 2 mode", Toast.LENGTH_LONG).show()
                    vibrate()
                }
                else -> {
                    saveInt("user", 1)
                    Toast.makeText(applicationContext, "User 1 mode", Toast.LENGTH_LONG).show()
                    vibrate()
                }
            }
        }
        async {
            //This languages the user thread to check for updates
            updateTextThread.run()
        }
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT > 25) { /*Attempt to not use the deprecated version if possible, if the SDK version is >25, use the newer one*/
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(300, 10))
        } else {
            /*for backward comparability*/
            @Suppress("DEPRECATION")
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(300)
        }
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
        return sharedPreferences.getInt(key, 0)
    }
}
