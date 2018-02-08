package com.example.david.robotour

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Gravity
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import java.io.IOException

class NavigatingActivity : AppCompatActivity() {

    var buttonTitle = ""
    var positive = ""
    var negative = ""
    var skip = ""
    var skipDesc = ""
    val userid = ""
    var stop = ""
    var stopDesc = ""
    var cancelTour = ""
    var cancelDesc = ""
    var exit = ""
    var exitDesc = ""
    var toilet = ""
    var toiletDesc = ""
    var changeSpeed = ""

    fun constantCheck() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigating)
        supportActionBar?.hide() //hide actionbar

        //Obtain language from PicturesUI
        val language = intent.getStringExtra("language")
        when (language) {
            "English" -> {
                positive = "Yes"
                negative = "No"
                skip = "Skip to next Painting"
                skipDesc = "Are you sure you want to skip to the next painting?"
                stop = "Stop RoboTour"
                stopDesc = "Are you sure you want to stop RoboTour?"
                cancelTour = "Cancel tour"
                cancelDesc = "Are you sure you want to cancel the tour?"
                exit = "Take me to the exit"
                exitDesc = "Do you want to go to the exit?"
                toilet = "Take me to the toilet"
                toiletDesc = "Do you want to go to the toilet?"
                changeSpeed = "Change speed"
            }
            "French" -> {
                positive = "Oui"
                negative = "Non"
                skip = "Passer à la suivante"
                skipDesc = "Êtes-vous sûr de vouloir passer à la peinture suivante?"
                stop = "Arrêtez RoboTour"
                stopDesc = "Êtes-vous sûr de vouloir arrêter RoboTour?"
                cancelTour = "Annuler la visite"
                cancelDesc = "Êtes-vous sûr de vouloir annuler la visite?"
                exit = "Emmenez-moi à la sortie"
                exitDesc = "Voulez-vous aller à la sortie?"
                toilet = "Emmène-moi aux toilettes"
                toiletDesc = "Voulez-vous aller aux toilettes?"
                changeSpeed = "Changer la vitesse"
            }
            "Chinese" -> {
                positive = "是的"
                negative = "没有"
                skip = "跳到下一幅绘画"
                skipDesc = "你确定要跳到下一张画吗？"
                stop = "停止RoboTour"
                stopDesc = "你确定要停止RoboTour？"
                cancelTour = "取消游览"
                cancelDesc = "你确定要取消游览吗？"
                exit = "带我去出口"
                exitDesc = "带我去出口"
                toilet = "带我去厕所"
                toiletDesc = "你想上厕所吗？"
                changeSpeed = "改变速度"
            }
            "Spanish" -> {
                positive = "sí"
                negative = "No."
                skip = "¿Estás seguro de que quieres saltar a la próxima pintura"
                skipDesc = "¿Estás seguro de que quieres saltar a la próxima pintura?"
                stop = "Stop RoboTour"
                stopDesc = "Are you sure you want to stop RoboTour?"
                cancelTour = "Cancell robotour"
                cancelDesc = "¿Seguro que quieres cancelar el tour?"
                exit = "Llévame a la salida"
                exitDesc = "¿Quieres ir a la salida?"
                toilet = "Llévame al baño"
                toiletDesc = "¿Quieres ir al baño?"
                changeSpeed = "Cambiar la velocidad"
            }
            "German" -> {
                positive = "ja"
                negative = "Nein"
                skip = "Springe zum nächsten Bild"
                skipDesc = "Überspringen?"
                stop = "Stoppen Sie RoboTour"
                stopDesc = "Möchtest du RoboTour wirklich stoppen?"
                cancelTour = "Tour abbrechen"
                cancelDesc = "Möchtest du die Tour wirklich abbrechen?"
                exit = "Bring mich zum Ausgang"
                exitDesc = "Willst du zum Ausgang gehen?"
                toilet = "Bring mich auf die Toilette"
                toiletDesc = "Willst du auf die Toilette gehen?"
                changeSpeed = "Geschwindigkeit ändern"
            }
            else -> {
                positive = "Yes"
                negative = "No"
                skip = "Skip to next Painting"
                skipDesc = "Are you sure you want to skip to the next painting?"
                stop = "Stop RoboTour"
                stopDesc = "Are you sure you want to stop RoboTour?"
                cancelTour = "Cancel tour"
                cancelDesc = "Are you sure you want to cancel the tour?"
                exit = "Take me to the exit"
                exitDesc = "Do you want to go to the exit?"
                toilet = "Take me to the toilet"
                toiletDesc = "Do you want to go to the toilet?"
                changeSpeed = "Change speed"
            }
        }
        async {
            getPicture() //Asynchronously get the picture
            uiThread {
                createView(language)
            }
        }
    }

    private fun createView(language: String) {
        verticalLayout {
            textView {
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
            textView {
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
                    button(skip) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = 24f
                        width = wrapContent
                        onClick {
                            alert(skipDesc) {
                                positiveButton(positive) {
                                    skip()
                                }
                                negativeButton(negative) {
                                    //Do nothing
                                }
                            }.show()
                        }
                    }.lparams { leftMargin=dip(2) ; rightMargin=dip(6) }
                    button(stop) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = 24f
                        width = wrapContent
                        onClick {
                            alert(stopDesc) {
                                positiveButton(positive) {
                                    stopRoboTour() /*This function will call for RoboTour to be stopped*/
                                }
                                negativeButton(negative) { }
                            }.show()
                        }
                    }.lparams { rightMargin=2 }
                }.lparams { bottomMargin = dip(10) }
                tableRow {
                    button(cancelTour) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert(cancelDesc) {
                                positiveButton(positive) {
                                    cancelGuideTotal()
                                }
                                negativeButton(negative) { }
                            }.show()
                        }
                    }.lparams { leftMargin=dip(2) ; rightMargin=dip(6) }
                    button(changeSpeed) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert {
                                customView {
                                    verticalLayout {
                                        listView {
                                            val options: List<String>
                                            var SelectSpeed = ""
                                            when (language) {
                                                "English" -> {
                                                    options = listOf("Slow", "Normal", "Fast")
                                                    SelectSpeed = "Select speed"
                                                }
                                                "French" -> {
                                                    options = listOf("lent", "Ordinaire", "vite")
                                                    SelectSpeed = "Sélectionnez la vitesse"
                                                }
                                                "Chinese" -> {
                                                    options = listOf("慢", "正常", "快速")
                                                    SelectSpeed = "选择速度"
                                                }
                                                "Spanish" -> {
                                                    options = listOf("lento", "Normal", "rápido")
                                                    SelectSpeed = "Seleccionar velocidad"
                                                }
                                                "German" -> {
                                                    options = listOf("Langsam", "Normal", "Schnell")
                                                    SelectSpeed = "Wählen Sie Geschwindigkeit"
                                                }
                                                else -> {
                                                    options = listOf("Slow", "Normal", "Fast")
                                                    SelectSpeed = "Select speed"
                                                }
                                            }
                                            selector(SelectSpeed, options) { j ->
                                                if (j == 0) {
                                                    toast(options[0])
                                                } else if (j == 1) {
                                                    toast(options[1])
                                                } else {
                                                    toast(options[2])
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }.lparams { rightMargin=2 }
                }.lparams { bottomMargin = dip(10) }
                tableRow {
                    button(toilet) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert(toiletDesc) {
                                positiveButton(positive) { }
                                negativeButton(negative) { }
                            }.show()
                        }
                    }.lparams { leftMargin=dip(2) ; rightMargin=dip(6) }
                    button(exit) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = 24f
                        width = matchParent
                        onClick {
                            alert(exitDesc) {
                                positiveButton(positive) { }
                                negativeButton(negative) { }
                            }.show()
                        }
                    }.lparams { rightMargin=2 }
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
        sendPUT(userid, "http://homepages.inf.ed.ac.uk/s1553593/$userid.php")
        switchToMain()
    }
    fun switchToMain(){
        startActivity<TempActivity>()
    }

    fun showWaitingForPartner() {
        /*This function should show a pop up saying "waiting for partner to respond, */
    }

    fun skip() {
        async {
            sendPUT(userid, "http://homepages.inf.ed.ac.uk/s1553593/skip.php")
        }
    }

    fun stopRoboTour() {
        async {
            sendPUT("T", "http://homepages.inf.ed.ac.uk/s1553593/stop.php")
        }
    }

    fun sendPUT(command: String, url: String) {
        /*DISCLAIMER: When calling this function, if you don't run in an async, you will get
        * as security exception - just a heads up */
        val httpclient = DefaultHttpClient()
        val httppost = HttpPost(url)
        try {
            val nameValuePairs = ArrayList<NameValuePair>(4)
            nameValuePairs.add(BasicNameValuePair("command", command))
            httppost.entity = UrlEncodedFormEntity(nameValuePairs)
            httpclient.execute(httppost)
        } catch (e: ClientProtocolException) {
        } catch (e: IOException) { }
    }

    private fun getPicture() {

    }


}