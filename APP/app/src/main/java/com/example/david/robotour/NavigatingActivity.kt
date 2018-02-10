package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import java.io.IOException
import java.net.URL

class NavigatingActivity : AppCompatActivity() {
    val btnHgt = 77
    var btnTextSize = 24f
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
    var imageView: ImageView? = null
    var titleView: TextView? = null
    var descriptionView: TextView? = null

    @SuppressLint("SetTextI18n")
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
                skip = "Skip Painting"
                skipDesc = "Are you sure you want to skip to the next painting?"
                stop = "Stop RoboTour"
                stopDesc = "Are you sure you want to stop RoboTour?"
                cancelTour = "Cancel tour"
                cancelDesc = "Are you sure you want to cancel the tour?"
                exit = "Exit"
                exitDesc = "Do you want to go to the exit?"
                toilet = "Toilet"
                toiletDesc = "Do you want to go to the toilet?"
                changeSpeed = "Change speed"
            }
            "French" -> {
                positive = "Oui"
                negative = "Non"
                skip = "Sauter Peinture"
                skipDesc = "Êtes-vous sûr de vouloir passer à la peinture suivante?"
                stop = "Arrêtez RoboTour"
                stopDesc = "Êtes-vous sûr de vouloir arrêter RoboTour?"
                cancelTour = "Annuler Visite"
                cancelDesc = "Êtes-vous sûr de vouloir annuler la visite?"
                exit = "Sortie"
                exitDesc = "Voulez-vous aller à la sortie?"
                toilet = "W.C."
                toiletDesc = "Voulez-vous aller aux toilettes?"
                changeSpeed = "Changer Vitesse"
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
                positive = "Sí"
                negative = "No."
                skip = "Saltar Pintura"
                skipDesc = "¿Estás seguro de que quieres saltar a la próxima pintura?"
                stop = "Detener RoboTour"
                stopDesc = "¿Estás seguro de que quieres detener RoboTour?"
                cancelTour = "Cancelar RoboTour"
                cancelDesc = "¿Seguro que quieres cancelar el tour?"
                exit = "Salida"
                exitDesc = "¿Quieres ir a la salida?"
                toilet = "Baño"
                toiletDesc = "¿Quieres ir al baño?"
                changeSpeed = "Cambiar Velocidad"
            }
            "German" -> {
                positive = "Ja"
                negative = "Nein"
                skip = "Bild Überspringen"
                skipDesc = "Wollen Sie dieses Bild Überspringen?"
                stop = "RoboTour Stoppen"
                stopDesc = "Möchtest Sie RoboTour stoppen?"
                cancelTour = "Tour abbrechen"
                cancelDesc = "Möchten Sie die Tour wirklich abbrechen?"
                exit = "Ausgang"
                exitDesc = "Wollen sie zum Ausgang gehen?"
                toilet = "W.C."
                toiletDesc = "Wollen sie zum W.C. gehen?"
                changeSpeed = "Geschwindig keit ändern"
                btnTextSize = 20f
            }
            else -> {
                positive = "Yes"
                negative = "No"
                skip = "Skip Painting"
                skipDesc = "Are you sure you want to skip to the next painting?"
                stop = "Stop RoboTour"
                stopDesc = "Are you sure you want to stop RoboTour?"
                cancelTour = "Cancel tour"
                cancelDesc = "Are you sure you want to cancel the tour?"
                exit = "Exit"
                exitDesc = "Do you want to go to the exit?"
                toilet = "W.C."
                toiletDesc = "Do you want to go to the toilet?"
                changeSpeed = "Change speed"
            }
        }
        verticalLayout {
            titleView = textView {
                textSize = 32f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(5)
                gravity = Gravity.CENTER_HORIZONTAL
            }
            //get image the pictures.php file that is true
            imageView = imageView {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams {
                bottomMargin = dip(10)
                topMargin = dip(10)
            }
            //view.setImageDrawable(resources.getDrawable(allArtPieces[1].imageID))
            descriptionView = textView {
                text = "Thank you for waiting"
                textSize = 16f
                typeface = Typeface.DEFAULT
                padding = dip(10)
            }
            tableLayout {
                isStretchAllColumns = true
                tableRow {
                    button(skip) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = btnTextSize
                        height = dip(btnHgt)
                        width = wrapContent
                        onClick {
                            alert(skipDesc) {
                                positiveButton(positive) {
                                    async {
                                        skip()
                                    }
                                }
                                negativeButton(negative) {
                                    //Do nothing
                                }
                            }.show()
                        }
                    }.lparams { leftMargin = dip(2); rightMargin = dip(6) }
                    button(stop) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = btnTextSize
                        height = dip(btnHgt)
                        width = wrapContent
                        onClick {
                            alert(stopDesc) {
                                positiveButton(positive) {
                                    async {
                                        stopRoboTour() /*This function will call for RoboTour to be stopped*/
                                    }
                                }
                                negativeButton(negative) { }
                            }.show()
                        }
                    }.lparams { rightMargin = 2 }
                }.lparams { bottomMargin = dip(7) }
                tableRow {
                    button(cancelTour) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = btnTextSize
                        height = dip(btnHgt)
                        width = matchParent
                        onClick {
                            alert(cancelDesc) {
                                positiveButton(positive) {
                                    async {
                                        cancelGuideTotal()
                                    }
                                }
                                negativeButton(negative) {
                                    onBackPressed() //Call on back pressed to take them back to the main activity
                                }
                            }.show()
                        }
                    }.lparams { leftMargin = dip(2); rightMargin = dip(6) }
                    button(changeSpeed) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = btnTextSize
                        height = dip(btnHgt)
                        width = matchParent
                        onClick {
                            alert {
                                customView {
                                    verticalLayout {
                                        listView {
                                            val options: List<String>
                                            val SelectSpeed:String
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
                    }.lparams { rightMargin = 2 }
                }.lparams { bottomMargin = dip(7) }
                tableRow {
                    button(toilet) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = btnTextSize
                        height = dip(btnHgt)
                        width = matchParent
                        onClick {
                            alert(toiletDesc) {
                                positiveButton(positive) {
                                    async {
                                        sendPUT("T", "http://homepages.inf.ed.ac.uk/s1553593/toilet.php")
                                    }
                                }
                                negativeButton(negative) { }
                            }.show()
                        }
                    }.lparams { leftMargin = dip(2); rightMargin = dip(6) }
                    button(exit) {
                        background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                        textSize = btnTextSize
                        height = dip(btnHgt)
                        width = matchParent
                        onClick {
                            alert(exitDesc) {
                                positiveButton(positive) {
                                    async{
                                        sendPUT("T", "http://homepages.inf.ed.ac.uk/s1553593/exit.php")
                                    }
                                }
                                negativeButton(negative) { }
                            }.show()
                        }
                    }.lparams { rightMargin = 2 }
                }.lparams { bottomMargin = dip(7) }
            }
        }
        Thread.sleep(4000)
        //imageView?.setImageResource(allArtPieces[5].imageID)
        titleView?.text = "RoboTour calculating optimal route"
        //descriptionView?.text = allArtPieces[5].English_Desc
        async {
            t.run()
        }

    }

    val t: Thread = object : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    Thread.sleep(1500) //1000ms = 1 sec
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            async {
                                for (i in 0..9) {
                                    val a = URL("http://homepages.inf.ed.ac.uk/s1553593/$i.php").readText()
                                    if (a == "N") {
                                        //  imageView?.setImageResource(allArtPieces[i].imageID)
                                        //  titleView?.text = allArtPieces[i].name
                                        //  descriptionView?.text = allArtPieces[i].English_Desc
                                        runOnUiThread {
                                            //Change the image, text and descrioption
                                            imageView?.setImageResource(allArtPieces[i].imageID)
                                            titleView?.text = allArtPieces[i].name
                                            val language = intent.getStringExtra("language")
                                            when (language) {
                                                "French" -> descriptionView?.text =allArtPieces[i].French_Desc
                                                "Chinese" -> descriptionView?.text = allArtPieces[i].Chinese_Desc
                                                "Spanish" -> descriptionView?.text =  allArtPieces[i].Spanish_Desc
                                                "German" -> descriptionView?.text = allArtPieces[i].German_Desc
                                                else -> descriptionView?.text = allArtPieces[i].English_Desc
                                            }
                                        }
                                        break
                                    }

                                }
                            }
                            async {
                                val a = URL("http://homepages.inf.ed.ac.uk/s1553593/skip.php").readText()
                                if (a == "2") {
                                    alert("") {
                                        positiveButton("Yes") {
                                            async {
                                                skipImmediately()
                                            }
                                        }
                                        negativeButton("No") {
                                            async {
                                                rejectSkip()
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    })
                } catch (e: InterruptedException) {
                    //e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
        t.interrupt()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    fun cancelGuideTotal() {
        sendPUT(userid, "http://homepages.inf.ed.ac.uk/s1553593/$userid.php")
        switchToMain()
    }

    fun switchToMain() {
        startActivity<MainActivity>()
    }

    fun showWaitingForPartner() {
        alert("Waiting for other users response") {
            title = "Waiting for other users response"
        }.show()
    }

    fun rejectSkip() {
        async {
            sendPUT("N", "http://homepages.inf.ed.ac.uk/s1553593/skip.php")
        }
    }

    fun skipImmediately() {
        /*This function is only when both users have agreed to skip the next item*/
        async {
            sendPUT("Y", "http://homepages.inf.ed.ac.uk/s1553593/skip.php")
        }
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
        } catch (e: IOException) {
        }
    }
}