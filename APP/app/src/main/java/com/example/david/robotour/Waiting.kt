package com.example.david.robotour

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.async
import org.jetbrains.anko.startActivity
import java.net.URL

class Waiting : AppCompatActivity() {

    val user = 1
    var language = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)
        supportActionBar?.hide() //hide actionbar
        language = intent.getStringExtra("language") //Getting the language from the previous activity
        async {
            //Running the thread
            t.run()
        }
        //This part is temp until you make me the php files david
        t.interrupt()
        Thread.sleep(10000)
        switchToNavigate()
        //This above part is temproary
    }

    fun switchToNavigate() {
        t.interrupt() // Stop the thread
        startActivity<NavigatingActivity>("language" to language) // now we can switch the activity
    }

    override fun onBackPressed() {
        /*Overridden onBackPressed*/
    }

    fun checkUser1(): Boolean {
        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user1.php").readText()
        if (a == "Y") {
            return true
        }
        return false
    }

    fun checkUser2(): Boolean {
        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user2.php").readText()
        if (a == "Y") {
            return true
        }
        return false
    }

    val t: Thread = object : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    Thread.sleep(1000) //1000ms = 1 sec
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            async {
                                if (user == 1) {
                                    if (checkUser1()) {
                                        //If user 1 has made their selection and you are not user 1
                                        switchToNavigate()
                                    }
                                } else {
                                    //If user 2 has made their selection and you are not user 2
                                    if (checkUser1()) {
                                        switchToNavigate()
                                    }
                                }
                            }
                        }
                    })
                } catch (e: InterruptedException) {
                }
            }
        }
    }
}
