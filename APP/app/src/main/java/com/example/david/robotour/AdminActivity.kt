package com.example.david.robotour

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
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

    val url = "https://proparoxytone-icing.000webhostapp.com/receiver.php"
    private fun sendPUTNEW(identifier: Int, command: String) {
        /*DISCLAIMER: When calling this function, if you don't run in an async, you will get
        * as security exception - just a heads up */
        val httpclient = DefaultHttpClient()
        val httPpost = HttpPost(url)
        try { val nameValuePairs = ArrayList<NameValuePair>(4)
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
                        val a = URL(url).readText()
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
            Thread.currentThread().interrupt()
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
            "1" -> true
            "2" -> true
            "3" -> true
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
            18 -> true
            19 -> true
            20 -> true
            21 -> true
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        sendy.setOnClickListener {
            if (destination == null) {
                toast("Don't do that! Destination cannot be null!!")
            } else if (messageToSend.text == null) {
                toast("Don't do that! message cannot be null!!")
            } else {
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
            for (i in 10..20) {
                async {
                    sendPUTNEW(i, "F")
                }
            }
            vibrate()
        }
        SWITCH_URL.setOnClickListener{
            val url1 = "https://proparoxytone-icing.000webhostapp.com/receiver.php"
            val url2 = "https://proparoxytone-icing.000webhostapp.com/receiverPhone.php"
            if(loadString("url")==url1){
                saveString("url",url2)
                toast("set as receiverPhone")
            } else {
                saveString("url",url1)
                toast("set as receiver")
            }
        }
        RESET_EVERYTHING.setOnClickListener {
            //Resets all from 0 .. 17
            for (i in 0..20) {
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
        RESET_PAINTINGS.setOnClickListener {
            //Resets the paintings
            for (i in 0..9) {
                async {
                    sendPUTNEW(i, "F")
                }
            }
        }
        CONTINUE.setOnClickListener {
            //Tells RoboTour to continue
            async {
                sendPUTNEW(11, "F")
            }
            vibrate()
        }
        CONTROLON.setOnClickListener {
            async {
                sendPUTNEW(19, "T")
            }
            vibrate()
        }
        CONTROLOFF.setOnClickListener{
            async {
                sendPUTNEW(19, "F")
            }
            vibrate()
        }
        USER2_MODE_ON.setOnClickListener{
            async {
                sendPUTNEW(18, "T")
            }
            vibrate()
            toast("Function deprecated")
        }
        USER2_MODE_OFF.setOnClickListener{
            async {
                sendPUTNEW(18, "F")
            }
            vibrate()
            toast("Function deprecated")
        }
        //This languages the user thread to check for updates
        updateTextThread.start()

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

    override fun onResume() {
        if (updateTextThread.state == Thread.State.NEW) {
            updateTextThread.start()
        }
        super.onResume()
    }

    private fun saveString(key: String, value: String) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    private fun loadString(key: String): String {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getString(key, "https://proparoxytone-icing.000webhostapp.com/receiver.php")
    }
}