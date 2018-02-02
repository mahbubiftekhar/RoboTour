package com.example.david.robotour

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ScrollView
import org.jetbrains.anko.*

class SelectLanguageActivity : AppCompatActivity() {

    data class Language(val name: String, val imageID: Int)

    private val languages = ArrayList<Language>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // add back button to actionbar

        supportActionBar?.title = "Select Language"

        //Populate List
        languages.add(Language("Chinese", R.drawable.chineseflag_fixed))
        languages.add(Language("French", R.drawable.frenchflag))
        languages.add(Language("German", R.drawable.germanflag))
        languages.add(Language("Spanish", R.drawable.spanishflag))
        languages.add(Language("English", R.drawable.ukflag))
        languages.add(Language("No Language", R.drawable.otherpicture))

        //UI
        tableLayout {
            //Loop through List
            for (i in 0..4 step 2) {
                tableRow {
                    for (j in 0..1) {
                        imageButton(languages[i + j].imageID) {
                            backgroundColor = Color.TRANSPARENT
                            onClick { startActivity<PicturesActivity>("language" to languages[i + j].name) }
                        }.lparams { topMargin = dip(15); leftMargin = dip(20 + (j + 1) * 10) }
                    }
                }
            }
        }
    }

    //Define Functions upon actionbar button pressed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        //Back button moves back to MainActivity
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        /* override the back button, so the user is promted when they wish to leave the app */
        alert("Are you sure you want to exit?") {
            positiveButton {
                /*The user wishes to close the app, so be it - there loss*/
                super.onBackPressed() //Call the normal onBackPressed to take user back
            }
            negativeButton {
                /*If the user changes their minds*/
            }
        }.show()
    }

}