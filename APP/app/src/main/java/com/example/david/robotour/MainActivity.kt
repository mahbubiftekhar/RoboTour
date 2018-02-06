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
                    topMargin = dip(10)
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

    @Suppress("DEPRECATION") // Removes some of the warming messages, can't do much about the import warnings though
    fun uploadToServer(command: String) {
        async {
            val httpclient = DefaultHttpClient()
            val httpPost = HttpPost("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")
            try {
                val nameValuePairs = ArrayList<NameValuePair>(4)
                nameValuePairs.add(BasicNameValuePair("command", "Command: $command"))
                httpPost.entity = UrlEncodedFormEntity(nameValuePairs)
                httpclient.execute(httpPost)
            } catch (e: ClientProtocolException) {
                // TODO Auto-generated catch block
            } catch (e: IOException) {
                // TODO Auto-generated catch block
            }
        }
    }
}
