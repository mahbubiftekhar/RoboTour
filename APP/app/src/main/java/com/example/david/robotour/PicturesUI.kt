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
    override fun createView(ui: AnkoContext<PicturesActivity>): View = with(ui) {
        return relativeLayout() {
            verticalLayout {
                listView {
                    adapter = PicturesAdapter
                    //val selected = BooleanArray(adapter.count, { _ -> false})
                    onItemClick { _, view, i, _ ->
                        /*when (language) {
                            "English" -> {
                                alert("Do you want to go to this painting?") {
                                    positiveButton("Yes") {
                                        val progressDialog = indeterminateProgressDialog("Stuff")
                                        //if 2 users
                                        progressDialog.show()
                                        async {
                                            //replace this with once other user has pressed button
                                            Thread.sleep(2000)
                                            uiThread {
                                                progressDialog.dismiss()
                                            }
                                        }

                                    }
                                    negativeButton("No") { }
                                }.show()
                            }
                            "German" -> {
                                alert("Wollen Sie zu diesem Bild gehen?") {
                                    positiveButton("Ja") { }
                                    negativeButton("Nein") { }
                                }.show()
                            }
                            "Chinese" -> {
                                alert("你想去这幅画吗？") {
                                    positiveButton("是") { }
                                    negativeButton("没有") { }
                                }.show()
                            }
                            "French" -> {
                                alert("Voulez-vous aller à cette peinture?") {
                                    positiveButton("Oui") { }
                                    negativeButton("Non") { }
                                }.show()
                            }
                            "Spanish" -> {
                                alert("Quieres ir a esta pintura?") {
                                    positiveButton("Sí") { }
                                    negativeButton("No") { }
                                }.show()
                            }
                            else -> {
                                alert("Follow RoboTour") {
                                    positiveButton("YES"){

                                    }
                                    negativeButton("NO"){

                                    }
                                }.show()
                            }
                        } */
                        if (!PicturesAdapter.selected[i]) {
                            view?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                        } else {
                            view?.background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                        }
                        PicturesAdapter.selected[i] = !PicturesAdapter.selected[i]
                    }
                }
            }
             button("Start Tour") {
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