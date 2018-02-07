package com.example.david.robotour

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Gravity
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.customView
import java.io.IOException

class NavigatingActivity : AppCompatActivity() {
    val userid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigating)
        supportActionBar?.hide() //hide actionbar

        //Obtain language from PicturesUI
        val language = intent.getStringExtra("language")

        async {
            getPicture() //Asynchronously get the picture
            uiThread {
                createView(language)
            }
        }
    }

    private fun createView(language: String) {
        verticalLayout {
            textView() {
                text = allArtPieces[0].name
                textSize = 32f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                gravity = Gravity.CENTER_HORIZONTAL
            }
            //get image the pictures.php file that is true
            imageView(allArtPieces[0].imageID) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams {
                bottomMargin = dip(10)
                topMargin = dip(10)
            }
            textView() {
                when (language) {
                    "English" -> text = allArtPieces[0].English_Desc
                    "French" -> text = allArtPieces[0].French_Desc
                    "Chinese" -> text = allArtPieces[0].Chinese_Desc
                    "Spanish" -> text = allArtPieces[0].Spanish_Desc
                    "German" -> text = allArtPieces[0].German_Desc
                    else -> text = ""
                }
                textSize = 16f
                typeface = Typeface.DEFAULT
                padding = dip(10)
            }
            tableLayout {
                isStretchAllColumns = true
                tableRow {
                    button("Skip to Next Painting") {
                        textSize = 24f
                        width = wrapContent
                        onClick {
                            alert("Are you sure you want to skip to the next painting?") {
                                positiveButton { }
                                negativeButton { }
                            }.show()
                        }
                    }
                    button("Stop RoboTour") {
                        textSize = 24f
                        width = wrapContent
                        onClick {
                            alert("Do you want to go stop RoboTour?") {
                                positiveButton { }
                                negativeButton { }
                            }.show()
                        }
                    }
                }.lparams { bottomMargin = dip(10) }
                tableRow {
                    button("Cancel Tour") {
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert("Are you sure you want to cancel the tour?") {
                                positiveButton { }
                                negativeButton { }
                            }.show()
                        }
                    }
                    button("Change Speed") {
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert {
                                customView {
                                    verticalLayout {
                                        listView {
                                            val options = listOf("Slow", "Normal", "Fast")
                                            selector("Select Speed", options) { j ->
                                                if (j == 0) {
                                                    toast("Slow")
                                                } else if (j == 1) {
                                                    toast("Normal")
                                                } else {
                                                    toast("Fast")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.lparams { bottomMargin = dip(10) }
                tableRow {
                    button("Take me to the toilet") {
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert("Do you want to go to the toilet?") {
                                positiveButton { }
                                negativeButton { }
                            }.show()
                        }
                    }
                    button("Take me to the exit") {
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert("Do you want to go to the exit?") {
                                positiveButton { }
                                negativeButton { }
                            }.show()
                        }
                    }
                }.lparams { bottomMargin = dip(10) }
            }
        }
    }

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun cancelGuideTotal() {

    }


    fun skip() {

    }

    fun sendPUT(command: String, url: String) {
        async {
            val httpclient = DefaultHttpClient()
            val httppost = HttpPost(url)
            try {
                val nameValuePairs = ArrayList<NameValuePair>(4)
                nameValuePairs.add(BasicNameValuePair("command", command))
                httppost.entity = UrlEncodedFormEntity(nameValuePairs)
                httpclient.execute(httppost)
            } catch (e: ClientProtocolException) {
                // TODO Auto-generated catch block
            } catch (e: IOException) {
                // TODO Auto-generated catch block
            }
            println("FINISHED")
        }
    }

    private fun getPicture() {

    }


}