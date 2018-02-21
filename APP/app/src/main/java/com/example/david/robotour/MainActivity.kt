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
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.*


class MainActivity : AppCompatActivity() {
    private var count = 0
    private var advertisements = ArrayList<Int>()
    private var imageView: ImageView? = null
    private var continueThread = true


    override fun onBackPressed() {
        clearFindViewByIdCache()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
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

    private fun alternateUser() {
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

    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
              * return true, otherwise false*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Populate Advertisements
        advertisements.add(R.drawable.your_ad_here)
        advertisements.add(R.drawable.new_exhibit)
        advertisements.add(R.drawable.gift_shop)
        supportActionBar?.hide() //hide actionbar
        //Added in scroll view to work with horizontal orientation
        verticalLayout {
            imageView(R.drawable.robotour_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
            }.lparams {
                //  bottomMargin = dip(40)
                // topMargin = dip(10)
            }
            button("START") {
                textSize = 32f
                background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                onClick {
                    if (isNetworkConnected()) {
                        continueThread = false
                        interuptPicturesThread()
                        startActivity<SelectLanguageActivity>()
                    } else {
                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                    }
                }
                onLongClick {
                    if (count < 5) {
                        count++
                        vibrate()
                    } else {
                        alternateUser()
                    }
                    true
                }
            }
            //var on = true
            /*toggleButton {
            //Commented out for CD2
                onClick { on = !on }
                text = "Single User"
                textOn = "Multi User"
                textOff = "Single User"
            }*/
            imageView = imageView {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
        async {
            //This thread essentially starts the pictures at the bottom of the screen
           pictureThread.start()
        }
    }


    private val pictureThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        var a = 0
        override fun run() {
            while (!isInterrupted && continueThread) {
                println("+++ running here")
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
                }
            }
        }

    }

    private fun interuptPicturesThread(){
        pictureThread.interrupt()
    }
}
