package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.support.annotation.RequiresApi
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.*
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import java.io.IOException
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException
import java.util.*

@Suppress("DEPRECATION")
class NavigatingActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
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
    private lateinit var toiletPopUp: AlertDialogBuilder
    private var skippable = true
    private var tts: TextToSpeech? = null
    private var tts2: TextToSpeech? = null
    private var currentPic = -1
    private var startRoboTour = ""
    private var toiletPopUpBool = true
    private var speaking = -1
    private var killThread = false
    private var userTwoMode = false

    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        return sharedPreferences.getInt(key, 0)
    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if (tts2 != null) {
            tts2!!.stop()
            tts2!!.shutdown()
        }
        if (userid == "1") {
            async {
                sendPUTNEW(16, "F")
            }
        } else if (userid == "2") {
            async {
                sendPUTNEW(17, "F")
            }
        }
        checkerThread.interrupt()
        super.onDestroy()
    }

    public override fun onStop() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if (tts2 != null) {
            tts2!!.stop()
            tts2!!.shutdown()
        }
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.DONUT)
    override fun onInit(status: Int) {
        println("status code: $status")
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val language = intent.getStringExtra("language")
            val result: Int
            when (language) {
                "French" -> {
                    result = tts!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts!!.setLanguage(Locale.UK)
                }
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {

        }
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val language = intent.getStringExtra("language")
            val result: Int
            when (language) {
                "French" -> {
                    result = tts2!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts2!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts2!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts2!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts2!!.setLanguage(Locale.UK)
                }
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
            }
        } else {

        }

    }

    private fun resetSpeech() {
        tts = null
        tts = TextToSpeech(this, this)
    }

    override fun onPause() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if (tts2 != null) {
            tts2!!.stop()
            tts2!!.shutdown()
        }
        super.onPause()
    }

    override fun onResume() {
        //This ensures that when the nav activity is minimized and reloaded up, the speech still works
        tts = TextToSpeech(this, this)
        tts2 = TextToSpeech(this, this)
        onInit(0)
        super.onResume()
    }

    private fun switchToFinnished() {
        if (userid == "1") {
            async {
                sendPUTNEW(16, "F")
            }
        } else if (userid == "2") {
            async {
                sendPUTNEW(17, "F")
            }
        }
        checkerThread.interrupt()
        clearFindViewByIdCache()
        startActivity<FinishActivity>("language" to intent.getStringExtra("language"))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        userid = loadInt("user").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigating)
        tts = TextToSpeech(this, this)
        tts2 = TextToSpeech(this, this)
        supportActionBar?.hide() //hide actionbar
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        vibrate()

        //Obtain language from PicturesUI
        val language = intent.getStringExtra("language")
        when (language) {
            "English" -> {
                positive = "Yes"
                negative = "No"
                skip = "SKIP"
                skipDesc = "Are you sure you want to skip to the next painting?"
                stop = "STOP"
                stopDesc = "Are you sure you want to stop RoboTour?"
                start = "CONTINUE"
                startDesc = "Do you want to start RoboTour?"
                cancelTour = "Cancel tour"
                cancelDesc = "Are you sure you want to cancel the tour?"
                exit = "Exit"
                exitDesc = "Do you want to go to the exit?"
                toilet = "Toilet"
                toiletDesc = "Do you want to go to the toilet?"
                changeSpeed = "Change speed"
                startRoboTour = "Press START when you are ready for RoboTour to resume"
            }
            "French" -> {
                startRoboTour = "Appuyez sur Start lorsque vous êtes prêt à reprendre RoboTour"
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
                startRoboTour = "当您准备好跟随萝卜途时，请按开始。"
                positive = "是的"
                negative = "不是"
                skip = "跳到下一幅作品"
                skipDesc = "确定要跳到下一幅作品吗？"
                stop = "停止萝卜途"
                stopDesc = "确定要停止萝卜途吗？"
                start = "开始萝卜途"
                startDesc = "确定开始萝卜途吗？"
                cancelTour = "取消游览"
                cancelDesc = "确定要取消游览吗？"
                exit = "带我去出口"
                exitDesc = "确定要去出口吗？"
                toilet = "带我去厕所"
                toiletDesc = "确定要去厕所吗？"
                changeSpeed = "改变速度"
            }
            "Spanish" -> {
                positive = "Sí"
                negative = "No."
                skip = "Saltar Pintura"
                skipDesc = "¿Estás seguro de que quieres saltar a la próxima pintura?"
                stop = "Detener RoboTour"
                startRoboTour = ""
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
                startRoboTour = "Drücken Sie Start, wenn Sie bereit sind für die Fortsetzung von RoboTour"
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
                startRoboTour = "Press start when you are ready for RoboTour to resume"
                positive = "Yes"
                negative = "No"
                skip = "SKIP"
                skipDesc = "Are you sure you want to skip to the next painting?"
                stop = "STOP"
                stopDesc = "Are you sure you want to stop RoboTour?"
                start = "CONTINUE"
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
        relativeLayout {
            floatingActionButton {
                //UI
                imageResource = R.drawable.ic_volume_up_black_24dp
                //ColorStateList usually requires a list of states but this works for a single color
                backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.roboTourTeal))
                lparams { alignParentRight(); topMargin = dip(100); rightMargin = dip(5) }

                //Text-to-speech
                onClick {
                    speakOutButton(currentPic)
                }
            }
            verticalLayout {
                lparams { width = matchParent }
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
                                    if (isNetworkConnected()) {
                                        alert(skipDesc) {
                                            positiveButton(positive) {
                                                skip()
                                            }
                                            negativeButton(negative) {
                                                //Do nothing the user changed their minds
                                            }
                                        }.show()
                                    } else {
                                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }.lparams { leftMargin = dip(2); rightMargin = dip(6) }
                            stopButton = button(stop) {
                                background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                                textSize = btnTextSize
                                height = dip(btnHgt)
                                width = wrapContent
                                onClick {
                                    if (isNetworkConnected()) {
                                        alertStBtn = if (toggleStBtn) {
                                            startDesc
                                        } else {
                                            stopDesc
                                        }
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
                                    if (isNetworkConnected()) {
                                        alert(cancelDesc) {
                                            positiveButton(positive) {
                                                checkerThread.interrupt()
                                                cancelGuideTotal()

                                            }
                                            negativeButton(negative) {
                                                onBackPressed()
                                                //Call on back pressed to take them back to the main activity
                                            }
                                        }.show()
                                    } else {
                                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                    }
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
                                                    val selectSpeed: String
                                                    when (language) {
                                                        "English" -> {
                                                            options = listOf("Slow", "Normal", "Fast")
                                                            selectSpeed = "Select speed"
                                                        }
                                                        "French" -> {
                                                            options = listOf("lent", "Ordinaire", "vite")
                                                            selectSpeed = "Sélectionnez la vitesse"
                                                        }
                                                        "Chinese" -> {
                                                            options = listOf("慢", "正常", "快")
                                                            selectSpeed = "选择速度"
                                                        }
                                                        "Spanish" -> {
                                                            options = listOf("lento", "Normal", "rápido")
                                                            selectSpeed = "Seleccionar velocidad"
                                                        }
                                                        "German" -> {
                                                            options = listOf("Langsam", "Normal", "Schnell")
                                                            selectSpeed = "Wählen Sie Geschwindigkeit"
                                                        }
                                                        else -> {
                                                            options = listOf("Slow", "Normal", "Fast")
                                                            selectSpeed = "Select speed"
                                                        }
                                                    }
                                                    selector(selectSpeed, options) { j ->
                                                        when (j) {
                                                            0 -> {
                                                                async {
                                                                    sendSpeed(1)
                                                                }
                                                                toast(options[0])

                                                            }
                                                            1 -> {
                                                                async {
                                                                    sendSpeed(2)
                                                                }
                                                                toast(options[1])
                                                            }
                                                            else -> {
                                                                async {
                                                                    sendSpeed(3)
                                                                }
                                                                toast(options[2])
                                                            }
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
                                                    sendPUTNEW(14, "T")
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
                                    if (isNetworkConnected()) {
                                        alert(exitDesc) {
                                            positiveButton(positive) {
                                                async {
                                                    exitDoor()
                                                }
                                                async {
                                                    var a = URL("http://homepages.inf.ed.ac.uk/s1553593/user1.php").readText()
                                                    if (a == "1") {
                                                        sendPUTNEW(12, "T")
                                                    }
                                                    a = URL("http://homepages.inf.ed.ac.uk/s1553593/user1.php").readText()
                                                    if (a[16] == 'T' && a[17] == 'T') {
                                                        sendPUTNEW(12, "T")
                                                    }

                                                }
                                            }
                                            negativeButton(negative) { }
                                        }.show()
                                    } else {
                                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()

                                    }

                                }
                            }.lparams { rightMargin = 2 }
                        }.lparams { bottomMargin = dip(15) }
                    }.lparams { alignParentBottom() }
                }

            }
        }
        when (language) {
            "English" -> {
                titleView?.text = "RoboTour calculating optimal route..."
                descriptionView?.text = "RoboTour calculating optimal route..."
                imageView?.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }
            "German" -> {
                titleView?.text = "RoboTour berechnet optimale Route ..."
                descriptionView?.text = "RoboTour berechnet optimale Route ..."
                imageView?.setBackgroundColor(resources.getColor(android.R.color.transparent))

            }
            "Spanish" -> {
                titleView?.text = "RoboTour calcula la ruta óptima ..."
                descriptionView?.text = "RoboTour calcula la ruta óptima ..."
                imageView?.setBackgroundColor(resources.getColor(android.R.color.transparent))

            }
            "French" -> {
                titleView?.text = "RoboTour calculant l'itinéraire optimal ..."
                descriptionView?.text = "RoboTour calculant l'itinéraire optimal ..."
                imageView?.setBackgroundColor(resources.getColor(android.R.color.transparent))

            }
            "Chinese" -> {
                titleView?.text = "萝卜途正在计算最佳路线..."
                descriptionView?.text = "萝卜途正在计算最佳路线..."
                imageView?.setBackgroundColor(resources.getColor(android.R.color.transparent))

            }
            "other" -> {
                titleView?.text = "RoboTour calculating optimal route..."
                descriptionView?.text = "RoboTour calculating optimal route..."
                imageView?.setBackgroundColor(resources.getColor(android.R.color.transparent))

            }
            "else" -> {
                titleView?.text = "RoboTour calculating optimal route..."
                descriptionView?.text = "RoboTour calculating optimal route..."
                imageView?.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }
        }
        //Starting the thread which is defined above to keep polling the server for changes
        checkerThread.start()
        speakOutButton(-1) // Speak "RoboTour is finding optimal route
    }

    /////
    private val checkerThread: Thread = object : Thread() {
        /*This thread will update the pictures, this feature can be sold as an advertisement opportunity as well*/
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            val language = intent.getStringExtra("language")
            while (!isInterrupted) {
                try {
                    Thread.sleep(1000) //1000ms = 1 sec
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            async {
                                val a = URL("http://homepages.inf.ed.ac.uk/s1553593/receiver.php").readText()
                                println("++++++++" + a)
                                /*This updates the picture and text for the user*/
                                val counter = (0..9).count { a[it] == 'F' }
                                println("+++counter: $counter")
                                if (counter == 10) {
                                    runOnUiThread {
                                        switchToFinnished()
                                        killThread = true
                                    }
                                }
                                for (i in 0..9) {
                                    if (a[14] == 'N') {
                                        runOnUiThread {
                                            imageView?.setImageResource(R.drawable.toiletimage)
                                            titleView?.text = toilet
                                            descriptionView?.text = ""
                                        }
                                        break
                                    }
                                    if (a[i] == 'A' && speaking != i) {
                                        /*This will mean that when the robot has arrived at the painting*/
                                        if (tts != null) {
                                            tts!!.stop()
                                        }
                                        runOnUiThread {
                                            currentPic = i // Set current pic to the one being shown
                                            resetSpeech()
                                            speaking = i
                                            //Change the image, text and descrioption
                                            imageView?.setImageResource(allArtPieces[i].imageID)
                                            val text: String = when (language) {
                                                "German" -> allArtPieces[i].nameGerman
                                                "French" -> allArtPieces[i].nameFrench
                                                "Spanish" -> allArtPieces[i].nameSpanish
                                                "Chinese" -> allArtPieces[i].nameChinese
                                                else -> allArtPieces[i].name
                                            }
                                            titleView?.text = text
                                            currentPic = i /*This is to allow for the pics description to be read out to the user*/
                                            when (intent.getStringExtra("language")) {
                                                "French" -> descriptionView?.text = allArtPieces[i].French_Desc
                                                "Chinese" -> descriptionView?.text = allArtPieces[i].Chinese_Desc
                                                "Spanish" -> descriptionView?.text = allArtPieces[i].Spanish_Desc
                                                "German" -> descriptionView?.text = allArtPieces[i].German_Desc
                                                else -> descriptionView?.text = allArtPieces[i].English_Desc
                                            }
                                        }
                                        speakOut(i)
                                        break
                                    }

                                    //Updates title
                                    if (a[i] == 'N') {
                                        runOnUiThread {
                                            //Change the image, text and descrioption
                                            imageView?.setImageResource(allArtPieces[i].imageID)
                                            val text: String = when (language) {
                                                "German" -> allArtPieces[i].nameGerman
                                                "French" -> allArtPieces[i].nameFrench
                                                "Spanish" -> allArtPieces[i].nameSpanish
                                                "Chinese" -> allArtPieces[i].nameChinese
                                                else -> allArtPieces[i].name

                                            }
                                            titleView?.text = text
                                            currentPic = i /*This is to allow for the pics description to be read out to the user*/
                                            when (intent.getStringExtra("language")) {
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
                                userTwoMode = a[16] == 'T' && a[17] == 'T'
                                println("+++" + userTwoMode)/* This checks if the both users are online, if both are then we are in user two mode, otherwise immediate skip is allowed */
                                if (userid == 1.toString()) {
                                    if (a[10].toInt() == 2 && skippable) {
                                        skippable = false
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
                                                        skippable = true /*This will mean when the network is reestablished, the pop up will come again*/
                                                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }.show()
                                        }
                                    }
                                } else if (userid == 2.toString()) {
                                    if (a[10].toInt() == 1 && skippable) {
                                        skippable = false
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
                                                        skippable = true /*This will mean when the network is reestablished, the pop up will come again*/
                                                        Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }.show()
                                        }
                                    }
                                }
                                if (a[14] == 'A' && toiletPopUpBool) {
                                    toiletPopUpBool = false
                                    runOnUiThread {
                                        toiletPopUp = alert(startRoboTour) {
                                            cancellable(false)
                                            setFinishOnTouchOutside(false)
                                            positiveButton(positive) {
                                                if (isNetworkConnected()) {
                                                    async {
                                                        sendPUTNEW(11, "F")
                                                    }
                                                } else {
                                                    Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }.show()
                                    }
                                }
                                if (a[11] == 'T') {
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
                            Thread.sleep(300)
                        }
                    }
                    )
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedIOException) {
                    Thread.currentThread().interrupt()
                } catch (e: InterruptedByTimeoutException) {
                    Thread.currentThread().interrupt()
                }
            }
            Thread.currentThread().interrupt()
        }
    }

    private fun sendSpeed(speed: Int) {
        //This function will send the speed to the server
        when (speed) {
            1 -> {
                sendPUTNEW(13, "1")
            }
            2 -> {
                sendPUTNEW(13, "2")

            }
            3 -> {
                sendPUTNEW(13, "3")

            }
            else -> {
                toast("Sorry that's not a valid input")
            }
        }
    }

    private fun speakOut(input: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            if (input != -1) {
                when (language) {
                    "French" -> {
                        text = allArtPieces[input].French_Desc
                    }
                    "Chinese" -> {
                        text = allArtPieces[input].Chinese_Desc
                    }
                    "Spanish" -> {
                        text = allArtPieces[input].Spanish_Desc
                    }
                    "German" -> {
                        text = allArtPieces[input].German_Desc
                    }
                    else -> {
                        text = allArtPieces[input].English_Desc
                    }
                }
                tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            } else {
                when (language) {
                    "French" -> {
                        text = "RoboTour calcule l'itinéraire optimal"
                    }
                    "Chinese" -> {
                        text = "萝卜途正在计算最佳路线"
                    }
                    "Spanish" -> {
                        text = "RoboTour está calculando la ruta óptima"
                    }
                    "German" -> {
                        text = "RoboTour berechnet die optimale Route"
                    }
                    else -> {
                        text = "Ro-bow-Tour is calculating the optimal route"
                        //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
                    }
                }
                tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    private fun speakOutButton(input: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            if (input != -1) {
                when (language) {
                    "French" -> {
                        text = allArtPieces[input].French_Desc
                    }
                    "Chinese" -> {
                        text = allArtPieces[input].Chinese_Desc
                    }
                    "Spanish" -> {
                        text = allArtPieces[input].Spanish_Desc
                    }
                    "German" -> {
                        text = allArtPieces[input].German_Desc
                    }
                    else -> {
                        text = allArtPieces[input].English_Desc
                    }
                }
                tts2!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            } else {
                when (language) {
                    "French" -> {
                        text = "RoboTour calcule l'itinéraire optimal"
                    }
                    "Chinese" -> {
                        text = "萝卜途正在计算最佳路线"
                    }
                    "Spanish" -> {
                        text = "RoboTour está calculando la ruta óptima"
                    }
                    "German" -> {
                        text = "RoboTour berechnet die optimale Route"
                    }
                    else -> {
                        text = "Ro-bow-Tour is calculating the optimal route"
                        //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
                    }
                }
                tts2!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
              * return true, otherwise false*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun exitDoor() {
        //This function will tell the robot to take the user to the exit
        if (isNetworkConnected()) {
            sendPUTNEW(11,"F")
            sendPUTNEW(15, "T")
        } else {
            toast("Check your network connection, command not sent")
        }
    }

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
        alert(exitDesc) {
            positiveButton(positive) {
                async {
                    val a = URL("http://homepages.inf.ed.ac.uk/s1553593/user1.php").readText()
                    if (a == "1") {
                        sendPUTNEW(12, "T")
                    }
                }
                if (userid == "1") {
                    async {
                        sendPUTNEW(16, "F")
                    }
                } else if (userid == "2") {
                    async {
                        sendPUTNEW(17, "F")
                    }
                }
                async {
                    val a = URL("http://homepages.inf.ed.ac.uk/s1553593/receiver.php").readText()
                    if (a[16] == 'T' && a[17] == 'T') {
                        sendPUTNEW(12, "T")
                    }
                }
                checkerThread.interrupt()
                clearFindViewByIdCache()
                switchToFinnished()
            }
            negativeButton(negative) { /*Do nothing*/ }
        }.show()
    }


    private fun cancelGuideTotal() {
        if (isNetworkConnected()) {
            switchToFinnished()
            if (userid == "1") {
                async {
                    sendPUTNEW(11,"F")
                    val a = URL("http://homepages.inf.ed.ac.uk/s1553593/receiver.php").readText()
                    if (a[12] == '2' || a[17] == 'F') {
                        sendPUTNEW(12, "T")
                        sendPUTNEW(16, "F")
                    } else {
                        sendPUTNEW(12, "1")
                        sendPUTNEW(16, "F")
                    }
                }
            } else if (userid == "2") {
                async {
                    val a = URL("http://homepages.inf.ed.ac.uk/s1553593/receiver.php").readText()
                    if (a[12] == '1' || a[16] == 'F') {
                        sendPUTNEW(12, "T")
                        sendPUTNEW(17, "F")
                    } else {
                        sendPUTNEW(12, "2")
                        sendPUTNEW(17, "F")
                    }
                }
            }
            checkerThread.interrupt()
        } else {
            toast("Check your network connection, command not sent")
        }
    }

    private fun rejectSkip() {
        if (isNetworkConnected()) {
            async {
                //This function will reject the skip by adding the empty string
                sendPUTNEW(10, "F")
            }
        } else {
            toast("Check your network connection, command not sent")
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

    @SuppressLint("SetTextI18n")
    private fun skipImmediately() {
        if (isNetworkConnected()) {
            /*This function is only when both users have agreed to skip the next item*/
            async {
                sendPUTNEW(10, "T")
                Thread.sleep(400)
                skippable = true
            }
            val language = intent.getStringExtra("language")
            when (language) {
                "English" -> {
                    titleView?.text = "RoboTour calculating optimal route..."
                    descriptionView?.text = "RoboTour calculating optimal route..."
                    imageView?.setImageResource(R.drawable.robotourfornavigating)
                }
                "German" -> {
                    titleView?.text = "RoboTour berechnet optimale Route ..."
                    descriptionView?.text = "RoboTour berechnet optimale Route ..."
                    imageView?.setImageResource(R.drawable.robotourfornavigating)
                }
                "Spanish" -> {
                    titleView?.text = "RoboTour calcula la ruta óptima ..."
                    descriptionView?.text = "RoboTour calcula la ruta óptima ..."
                    imageView?.setImageResource(R.drawable.robotourfornavigating)

                }
                "French" -> {
                    titleView?.text = "RoboTour calculant l'itinéraire optimal ..."
                    descriptionView?.text = "RoboTour calcula la ruta óptima ..."
                    imageView?.setImageResource(R.drawable.robotourfornavigating)
                }
                "Chinese" -> {
                    titleView?.text = "萝卜途正在计算最佳路线..."
                    descriptionView?.text = "萝卜途正在计算最佳路线..."
                    imageView?.setImageResource(R.drawable.robotourfornavigating)

                }
                "other" -> {
                    titleView?.text = "RoboTour calculating optimal route..."
                    descriptionView?.text = "RoboTour calcula la ruta óptima ..."
                    imageView?.setImageResource(R.drawable.robotourfornavigating)

                }
                "else" -> {
                    titleView?.text = "RoboTour calculating optimal route..."
                    descriptionView?.text = "RoboTour calculating optimal route..."
                    imageView?.setImageResource(R.drawable.robotourfornavigating)

                }
            }
        } else {
            toast("Check your network connection, command not sent")
        }
    }
    private fun skip() {
        println("+++++user mode" + userTwoMode)
        if (userTwoMode) {
            if (isNetworkConnected()) {
                async {
                    sendPUTNEW(10, userid)
                }
            } else {
                toast("Check your network connection, command not sent")
            }
        } else {
            skipImmediately()
        }
    }

    private fun stopRoboTour() {
        if (isNetworkConnected()) {
            async {
                sendPUTNEW(11, "T")
            }
        } else {
            toast("Check your network connection, command not sent")
        }
    }

    private fun startRoboTour() {
        if (isNetworkConnected()) {
            async {
                sendPUTNEW(11, "F")
            }
        } else {
            toast("Check your network connection, command not sent")
        }
    }

    private fun sendPUTNEW(identifier: Int, command: String) {
        val url = "http://homepages.inf.ed.ac.uk/s1553593/receiver.php"
        /*DISCLAIMER: When calling this function, if you don't run in an async, you will get
        * as security exception - just a heads up */
        val httpclient = DefaultHttpClient()
        val httPpost = HttpPost(url)
        try {
            val nameValuePairs = ArrayList<NameValuePair>(4)
            nameValuePairs.add(BasicNameValuePair("command$identifier", command))
            httPpost.entity = UrlEncodedFormEntity(nameValuePairs)
            httpclient.execute(httPpost)
        } catch (e: ClientProtocolException) {
        } catch (e: IOException) {
        }
    }
}