package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v4.content.res.ResourcesCompat
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private val btnHgt = 77
    private var btnTextSize = 24f
    private var toggleStBtn = true
    private var alertStBtn = ""
    private var positive = ""
    private var negative = ""
    private var skip = ""
    private var skipDesc = ""
    private var userid = ""
    private var stop = ""
    private var stopDesc = ""
    private var start = ""
    private var startDesc = ""
    private var cancelTour = ""
    private var cancelDesc = ""
    private var exit = ""
    private var exitDesc = ""
    private var toilet = ""
    private var toiletDesc = ""
    private var changeSpeed = ""
    private var imageView: ImageView? = null
    private var titleView: TextView? = null
    private var descriptionView: TextView? = null
    private var stopButton: Button? = null
    private var Skippable = true
    private lateinit var t: Thread

    private fun LoadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val savedValue = sharedPreferences.getInt(key, 0)
        return savedValue
        //        val a = LoadInt("user")

    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        userid = LoadInt("user").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigating)
        supportActionBar?.hide() //hide actionbar
        //Obtain language from PicturesUI
        vibrate()
        val language = intent.getStringExtra("language")
        when (language) {
            "English" -> {
                positive = "Yes"
                negative = "No"
                skip = "Skip Painting"
                skipDesc = "Are you sure you want to skip to the next painting?"
                stop = "Stop RoboTour"
                stopDesc = "Are you sure you want to stop RoboTour?"
                start = "Start RoboTour"
                startDesc = "Do you want to start RoboTour?"
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
                start = "Démarrer RoboTour"
                startDesc = "Voulez-vous démarrer RoboTour?"
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
                negative = "不是"
                skip = "跳到下一幅作品"
                skipDesc = "你确定要跳到下一幅作品吗？"
                stop = "停止RoboTour"
                stopDesc = "你确定要停止RoboTour？"
                start = "開始RoboTour"
                startDesc = "你想開始RoboTour？"
                cancelTour = "取消游览"
                cancelDesc = "你确定要取消游览吗？"
                exit = "带我去出口"
                exitDesc = "你确定要去出口吗？"
                toilet = "带我去厕所"
                toiletDesc = "你确定要去厕所吗？"
                changeSpeed = "改变速度"
            }
            "Spanish" -> {
                positive = "Sí"
                negative = "No."
                skip = "Saltar Pintura"
                skipDesc = "¿Estás seguro de que quieres saltar a la próxima pintura?"
                stop = "Detener RoboTour"
                stopDesc = "¿Estás seguro de que quieres detener RoboTour?"
                start = "Iniciar RoboTour"
                startDesc = "¿Quieres iniciar RoboTour?"
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
                stopDesc = "Möchten Sie RoboTour stoppen?"
                start = "RoboTour Starten"
                startDesc = "Möchten Sie RoboTour starten?"
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
                start = "Start RoboTour"
                startDesc = "Do you want to start RoboTour?"
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
                setImageResource(R.drawable.robotourfornavigating)
                backgroundColor = Color.TRANSPARENT //Removes gray border
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams {
                bottomMargin = dip(10)
                topMargin = dip(10)
            }
            descriptionView = textView {
                text = ""
                textSize = 16f
                typeface = Typeface.DEFAULT
                padding = dip(10)
            }
            relativeLayout {
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
                                        if (isNetworkConnected()) {
                                            async {
                                                skip()
                                            }
                                        } else {
                                            Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                        }

                                    }
                                    negativeButton(negative) {
                                        //Do nothing the user changed their minds
                                    }
                                }.show()
                            }
                        }.lparams { leftMargin = dip(2); rightMargin = dip(6) }
                        stopButton = button(stop) {
                            background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                            textSize = btnTextSize
                            height = dip(btnHgt)
                            width = wrapContent
                            onClick {
                                if (toggleStBtn) {
                                    alertStBtn = stopDesc
                                } else {
                                    alertStBtn = startDesc
                                }
                                alert(alertStBtn) {
                                    positiveButton(positive) {
                                        if (isNetworkConnected()) {
                                            if (!toggleStBtn) {
                                                text = stop
                                                async {
                                                    stopRoboTour() /*This function will call for RoboTour to be stopped*/
                                                }
                                            } else {
                                                text = start
                                                async {
                                                    startRoboTour()
                                                }
                                            }
                                            toggleStBtn = !toggleStBtn
                                        } else {
                                            Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    negativeButton(negative) { }
                                }.show()
                            }
                        }.lparams { rightMargin = 2 }
                    }.lparams { bottomMargin = dip(8) }
                    tableRow {
                        button(cancelTour) {
                            background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                            textSize = btnTextSize
                            height = dip(btnHgt)
                            width = matchParent
                            onClick {
                                alert(cancelDesc) {
                                    positiveButton(positive) {
                                        if (isNetworkConnected()) {
                                            async {
                                                cancelGuideTotal()
                                            }
                                        } else {
                                            Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
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
                                                val SelectSpeed: String
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
                    }.lparams { bottomMargin = dip(8) }
                    tableRow {
                        button(toilet) {
                            background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                            textSize = btnTextSize
                            height = dip(btnHgt)
                            width = matchParent
                            onClick {
                                alert(toiletDesc) {
                                    positiveButton(positive) {
                                        if (isNetworkConnected()) {
                                            async {
                                                sendPUT("T", "http://homepages.inf.ed.ac.uk/s1553593/toilet.php")
                                            }
                                        } else {
                                            Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
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
                                        if (isNetworkConnected()) {
                                            async {
                                                sendPUT("T", "http://homepages.inf.ed.ac.uk/s1553593/exit.php")
                                            }
                                            t.interrupt()
                                            switchToMain()
                                        } else {
                                            Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                        }

                                    }
                                    negativeButton(negative) { }
                                }.show()
                            }
                        }.lparams { rightMargin = 2 }
                    }.lparams { bottomMargin = dip(15) }
                }.lparams { alignParentBottom() }
            }

        }
        when (language) {
            "English" -> titleView?.text = "RoboTour calculating optimal route..."
            "German" -> titleView?.text = "RoboTour berechnet optimale Route ..."
            "Spanish" -> titleView?.text = "RoboTour calcula la ruta óptima ..."
            "French" -> titleView?.text = "RoboTour calculant l'itinéraire optimal ..."
            "Chinese" -> titleView?.text = "RoboTour计算最佳路线..."
            "other" -> titleView?.text = "RoboTour calculating optimal route..."
            "else" -> titleView?.text = "RoboTour calculating optimal route..."
        }

        t = object : Thread() {
            override fun run() {
                while (!isInterrupted) {
                    try {
                        Thread.sleep(1000) //1000ms = 1 sec
                        runOnUiThread(object : Runnable {
                            override fun run() {
                                async {
                                    for (i in 0..9) {
                                        //This part checks for updates of the next location we are going to
                                        val a = URL("http://homepages.inf.ed.ac.uk/s1553593/$i.php").readText()
                                        if (a == "N") {
                                            runOnUiThread {
                                                //Change the image, text and descrioption
                                                imageView?.setImageResource(allArtPieces[i].imageID)
                                                titleView?.text = allArtPieces[i].name
                                                val language = intent.getStringExtra("language")
                                                when (language) {
                                                    "French" -> descriptionView?.text = allArtPieces[i].French_Desc
                                                    "Chinese" -> descriptionView?.text = allArtPieces[i].Chinese_Desc
                                                    "Spanish" -> descriptionView?.text = allArtPieces[i].Spanish_Desc
                                                    "German" -> descriptionView?.text = allArtPieces[i].German_Desc
                                                    else -> descriptionView?.text = allArtPieces[i].English_Desc
                                                }
                                            }
                                            break
                                        }
                                    }
                                }
                                async {
                                    Thread.sleep(200)
                                    val a = URL("http://homepages.inf.ed.ac.uk/s1553593/skip.php").readText()
                                    if (a == "2" && Skippable) {
                                        Skippable = false
                                        runOnUiThread {
                                            alert(skip) {
                                                cancellable(false)
                                                setFinishOnTouchOutside(false)
                                                positiveButton(positive) {
                                                    if (isNetworkConnected()) {
                                                        skipImmediately()
                                                    } else {
                                                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                                negativeButton(negative) {
                                                    if (isNetworkConnected()) {
                                                        rejectSkip()
                                                    } else {
                                                        Skippable = true /*This will mean when the network is reestablished, the pop up will come again*/
                                                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }.show()
                                        }
                                    }
                                }
                                async {
                                    //This part checks if the other user has pressed the stop buttons and updates accordingly
                                    val a = URL("http://homepages.inf.ed.ac.uk/s1553593/stop.php").readText()
                                    if (a == "T") {
                                        runOnUiThread {
                                            toggleStBtn = true
                                            stopButton!!.text = start
                                        }
                                    } else {
                                        runOnUiThread {
                                            stopButton!!.text = stop
                                            toggleStBtn = false
                                        }
                                    }
                                }
                            }
                        }
                        )
                    } catch (e: InterruptedException) {
                    }
                }
            }
        }
        async {
            //Starting the thread which is defined above to keep polling the server
            t.run()
        }

    }

    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
              * return true, otherwise false*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun toiletAlert() {
        alert {
            Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
       alert(exitDesc){
           positiveButton(positive){
               t.interrupt()
               switchToMain()
           }
           negativeButton(negative) {
               /*Do nothing*/
           }
       }.show()
    }

    fun cancelGuideTotal() {
        sendPUT(userid, "http://homepages.inf.ed.ac.uk/s1553593/$userid.php")
        switchToMain()
    }

    fun switchToMain() {
        startActivity<MainActivity>()
    }

    fun rejectSkip() {
        async {
            //This function will reject the skip by adding the empty string
            sendPUT(" ", "http://homepages.inf.ed.ac.uk/s1553593/skip.php")
        }
    }
    private fun vibrate() {
        if (Build.VERSION.SDK_INT > 25) { /*Attempt to not use the deprecated version if possible, if the SDK version is >25, use the newer one*/
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(300, 10))
        } else {
            /*for backward comparability*/
            @Suppress("DEPRECATION")
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(300)
        }
    }

    fun skipImmediately() {
        /*This function is only when both users have agreed to skip the next item*/
        async {
            sendPUT("Y", "http://homepages.inf.ed.ac.uk/s1553593/skip.php")
            Thread.sleep(400)
            Skippable = true
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

    fun startRoboTour() {
        async {
            sendPUT("F", "http://homepages.inf.ed.ac.uk/s1553593/stop.php")
        }
    }

    fun sendPUT(command: String, url: String) {
        /*DISCLAIMER: When calling this function, if you don't run in an async, you will get
        * as security exception - just a heads up */
        val httpclient = DefaultHttpClient()
        val httPpost = HttpPost(url)
        try {
            val nameValuePairs = ArrayList<NameValuePair>(4)
            nameValuePairs.add(BasicNameValuePair("command", command))
            httPpost.entity = UrlEncodedFormEntity(nameValuePairs)
            httpclient.execute(httPpost)
        } catch (e: ClientProtocolException) {
        } catch (e: IOException) {
        }
    }
}