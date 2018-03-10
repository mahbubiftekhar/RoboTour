package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.res.ResourcesCompat
import kotlinx.android.synthetic.*


@Suppress("DEPRECATION")
class FinishActivity : AppCompatActivity() {
    /*This activity will be shown to the user when they cancel or finish the tour */
    private lateinit var closeApp: String
    private lateinit var restartApp: String

    override fun onBackPressed() {
        //Restart the app cleanly
        val i = baseContext.packageManager
                .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
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
            "Chinese" -> "感谢您使用萝卜途\n希望您喜欢这次旅程"
            else -> "Thank you for using RoboTour.\nWe hope you enjoyed your tour."
        }
        when (language) {
            "French" -> {
                restartApp = "START"
                closeApp = "FERMER APP"
            }
            "German" -> {
                restartApp = "ANFANG"
                closeApp = "SCHLIEßE APP"
            }
            "Spanish" -> {
                restartApp = "COMIENZO"
                closeApp = "CERRAR APP"
            }
            "Chinese" -> {
                restartApp = "开始"
                closeApp = "关闭APP"
            }
            else -> {
                restartApp = "START"
                closeApp = "CLOSE APP"
            }
        }

        verticalLayout {
            imageView(R.drawable.robotour_small) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                onClick {
                    val i = baseContext.packageManager
                            .getLaunchIntentForPackage(baseContext.packageName)
                    i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                }
            }
            textView {
                textSize = 24f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                topPadding = dip(20)
                gravity = Gravity.CENTER
                text = message
                setTextColor(resources.getColor(R.color.roboTourTeal))
            }
            button(restartApp) {
                textSize = 20f
                background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)

                onClick {
                    //Restart the app cleanly
                    val i = baseContext.packageManager
                            .getLaunchIntentForPackage(baseContext.packageName)
                    i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                }
            }
            verticalLayout {
                button {
                    textSize = 20f
                    background = ColorDrawable(Color.parseColor("#FFFFFF"))
                }
            }
            button(closeApp) {
                textSize = 20f
                background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                onClick {
                    //Kill the app
                    clearFindViewByIdCache()
                    val closeTheApp = Intent(Intent.ACTION_MAIN)
                    closeTheApp.addCategory(Intent.CATEGORY_HOME)
                    closeTheApp.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(closeTheApp)
                }
            }
        }
    }
}