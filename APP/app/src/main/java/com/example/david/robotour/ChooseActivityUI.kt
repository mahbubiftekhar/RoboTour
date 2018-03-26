package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import org.jetbrains.anko.*


class ChooseActivityUI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var tourAvailable = false
        supportActionBar?.hide() //hide actionbar
        verticalLayout {
            textView {
                text = "Looking For An Available Robot..."
                textSize = 32f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                gravity = Gravity.CENTER_HORIZONTAL
            }
            val pb = progressBar {
                lparams { topMargin = dip(30); gravity = Gravity.CENTER_HORIZONTAL }
                scaleX = 2f
                scaleY = 2f
            }
            //This code changes a progressBar color programmatically :O
            pb.indeterminateDrawable.setColorFilter(resources.getColor(R.color.roboTourTeal),android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        if (!tourAvailable) {
            alert {
                customView {
                    linearLayout {
                        orientation = LinearLayout.VERTICAL
                        textView {
                            text = "No Robot Available."
                        }
                        textView {
                            text = "Would you like to wait for a robot to become available or follow an existing tour?"
                        }
                    }
                }
                positiveButton("Wait"){}
                negativeButton("Follow") {
                    startActivity<WaitingActivity>("follow" to true)
                }
                //text = "No Tour is available would you like to wait for a robot to become available or follow an existing tour" -> second option take them to the waiting activity "Waiting for controllers to make selections"
            }.show()
        }
    }
}
