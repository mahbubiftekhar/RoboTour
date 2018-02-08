package com.example.david.robotour

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import org.jetbrains.anko.*

class PicturesUI(private val PicturesAdapter: PicturesAdapter, val language: String) : AnkoComponent<PicturesActivity> {
    lateinit var a: String
    var navigate = ""

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
                }.lparams{bottomMargin = dip(60)}
            }
            when (language) {
                "Spanish" -> {
                    a = "Empezar recorrido"
                    navigate = "zu ausgewählten Bildern navigieren?"
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