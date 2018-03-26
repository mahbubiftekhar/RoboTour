package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import org.jetbrains.anko.*
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.*

@Suppress("DEPRECATION")
var url = "http://www.mahbubiftekhar.co.uk/receiver.php"
class MainActivity : AppCompatActivity() {
    private var continueThread = true
    private var url = ""
    private var count = 0

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
        if (url == "https://proparoxytone-icing.000webhostapp.com/receiverPhone.php") {
            toast("Warning, in receiverPhone mode")
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        verticalLayout {
            imageView(R.drawable.robotour_img2) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                onLongClick {
                    count++
                    true
                }
                horizontalPadding = dip(20)
                verticalPadding = dip(25)
            }
            button("START") {
                textSize = 32f
                //background = ResourcesCompat.getDrawable(resources, R.drawable.rb2, null) Using XML
                background = buttonBg() // Using kotlin - better ;)
                lparams { width = matchParent; horizontalMargin = dip(5); topMargin = dip(5) }
                onClick {
                    if (isNetworkConnected()) {
                        continueThread = false
                        startActivity<SelectLanguageActivity>()
                    } else {
                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                    }
                }
                onLongClick {
                    if(count>0){
                        switchToAdmin()
                    }
                    true
                }
            }
        }
    }

    private fun buttonBg() = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 100f
        setColor(resources.getColor(R.color.roboTourTeal))
        setStroke(2, Color.BLACK)
    }
}
