package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.*
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.view.WindowManager
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

@Suppress("DEPRECATION")
var url = "http://www.mahbubiftekhar.co.uk/receiver.php"

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var continueThread = true
    private var userGoThrough = false
    override fun onBackPressed() {
        clearFindViewByIdCache()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onDestroy() {
        try {
            updateTextThread.interrupt()
        } catch (e: Exception) {

        }
        super.onDestroy()
    }

    private fun switchToAdmin() {
        startActivity<AdminActivity>()
    }

    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
              * return true, otherwise false*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    @SuppressLint("ApplySharedPref")
    private fun saveInt(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    private fun sendPUTNEW(identifier: Int, command: String) {
       /* /*DISCLAIMER: When calling this function, if you don't run in an async, you will get
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
        }*/
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        userGoThrough = false
        async {
            saveInt("user", 1)
        }
        val a = loadInt("urlnum")

        when (a) {
            1 -> {
                url = "http://www.mahbubiftekhar.co.uk/receiver.php"
                saveInt("urlnum", 1)
            }
            2 -> {
                url = "http://www.mahbubiftekhar.co.uk/receiver2.php"
                toast("WARNING!!!: receiver2 1&1")
                saveInt("urlnum", 2)
            }
            3 -> {
                url = "http://homepages.inf.ed.ac.uk/s1539308/receiver.php"
                toast("WARNING!!!: homepages receiver")
                saveInt("urlnum", 3)
            }
            4 -> {
                url = "https://proparoxytone-icing.000webhostapp.com/receiver.php"
                toast("WARNING!!!: 000webHost receiver")
                saveInt("urlnum", 4)
            }
        }
        try {
            for (i in 0..25) {
                async {
                   // sendPUTNEW(i, "F")
                }
            }
        } catch (e: Exception) {
            toast("error occurred, try again")
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        verticalLayout {
            imageView(R.drawable.robotour_img_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                onLongClick {
                    switchToAdmin()
                    true
                }
                horizontalPadding = dip(10)
                verticalPadding = dip(15)
            }
            button("START") {
                textSize = 32f
                //background = ResourcesCompat.getDrawable(resources, R.drawable.rb2, null) Using XML
                background = buttonBg() // Using kotlin - better ;)
                lparams { width = matchParent; horizontalMargin = dip(5); topMargin = dip(5) }
                onClick {
                    startActivity<SelectLanguageActivity>()

                    /*  if (!userGoThrough) {
                          toast("RoboTour not online")
                      } else if (isNetworkConnected()) {
                          continueThread = false
                          startActivity<SelectLanguageActivity>()
                      } else {
                          toast("No Network - Please investigate")
                      } */
                }
                onLongClick {
                    startActivity<InfoForumDemoActivity>()
                    true
                }
            }
        }
    }

    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        return sharedPreferences.getInt(key, 1)
    }

    private val updateTextThread: Thread = object : Thread() {
        /*This thread is used to update the header*/
        /*Will update the header automatically*/
        override fun run() {
            while (!isInterrupted) {
                try {
                    async {
                        val a = URL(url).readText()
                        userGoThrough = a[18].toString() == 1.toString() || a[18].toString().toInt() == 2
                    }
                    try {
                        Thread.sleep(1000)
                    } catch (e: Exception) {

                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                }
            }
            Thread.currentThread().interrupt()
        }
    }

    override fun onResume() {
        userGoThrough = false
        if (updateTextThread.state == Thread.State.NEW) {
            updateTextThread.start()
        }
        super.onResume()
    }

    private fun buttonBg() = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 100f
        setColor(resources.getColor(R.color.roboTourTeal))
        setStroke(2, Color.BLACK)
    }
}
