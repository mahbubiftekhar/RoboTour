package com.example.david.robotour

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_temp.*
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.jetbrains.anko.async
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class TempActivity : AppCompatActivity() {

    /*THIS IS A QUICK TEMP ACTIVITY FOR THE PURPOSE OF CLIENT DEMO ONE, THIS CAN ONLY BE ACCESSED FROM A LONG PRESS ON THE START BUTTON*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)

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
            async{
               val a = URL("http://homepages.inf.ed.ac.uk/s1553593/monalisa.php").readText()
           }
        }
        RIGHT.setOnClickListener{
            uploadToServer("RIGHT") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
            //sendPUT("F", "http://homepages.inf.ed.ac.uk/s1553593/monalisa.php")
        }
        LEFT.setOnClickListener{
            uploadToServer("LEFT") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
            //sendPUT("T", "http://homepages.inf.ed.ac.uk/s1553593/monalisa.php")
        }
    }

    @SuppressLint("ApplySharedPref")
    fun SaveInt(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun LoadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getInt(key, 0)
        return savedValue
    }

    fun uploadToServer(command: String) {
        async {
            val httpclient = DefaultHttpClient()
            val httppost = HttpPost("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")
            try {
                val a = LoadInt("NUMBER") //Get the unique number
                val nameValuePairs = ArrayList<NameValuePair>(4)
                nameValuePairs.add(BasicNameValuePair("command", "Command:$$command-$a"))
                SaveInt("NUMBER",a+1 ) //Increment the unique number
                httppost.entity = UrlEncodedFormEntity(nameValuePairs)
                httpclient.execute(httppost)
            } catch (e: ClientProtocolException) {
                // TODO Auto-generated catch block
            } catch (e: IOException) {
                // TODO Auto-generated catch block
            }
            println("FINNISHED")
        }
    }

    fun sendPUT(command: String, url: String) {
        async {
            val httpclient = DefaultHttpClient()
            val httppost = HttpPost(url)
            try {
                val nameValuePairs = ArrayList<NameValuePair>(4)
                nameValuePairs.add(BasicNameValuePair("command", command))
                httppost.entity = UrlEncodedFormEntity(nameValuePairs)
                httpclient.execute(httppost)
            } catch (e: ClientProtocolException) {
                // TODO Auto-generated catch block
            } catch (e: IOException) {
                // TODO Auto-generated catch block
            }
            println("FINISHED")
        }
    }

}
