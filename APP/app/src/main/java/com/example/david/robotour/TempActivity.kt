package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_temp.*
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.intellij.lang.annotations.Language
import org.jetbrains.anko.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class TempActivity : AppCompatActivity() {
    val btnHgt = 77
    var btnTextSize = 24f
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
    var language2 = ""
    var running = true
    var updateTo = ""
    lateinit var textView_title: TextView
    lateinit var textView_Text: TextView
    lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)
        //Obtain language from PicturesUI
        val language = intent.getStringExtra("language")
        textView_title = findViewById(R.id.title_text)
        textView_Text = findViewById(R.id.description_Text)
        image = findViewById(R.id.artworkPic)
        language2 = language
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

        STOP.setOnClickListener {
            alert(stopDesc) {
                positiveButton(positive) {
                    stopRoboTour() /*This function will call for RoboTour to be stopped*/
                }
                negativeButton(negative) { }
            }.show()
        }

        TOILET.setOnClickListener {
            alert(toiletDesc) {
                positiveButton(positive) { }
                negativeButton(negative) { }
            }.show()
        }
        EXIT.setOnClickListener {
            alert(exitDesc) {
                positiveButton(positive) { }
                negativeButton(negative) { }
            }.show()
        }
        SKIP.setOnClickListener {
            alert(skipDesc) {
                positiveButton(positive) {
                    skip()
                }
                negativeButton(negative) {
                    //Do nothing
                    //imageView.setImageResource(allArtPieces[5].imageID)
                    //titleView.text = allArtPieces[5].name
                    //descriptionView.text = allArtPieces[5].English_Desc
                }
            }.show()
        }
        SPEED.setOnClickListener {
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
        EXIT.setOnClickListener {
            updateTextAndImage(9)
            alert(exitDesc) {
                positiveButton(positive) { }
                negativeButton(negative) { }
            }.show()
        }
        //Launch the thread to check that
        val a = rcv_Ack()
        a.start()

    }

    fun updateTextAndImage(id: Int) {
        runOnUiThread {
            updateHeader(allArtPieces[id].name)
            when (language2) {
                "English" -> updateText(allArtPieces[id].English_Desc)
                "German" -> updateText(allArtPieces[id].German_Desc)
                "Spanish" -> updateText(allArtPieces[id].Spanish_Desc)
                "Chinese" -> updateText(allArtPieces[id].Chinese_Desc)
                "French" -> updateText(allArtPieces[id].French_Desc)
                else -> updateText(allArtPieces[id].English_Desc)
            }
            updateText(allArtPieces[id].English_Desc)
            updateImage(9)
        }
    }



    fun updateHeader(text: String) {
        /*Update selected style text on screen*/
        textView_title.text = text
        textView_title.gravity = Gravity.CENTER
        textView_title.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
    }

    fun updateText(text: String) {
        /*Update selected style text on screen*/
        textView_Text.text = text
    }

    // Thread that receives acknowledgements
    class rcv_Ack : Thread() {
        val a = TempActivity()
        override fun run() {
            while (true) {
                a.constantCheck()
            }
        }
    }

    fun updateImage(imageID: Int) {
        //try {
        if (android.os.Build.VERSION.SDK_INT > 15) {
            // for API above 15
            image.background = resources.getDrawable(R.drawable.waterlillies)
        } else {
            // for API below 15
            image.setBackgroundDrawable(resources.getDrawable(R.drawable.waterlillies))
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

    fun switchToMain() {
        startActivity<TempActivity>()
    }

    fun showWaitingForPartner() {
        /*This function should show a pop up saying "waiting for partner to respond, */
    }

    fun constantCheck() {
        for (i in 0..9) {
            val a = URL("http://homepages.inf.ed.ac.uk/s1553593/$i.php").readText()
            if (a == "N") {
                //updateTextAndImage(i)
                runOnUiThread {
                    textView_Text.text = "WORKING"
                    textView_title.text = "WORKING"
                }
                break
            }
        }
        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/skip.php").readText()
        if (a == "2") {
            alert("") {
                positiveButton("Yes") {
                    skipImmediately()
                }
                negativeButton("No") {
                    rejectSkip()
                }
            }
        }
        Thread.sleep(3000)
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