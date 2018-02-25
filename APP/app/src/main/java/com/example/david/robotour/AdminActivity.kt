package com.example.david.robotour

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
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

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar?.hide() //hide actionbar

        STOP_ROBOTOUR.setOnClickListener {
            async{
                sendPUTNEW(11,"T")
            }
        }
        AUX_RESET.setOnClickListener {
            for(i in 10..17){
                async{
                    sendPUTNEW(i,"F")
                }
            }
        }

        RESET_EVERYTHING.setOnClickListener {
            for(i in 0..17){
                async{
                    sendPUTNEW(i,"F")
                }
            }
        }

        USER1_ONLINE.setOnClickListener {
            async{
                sendPUTNEW(16,"T")
            }
        }

        USER2_ONLINE.setOnClickListener {
          async{
              sendPUTNEW(17,"T")
          }
        }

        USER_1_OFF.setOnClickListener {
            async{
                sendPUTNEW(16,"F")
            }
        }

        USER_2_OFF.setOnClickListener {
            async{
                sendPUTNEW(17,"F")
            }
        }

        SWITCH_USER.setOnClickListener{
            val a = loadInt("user")
            when (a) {
                0 -> {
                    saveInt("user", 1)
                    Toast.makeText(applicationContext, "User 1", Toast.LENGTH_LONG).show()
                    vibrate()
                }
                1 -> {
                    saveInt("user", 2)
                    Toast.makeText(applicationContext, "User 2", Toast.LENGTH_LONG).show()
                    vibrate()
                }
                else -> {
                    saveInt("user", 1)
                    Toast.makeText(applicationContext, "User 1", Toast.LENGTH_LONG).show()
                    vibrate()
                }
            }
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
}
