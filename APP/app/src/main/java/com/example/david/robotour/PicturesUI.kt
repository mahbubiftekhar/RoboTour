package com.example.david.robotour

import android.view.View
import org.jetbrains.anko.*

class PicturesUI(private val PicturesAdapter : PicturesAdapter, val language:String) : AnkoComponent<PicturesActivity> {
    override fun createView(ui: AnkoContext<PicturesActivity>): View = with(ui) {
        return relativeLayout {
            verticalLayout {
                listView {
                    adapter = PicturesAdapter

                    onItemClick { _, _, _, _ ->
                        when (language) {
                            "English" -> {
                                alert("Do you want to go to this painting?") {
                                    positiveButton("Yes") { }
                                    negativeButton("No") { }
                                }.show()
                            }
                            "German" -> {
                                alert("Willst du zu diesem Bild gehen?") {
                                    positiveButton("Ja") { }
                                    negativeButton("Nah") { }
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
                        }
                    }
                }
            }
        }
    }
}