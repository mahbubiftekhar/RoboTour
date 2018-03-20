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
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class ListenInActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var btnTextSize = 24f
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
    private var tableLayout2: LinearLayout? = null
    private var tts: TextToSpeech? = null
    private var tts2: TextToSpeech? = null
    private var tts3: TextToSpeech? = null
    private var currentPic = -1
    private var startRoboTour = ""
    private var speaking = -1
    private var killThread = false
    private var userTwoMode = false
    private val listPaintings = ArrayList<ImageButton>()
    private var alertTitle = ""
    private var alertETA = ""
    private var alertDescription = ""
    private val map = mutableMapOf<Int, Int>()
    private var otherUseCancel = "Other user wishes to cancel, allow cancel?"
    private var cancelRequestSent = ""
    private var cancelPainting = ""
    private var artPieceTitle = ""
    private var artPieceDescription = ""

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
        if (tts3 != null) {
            tts3!!.stop()
            tts3!!.shutdown()
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
        if (tts3 != null) {
            tts3!!.stop()
            tts3!!.shutdown()
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
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val language = intent.getStringExtra("language")
            val result: Int
            when (language) {
                "French" -> {
                    result = tts3!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts3!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts3!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts3!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts3!!.setLanguage(Locale.UK)
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
        if (tts3 != null) {
            tts3!!.stop()
            tts3!!.shutdown()
        }
        super.onPause()
    }

    override fun onResume() {
        //This ensures that when the nav activity is minimized and reloaded up, the speech still works
        tts = TextToSpeech(this, this)
        tts2 = TextToSpeech(this, this)
        tts3 = TextToSpeech(this, this)
        onInit(0)
        super.onResume()
        if (checkerThread.state == Thread.State.NEW) {
            checkerThread.start()
        }
    }

    private fun switchToFinnished() {
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
        vibrate()
        async {
            Thread.sleep(1500)
            speakOutOnCreate()
        }
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
                changeSpeed = "SPEED"
                startRoboTour = "Press START when you are ready for RoboTour to resume"
                otherUseCancel = "Other user wishes to cancel, allow cancel?"
                cancelRequestSent = "Cancel request sent"
                cancelPainting = "Cancel painting"
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
                otherUseCancel = "D'autres utilisateurs souhaitent annuler, autoriser l'annulation?"
                cancelRequestSent = "Annuler la demande envoyée"
                cancelPainting = "Annuler la peinture"
            }
            "Chinese" -> {
                startRoboTour = "当您准备好跟随萝卜途时，请按开始。"
                positive = "是的"
                negative = "不是"
                skip = "跳过"
                skipDesc = "确定要跳过这一幅作品吗？"
                stop = "停止"
                stopDesc = "确定要停止萝卜途吗？"
                start = "开始"
                startDesc = "确定开始萝卜途吗？"
                cancelTour = "取消游览"
                cancelDesc = "确定要取消游览吗？"
                exit = "带我去出口"
                exitDesc = "确定要去出口吗？"
                toilet = "带我去厕所"
                toiletDesc = "确定要去厕所吗？"
                changeSpeed = "改变速度"
                otherUseCancel = "其他用户希望取消，允许取消？"
                cancelRequestSent = "取消请求已发送"
                cancelPainting = "取消绘画"
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
                otherUseCancel = "Otro usuario desea cancelar, ¿permite cancelar?"
                cancelRequestSent = "Cancelar solicitud enviada"
                cancelPainting = "Cancelar pintura"
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
                otherUseCancel = "Andere Benutzer möchten stornieren, Abbrechen zulassen?"
                cancelRequestSent = "Anfrage abbrechen gesendet"
                cancelPainting = "Gemälde abbrechenC"
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
                changeSpeed = "SPEED"
                otherUseCancel = "Other user wishes to cancel, allow cancel?"
                cancelRequestSent = "Cancel request sent"
                cancelPainting = "Cancel painting"
            }
        }
        relativeLayout {
            val nextPaintings = textView {
                id = View.generateViewId()
                // text = "Next Art Pieces:"
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                padding = dip(2)
            }.lparams { alignParentTop() }
            val hSV = horizontalScrollView {
                id = View.generateViewId()
                linearLayout {
                    val aasd = allArtPieces.size
                    println(">>>>> all art pieces: $aasd")
                    for (i in allArtPieces) { //change to sortedChosenArtPieces
                        listPaintings.add(
                                imageButton {
                                    backgroundColor = Color.TRANSPARENT
                                    image = resources.getDrawable(i.imageID)
                                    horizontalPadding = dip(5)
                                }
                        )
                    }
                }
            }.lparams { below(nextPaintings) }

            tableLayout2 = linearLayout {
                orientation = LinearLayout.VERTICAL
                relativeLayout {
                    floatingActionButton {
                        //UI
                        imageResource = R.drawable.ic_volume_up_black_24dp
                        //ColorStateList usually requires a list of states but this works for a single color
                        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.roboTourTeal))
                        lparams { alignParentRight(); topMargin = dip(100); rightMargin = dip(20) }

                        onClick {
                            speakOutButton(currentPic)
                        }
                    }
                    floatingActionButton {
                        //UI
                        imageResource = R.drawable.ic_chat_black_24dp
                        //ColorStateList usually requires a list of states but this works for a single color
                        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.roboTourTeal))
                        lparams { alignParentLeft(); topMargin = dip(100); leftMargin = dip(20) }
                        val language = intent.getStringExtra("language")
                        if (currentPic == -1) {
                            when (language) {
                                "French" -> {
                                    artPieceDescription = "RoboTour calcule l'itinéraire optimal"
                                    artPieceTitle = "RoboTour calcule l'itinéraire optimal"
                                }
                                "Chinese" -> {
                                    artPieceDescription = "萝卜途正在计算最佳路线"
                                    artPieceTitle = "萝卜途正在计算最佳路线"
                                }
                                "Spanish" -> {
                                    artPieceDescription = "RoboTour está calculando la ruta óptima"
                                    artPieceTitle = "RoboTour está calculando la ruta óptima"
                                }
                                "German" -> {
                                    artPieceDescription = "RoboTour berechnet die optimale Route"
                                    artPieceTitle = "RoboTour berechnet die optimale Route"
                                }
                            }
                        } else {
                            when (language) {
                                "French" -> {
                                    artPieceTitle = allArtPieces[currentPic].nameFrench
                                    artPieceDescription = allArtPieces[currentPic].French_Desc
                                }
                                "Chinese" -> {
                                    artPieceTitle = allArtPieces[currentPic].nameChinese
                                    artPieceDescription = allArtPieces[currentPic].Chinese_Desc
                                }
                                "Spanish" -> {
                                    artPieceTitle = allArtPieces[currentPic].nameSpanish
                                    artPieceDescription = allArtPieces[currentPic].Spanish_Desc
                                }
                                "German" -> {
                                    artPieceTitle = allArtPieces[currentPic].nameGerman
                                    artPieceDescription = allArtPieces[currentPic].German_Desc
                                }
                                else -> {
                                    artPieceTitle = allArtPieces[currentPic].name
                                    artPieceDescription = allArtPieces[currentPic].English_Desc
                                }
                            }

                        }
                        //Alert
                        onClick {
                            alert {
                                customView {
                                    linearLayout {
                                        orientation = LinearLayout.VERTICAL
                                        textView {
                                            text = artPieceTitle
                                            textSize = 32f
                                            typeface = Typeface.DEFAULT_BOLD
                                            padding = dip(3)
                                            gravity = Gravity.CENTER_HORIZONTAL
                                        }
                                        textView {
                                            text = "ETA: <10s"
                                            textSize = 20f
                                            padding = dip(2)
                                            leftPadding = dip(4)
                                        }
                                        textView {
                                            text = artPieceDescription
                                            textSize = 16f
                                            padding = dip(2)
                                            leftPadding = dip(4)
                                        }
                                    }
                                }
                            }.show()
                        }
                    }
                    tableLayout {
                        orientation = LinearLayout.VERTICAL
                        lparams { width = matchParent }
                        titleView = textView {
                            textSize = 32f
                            typeface = Typeface.DEFAULT_BOLD
                            padding = dip(5)
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        verticalLayout {
                            orientation = LinearLayout.HORIZONTAL
                            imageView = imageView {
                                backgroundColor = Color.TRANSPARENT //Removes gray border
                                gravity = Gravity.CENTER_HORIZONTAL
                            }.lparams {
                                bottomMargin = dip(10)
                                topMargin = dip(10)
                            }
                        }
                    }
                    verticalLayout {
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
            }.lparams {
                below(hSV)
            }
        }
        //Starting the thread which is defined above to keep polling the server for changes
        val sortedNums: MutableCollection<Int> = arrayListOf(1, 5, 7)
        updateScrollViewPictures(sortedNums)
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
                                val a = URL(url).readText()
                                println("++++++++" + a)
                                /*This updates the picture and text for the user*/
                                val paintings = a.substring(0, 9)
                                runOnUiThread { updateScrollView(paintings) }
                                val counter = (0..16).count { a[it] == 'F' }
                                if (counter == 17) {
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

                            }
                            Thread.sleep(600)
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

    private fun sortString(input: String): MutableMap<Int, Int> {
        val context: MutableMap<Int, Int> = mutableMapOf()
        val input2 = input.toCharArray()
        for (a in 0 until input2.size) {
            if (input2[a] == 'T' || input2[a] == 'F' || input2[a] == 'A' || input2[a] == 'N') {
            } else {
                try {
                    context.put(input2[a].toString().toInt(), a)
                } catch (e: java.lang.NumberFormatException) {

                }
            }
        }
        return context.toSortedMap()
    }

    private fun updateScrollView(paintings: String) {
        val output = sortString(paintings)
        val sortedChosenArtPieces = output.values
        updateScrollViewPictures(sortedChosenArtPieces)
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

    private fun speakOutOnCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
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
            tts3!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
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
                        text = "Calcule l'itinéraire optimal"
                    }
                    "Chinese" -> {
                        text = "萝卜途正在计算最佳路线"
                    }
                    "Spanish" -> {
                        text = "Está calculando la ruta óptima"
                    }
                    "German" -> {
                        text = "Berechnet die optimale Route"
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

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
        alert(exitDesc) {
            positiveButton(positive) {
                checkerThread.interrupt()
                clearFindViewByIdCache()
                switchToFinnished()
            }
            negativeButton(negative) { /*Do nothing*/ }
        }.show()
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


    private fun updateScrollViewPictures(sortedNums: MutableCollection<Int>) {
        val numSelectedPaintings = sortedNums.size
        print(">>>>>>>" + numSelectedPaintings)
        for (i in numSelectedPaintings..10) {
            if (i < 10) {
                print("£££££" + i)
                listPaintings[i].visibility = View.GONE
            }
        }
        for (i in 0..numSelectedPaintings-1) {
            print("£££££" + i)
            listPaintings[i].visibility = View.VISIBLE
        }
        sortedNums.withIndex().forEach { (listIndex, i) ->
            when (i) {
                0 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.birthofvenus))
                1 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.creationofadam))
                2 -> {
                    println("+++++ getting in here updateScrollView")
                    listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.david))
                }
                3 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.girlwithpearlearring))
                4 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.monalisa))
                5 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.napoleoncrossingthealps))
                6 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.starrynight))
                7 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.thelastsupper))
                8 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.tsunami))
                9 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.waterlillies))
            }
            map.put(i, listIndex)
            listPaintings[listIndex].setOnClickListener {
                paintingAlertUpdate(i)
            }
        }
    }

    private fun getETA(): String {
        /*This function will get the ETA*/
        return "ETA 10SECONDS"
    }

    @SuppressLint("SetTextI18n")
    private fun paintingAlertUpdate(paintIndex: Int) {
        alertETA = "ETA: <10s"
        val language = intent.getStringExtra("language")
        when (language) {
            "French" -> {
                alertTitle = allArtPieces[paintIndex].nameFrench
                alertDescription = allArtPieces[paintIndex].English_Desc
            }
            "Chinese" -> {
                alertTitle = allArtPieces[paintIndex].nameChinese
                alertDescription = allArtPieces[paintIndex].Chinese_Desc
            }
            "Spanish" -> {
                alertTitle = allArtPieces[paintIndex].nameSpanish
                alertDescription = allArtPieces[paintIndex].Spanish_Desc
            }
            "German" -> {
                alertTitle = allArtPieces[paintIndex].nameGerman
                alertDescription = allArtPieces[paintIndex].German_Desc
            }
            else -> {
                alertTitle = allArtPieces[paintIndex].name
                alertDescription = allArtPieces[paintIndex].English_Desc
            }
        }

        alert {
            customView {
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    textView {
                        text = alertTitle
                        textSize = 32f
                        typeface = Typeface.DEFAULT_BOLD
                        padding = dip(3)
                        gravity = Gravity.CENTER_HORIZONTAL

                        textView {
                            text = alertETA //The position the paining is on the list times 10s
                            textSize = 20f
                            padding = dip(2)
                        }
                        textView {
                            text = alertDescription
                            textSize = 16f
                            padding = dip(2)
                        }
                        button {
                            text = cancelPainting
                            onClick {
                                //Remove Painting From List
                                //listPaintings[map[paintIndex]!!].visibility = View.GONE
                                toast(cancelRequestSent)
                                dismiss()
                            }
                        }
                    }
                }
            }
        }.show()
    }
}