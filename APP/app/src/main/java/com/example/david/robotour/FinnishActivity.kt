package com.example.david.robotour

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

import kotlinx.android.synthetic.main.activity_finnish.*
import org.jetbrains.anko.startActivity

class FinnishActivity : AppCompatActivity() {
    /*This activity will be shown to the user when they */

    override fun onBackPressed() {
        startActivity<SelectLanguageActivity>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finnish)
        setSupportActionBar(toolbar)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings

    }
}