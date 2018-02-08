package com.example.david.robotour

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import org.jetbrains.anko.*

class PicturesUI(private val PicturesAdapter: PicturesAdapter, val language: String) : AnkoComponent<PicturesActivity> {
    lateinit var a: String
    var navigate = ""

    override fun createView(ui: AnkoContext<PicturesActivity>): View = with(ui) {
        return relativeLayout {
            when (language) {
                "Spanish" -> {
                    a = "Empezar recorrido"
                    navigate = "Navegar a obras de arte seleccionadas?"
                }
                "German" -> {
                    a = "Tour starten"
                    navigate = "zu ausgewählten Bildern navigieren?"
                }
                "French" -> {
                    a = "Tour initial"
                    navigate = "naviguer vers l'illustration sélectionnée?"
                }
                "Chinese" -> {
                    a = "开始旅游"
                    navigate = "导航到选定的艺术品？"
                }
                else -> {
                    a = "Start tour"
                    navigate = "navigate to selected artwork?"
                }
            }
            linearLayout {
                listView {
                    adapter = PicturesAdapter
                    //val selected = BooleanArray(adapter.count, { _ -> false})
                    onItemClick { _, view, i, _ ->
                        val picID = PicturesAdapter.getItem(i)
                        if (allArtPieces[picID].selected) {
                            view?.background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                        } else {
                            view?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                        }
                        allArtPieces[picID].selected = !allArtPieces[picID].selected
                    }
                }.lparams { width = matchParent; height = dip(0); weight = 1.0f }
                button(a) {
                    textSize = 32f
                    background = ColorDrawable(resources.getColor(R.color.roboTourTeal))
                    onClick {
                        //need to translate here
                        alert(navigate) {
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

                }.lparams { width = matchParent; height = wrapContent; weight = 0.0f }
                lparams { width = matchParent; height = matchParent; orientation = LinearLayout.VERTICAL }
            }
        }
    }
}