package com.example.david.robotour

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.WindowManager
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import java.io.IOException
import java.util.*

class SelectLanguageActivity : AppCompatActivity() {
    data class Language(val name: String, val imageID: Int)

    private val languages = ArrayList<Language>()

    override fun onBackPressed() {
        async {
            val a = loadInt("user")
            when (a) {
                1 -> sendPUTNEW(16, "F")
                2 -> sendPUTNEW(17, "F")
                else -> {
                    //Do nothing
                }
            }
        }
        startActivity<MainActivity>()
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

    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getInt(key, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // add back button to actionbar
        supportActionBar?.title = "Select your language"
        //Populate List
        languages.add(Language("Chinese", R.drawable.chineseflag_fixed))
        languages.add(Language("French", R.drawable.frenchflag))
        languages.add(Language("German", R.drawable.germanflag))
        languages.add(Language("Spanish", R.drawable.spanishflag))
        languages.add(Language("English", R.drawable.ukflag))
        languages.add(Language("No Language", R.drawable.otherpicture))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings

        //UI
        linearLayout {
            tableLayout {
                //Loop through List
                for (i in 0..4 step 2) {
                    tableRow {
                        for (j in 0..1) {
                            imageButton(languages[i + j].imageID) {
                                backgroundColor = Color.TRANSPARENT
                                onClick {
                                    startActivity<ChooseActivity>("language" to languages[i + j].name)
                                }
                            }.lparams { topMargin = dip(15); leftMargin = dip(20 + (j + 1) * 10) }
                        }
                    }
                }
            }
        }
    }

    //Define Functions upon actionbar button pressed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        //Back button moves back to MainActivity
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
