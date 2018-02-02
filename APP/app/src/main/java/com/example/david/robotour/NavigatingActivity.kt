package com.example.david.robotour

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.async

class NavigatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigating)
        supportActionBar?.hide() //hide actionbar
        async{getPicture()} //Asynchrounously get the picture
    }

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
        async{cancelGuide()} // Asynchronously cancel the guide - better UI experience
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun cancelGuide(){
        // This function should cancel the guide
    }

    fun getPicture(){

    }


}
