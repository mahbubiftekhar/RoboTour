package com.example.david.robotour

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import org.jetbrains.anko.*
import android.content.Intent


class MainActivity : AppCompatActivity() {
    /* override the back button, so the user is promoted when they wish to leave the app */
    override fun onBackPressed(){
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
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
                        bottomMargin = dip(10)
                        topMargin = dip(10)
                    }
                    button("Start") {
                        textSize = 32f
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        onClick { startActivity<SelectLanguageActivity>() }
                        onLongClick { startActivity<TempActivity>(); true }
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
