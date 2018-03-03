package com.example.david.robotour

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

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

    }
}