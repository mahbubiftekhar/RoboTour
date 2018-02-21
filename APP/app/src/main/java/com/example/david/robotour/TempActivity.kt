package com.example.david.robotour

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_temp.*
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.async
import java.io.IOException

class TempActivity : AppCompatActivity() {
    /*THIS CLASS/ACTIVITY HAS BEEN DEPRECATED
    * AS NO LONGER NEEDED (USED FOR CD1)
    *
    * M IFTEKHAR
    *
    * 19/02/2018
    *
    * */


    /*THIS IS A QUICK TEMP ACTIVITY FOR THE PURPOSE OF CLIENT DEMO ONE, THIS CAN ONLY BE ACCESSED FROM A LONG PRESS ON THE START BUTTON*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)
        supportActionBar?.hide() //hide actionbar

        SPEAK.setOnClickListener {
            uploadToServer("SPEAK") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
        }
        FORWARD.setOnClickListener {
            uploadToServer("FORWARD") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
        }
        BACKWARDS.setOnClickListener {
            uploadToServer("BACKWARDS") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
        }
        STOP.setOnClickListener {
            uploadToServer("STOP") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
        }
        RIGHT.setOnClickListener{
            uploadToServer("RIGHT") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
        }
        LEFT.setOnClickListener{
            uploadToServer("LEFT") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun saveINT(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    private fun loadINT(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getInt(key, 0)
    }

    private fun uploadToServer(command: String) {
        async {
            val httpclient = DefaultHttpClient()
            val httpPost = HttpPost("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")
            try {
                val a = loadINT("NUMBER") //Get the unique number
                val nameValuePairs = ArrayList<NameValuePair>(4)
                nameValuePairs.add(BasicNameValuePair("command", "Command:$$command-$a"))
                saveINT("NUMBER",a+1 ) //Increment the unique number
                httpPost.entity = UrlEncodedFormEntity(nameValuePairs)
                httpclient.execute(httpPost)
            } catch (e: ClientProtocolException) {
            } catch (e: IOException) {
            }
        }
    }
}
