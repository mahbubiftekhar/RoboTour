package com.example.david.robotour

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.Button
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.floatingActionButton
class PicturesUI(private val PicturesAdapter : PicturesAdapter, val language:String) : AnkoComponent<PicturesActivity> {
    lateinit var a: String

    override fun createView(ui: AnkoContext<PicturesActivity>): View = with(ui) {
        return relativeLayout() {
            verticalLayout {
                listView {
                    adapter = PicturesAdapter
                    //val selected = BooleanArray(adapter.count, { _ -> false})
                    onItemClick { _, view, i, _ ->

                        if (!PicturesAdapter.selected[i]) {
                            view?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                        } else {
                            view?.background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                        }
                        PicturesAdapter.selected[i] = !PicturesAdapter.selected[i]
                    }
                }
            }
            when (language) {
                "Spanish" -> {a = "Empezar recorrido"}
                "German" -> {a = "Tour starten"}
                "French" -> {a = "Tour initial"}
                "Chinese" -> {a = "Start tour"}
                else -> {a = "Start tour"}
            }
             button(a) {
                textSize = 32f
                background = ColorDrawable(resources.getColor(R.color.roboTourTeal))
                onClick {
                    alert("Do you want RoboTour to guide you to the highlighted paintings?") {
                        positiveButton("Yes") {
                            val progressDialog = indeterminateProgressDialog("Waiting for other user to select paintings...")
                            //if 2 users
                            progressDialog.show()
                            async {
                                //replace this with once other user has pressed button
                                Thread.sleep(2000)
                                uiThread {
                                    startActivity<NavigatingActivity>("language" to language)
                                }
                            }

                        }
                        negativeButton("No") { }
                    }.show()
                }

            }.lparams {
                //setting button to bottom right of the screen
                width = matchParent
                height = wrapContent
                alignParentBottom()
                gravity = Gravity.BOTTOM
            }
        }
    }
}