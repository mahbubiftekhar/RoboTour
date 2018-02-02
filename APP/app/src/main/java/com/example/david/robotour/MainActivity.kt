package com.example.david.robotour

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.jetbrains.anko.*
import java.io.IOException
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair

class MainActivity : AppCompatActivity() {

    /* override the back button, so the user is promted when they wish to leave the app */
    override fun onBackPressed() {
        /* override the back button, so the user is promted when they wish to leave the app */
        alert("Are you sure you want to exit?") {
            positiveButton {
                /*The user wishes to close the app, so be it - there loss*/
                super.onBackPressed() //Call the normal onBackPressed to take user back
            }
            negativeButton {
                /*If the user changes their minds*/
            }
        }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        //UI
        scrollView {
            //Addded in scroll view to work with horizontal orientation
            verticalLayout {
                imageView(R.drawable.robotour_small) {
                    backgroundColor = Color.TRANSPARENT //Removes gray border
                }.lparams {
                    bottomMargin = dip(4)
                    topMargin = dip(20)
                }
                button("Start") {
                    textSize = 32f
                    onClick { startActivity<SelectLanguageActivity>() }
                    onLongClick { startActivity<TempActivity>(); true } //This line should be removed after Client Demo 1
                }
            }
        }
        uploadToServer("TESTING") /*Adds the message to the server, JUST CHANGE THE PARAMETER HERE, DO NOT CHANGE THE METHOD*/
    }

    @Suppress("DEPRECATION") // Removes some of the messages, can't do much about the import warnings though
    fun uploadToServer(command: String) {
        async {
            val httpclient = DefaultHttpClient()
            val httppost = HttpPost("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")
            try {
                val nameValuePairs = ArrayList<NameValuePair>(4)
                nameValuePairs.add(BasicNameValuePair("command", "Command: $command"))
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
}
