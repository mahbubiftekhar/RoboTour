package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import org.jetbrains.anko.*
import android.content.Intent
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.*
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.IOException
import java.io.InterruptedIOException


@Suppress("DEPRECATION")
var url = ""
class MainActivity : AppCompatActivity() {
    private var advertisements = ArrayList<Int>()
    private var imageView: ImageView? = null
    private var continueThread = true
    private var url = ""

    override fun onBackPressed() {
        clearFindViewByIdCache()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
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

    private fun loadString(key: String): String {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getString(key, "https://proparoxytone-icing.000webhostapp.com/receiver.php")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        url = loadString("url")
        if(url=="https://proparoxytone-icing.000webhostapp.com/receiverPhone.php"){
            toast("Warning, in receiverPhone mode")
        }
        //window.decorView.setBackgroundColor(Color.parseColor("#24E8EA"))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        verticalLayout {
            imageView(R.drawable.robotour_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                onClick{

                }
            }
            button("START") {
                textSize = 32f
                background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                onClick {
                    if (isNetworkConnected()) {
                        continueThread = false
                        interuptPicturesThread()
                        pictureThread.interrupt()
                        startActivity<SelectLanguageActivity>()
                    } else {
                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                    }
                }
                onLongClick {
                    interuptPicturesThread()
                    switchToAdmin()
                    true
                }
            }
            imageView = imageView {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (pictureThread.state == Thread.State.NEW) {
            advertisements.clear()
            advertisements.add(R.drawable.your_ad_here)
            advertisements.add(R.drawable.new_exhibit)
            advertisements.add(R.drawable.gift_shop)
            pictureThread.start()
        }
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

    override fun onStop() {
        pictureThread.interrupt()
        super.onStop()
    }

    private val pictureThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        var a = 0

        override fun run() {
            while (!isInterrupted) {
                println("+++ running here main activity")
                if (a > (advertisements.size - 1)) {
                    //Reset A to avoid null pointers
                    a = 0
                }
                try {
                    //UI thread MUST be updates on the UI thread, other threads may not update the UI thread
                    runOnUiThread {
                        imageView?.setImageResource(advertisements[a])
                    }
                    Thread.sleep(3000)
                    a++
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                }
            }
            Thread.currentThread().interrupt()
        }
    }

    private fun interuptPicturesThread() {
        pictureThread.interrupt()
    }
}
