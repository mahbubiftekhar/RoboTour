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
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    /* override the back button, so the user is promoted when they wish to leave the app */
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
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
        scrollView {
            //Added in scroll view to work with horizontal orientation
            verticalLayout {
                imageView(R.drawable.robotour_small) {
                    backgroundColor = Color.TRANSPARENT //Removes gray border
                }.lparams {
                    bottomMargin = dip(40)
                    topMargin = dip(10)
                }
                button("START") {
                    textSize = 32f
                    background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                    onClick {
                        if (isNetworkConnected()) {
                            startActivity<SelectLanguageActivity>()
                        } else {
                            Toast.makeText(applicationContext,"Check network connection then try again",Toast.LENGTH_LONG).show()
                        }
                    }
                   // onLongClick { startActivity<TempActivity>(); true }
                }
                var on = true
                /*toggleButton {
                //Commented out for CD2
                    onClick { on = !on }
                    text = "Single User"
                    textOn = "Multi User"
                    textOff = "Single User"
                }*/
            }
        }

    }
}
