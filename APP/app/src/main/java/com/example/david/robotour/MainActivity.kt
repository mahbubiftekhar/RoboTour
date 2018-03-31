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
import android.widget.Toast
import kotlinx.android.synthetic.*
import android.text.method.TextKeyListener.clear
import android.R.id.edit
import android.content.SharedPreferences



@Suppress("DEPRECATION")
var url = "http://www.mahbubiftekhar.co.uk/receiver.php"

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
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

    private fun saveInt(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        async{
            saveInt("user", 1)
        }
        val a = loadInt("urlnum")
        //background = ResourcesCompat.getDrawable(resources, R.drawable.rb2, null) Using XML
        // Using kotlin - better ;)
        //Removes gray border

        when (a) {
            1 -> {
                url = "http://www.mahbubiftekhar.co.uk/receiver.php"
                saveInt("urlnum",1)
            }
            2 -> {
                url = "http://www.mahbubiftekhar.co.uk/receiver2.php"
                toast("WARNING!!!: receiver2 1&1")
                saveInt("urlnum",2)
            }
            3 -> {
                url = "http://homepages.inf.ed.ac.uk/s1539308/receiver.php"
                toast("WARNING!!!: homepages receiver")
                saveInt("urlnum",3)
            }
        //This will keep the screen on, overriding users settings
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        verticalLayout {
            imageView(R.drawable.robotour_img2) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                onLongClick {
                    switchToAdmin()
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

    private fun buttonBg() = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 100f
        setColor(resources.getColor(R.color.roboTourTeal))
        setStroke(2, Color.BLACK)
    }
}
