package com.example.david.robotour

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.async
import org.jetbrains.anko.startActivity

class Waiting : AppCompatActivity() {

    var language = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)
        supportActionBar?.hide() //hide actionbar
        language = intent.getStringExtra("language")
        t.run()
    }

    fun switchToNavigate(){
        startActivity<NavigatingActivity>("language" to language)
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
    }
    fun checkUser1():Boolean {
        return true
    }
    fun checkUser2():Boolean {
        return true 
    }
    val t: Thread = object : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    Thread.sleep(1500) //1000ms = 1 sec
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            async {

                            }
                        }
                    })
                } catch (e: InterruptedException) {

                }
            }
        }
    }


}
