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
import android.support.annotation.RequiresApi
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.*
import java.io.InterruptedIOException
import java.nio.channels.InterruptedByTimeoutException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var advertisements = ArrayList<Int>()
    private var imageView: ImageView? = null
    private var continueThread = true

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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        //window.decorView.setBackgroundColor(Color.parseColor("#24E8EA"))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        verticalLayout {
            imageView(R.drawable.robotour_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
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
        advertisements.clear()
        advertisements.add(R.drawable.your_ad_here)
        advertisements.add(R.drawable.new_exhibit)
        advertisements.add(R.drawable.gift_shop)
        pictureThread.start()
        super.onResume()
    }

    override fun onStop() {
        pictureThread.interrupt()
        super.onStop()
    }

    private val pictureThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        var a = 0

        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
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
                } catch (e: InterruptedByTimeoutException) {
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
