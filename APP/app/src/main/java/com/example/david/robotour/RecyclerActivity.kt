package com.example.david.robotour

import android.app.ActionBar
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout

import kotlinx.android.synthetic.main.activity_recycler.*
import org.jetbrains.anko.*
import java.util.*

class RecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listPaintings = ArrayList<View>()

        relativeLayout {
            val nextPaintings = textView {
                id = View.generateViewId()
                text = "Next Art Pieces:"
            }.lparams { alignParentTop() }
            val hSV = horizontalScrollView {
                id = View.generateViewId()
                linearLayout {
                    for (i in allArtPieces) {
                        listPaintings.add(
                           imageButton {
                               image = resources.getDrawable(i.imageID)
                               onClick {
                                   //toast(i.name)

                               }
                           }
                        )
                    }
                    val removableImage = imageView {
                        image = resources.getDrawable(R.drawable.girlwithpearlearring)
                    }
                    if (true) {
                        listPaintings[0].visibility = View.GONE
                    }
                }
            }.lparams { below(nextPaintings) }

            linearLayout {
                orientation = LinearLayout.VERTICAL

            }.lparams {
                below(hSV)
            }

        }
    }
}
