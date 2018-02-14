package com.example.david.robotour

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import org.jetbrains.anko.*
import android.content.Intent
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions

class MainActivity : AppCompatActivity() {
    private val API_KEY = "32ac0b5a9a4b0edd2714ea6e7c14b0956b683ad0"

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
        async {
            println("+++Start async")
            val options = TranslateOptions.newBuilder().setApiKey(API_KEY).build()
            println("+++2")
            val translate = options.service
            println("+++3")
            val translation = translate.translate("Hello World", Translate.TranslateOption.targetLanguage("de"))
            println("+++4")
            runOnUiThread{
                println("+++getting here")
                println("+++"+translation)
            }
        }
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
                    toggleButton {
                        onClick { on = !on }
                        text = "Single User"
                        textOn = "Multi User"
                        textOff = "Single User"
                    }
                }
            }

    }
}
