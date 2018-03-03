package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*

class FinishActivity : AppCompatActivity() {
    /*This activity will be shown to the user when they cancel or finish the tour */

    override fun onBackPressed() {
        startActivity<SelectLanguageActivity>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val language = intent.getStringExtra("language")
        val message: String
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        message = when (language) {
            "French" -> "Merci d'utiliser RoboTour.\nNous espérons que vous avez apprécié votre visite."
            "German" -> "Vielen Dank für die Verwendung von RoboTour.\nWir hoffen, Sie haben Ihre Tour genossen."
            "Spanish" -> "Gracias por usar RoboTour.\nEsperamos que hayas disfrutado tu recorrido."
            "Chinese" -> "感谢您使用 RoboTour\n我们希望你喜欢你的旅程"
            else -> "Thank you for using RoboTour.\nWe hope you enjoyed your tour."
        }
        verticalLayout {
            imageView(R.drawable.robotour_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
            }
            textView {
                textSize = 34f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = Gravity.CENTER
                text = message
                setTextColor(resources.getColor(R.color.roboTourTeal))
            }
        }

    }
}