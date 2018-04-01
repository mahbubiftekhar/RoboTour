package com.example.david.robotour

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.*
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException
import java.net.URL
import java.nio.channels.InterruptedByTimeoutException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class NavigatingActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val btnHgt = 55
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
    private var obstacleRemovePlease = ""
    private var changeSpeed = ""
    private var imageView: ImageView? = null
    private var titleView: TextView? = null
    private var descriptionView: TextView? = null
    private var stopButton: Button? = null
    private var tableLayout2: LinearLayout? = null
    private lateinit var toiletPopUp: AlertDialogBuilder
    private lateinit var exitPopUp: AlertDialogBuilder
    private lateinit var obstacleAlert: AlertDialogBuilder
    private var skippable = true
    private var tts: TextToSpeech? = null
    private var tts2: TextToSpeech? = null
    private var tts3: TextToSpeech? = null
    private var tts5: TextToSpeech? = null
    private var tts6: TextToSpeech? = null
    private var tts7: TextToSpeech? = null
    private var currentPic = -1
    private var startRoboTour = ""
    private var toiletPopUpBool = true
    private var exitPop = true
    private var speaking = -1
    private var killThread = false
    private var userTwoMode = false
    private val listPaintings = ArrayList<ImageButton>()
    private var alertTitle = ""
    private var alertETA = ""
    private var alertDescription = ""
    private var cancelRequest = false
    private val map = mutableMapOf<Int, Int>()
    private var otherUseCancel = "Other user wishes to cancel, allow cancel?"
    private var cancelRequestSent = ""
    private var cancelPainting = ""
    private var artPieceTitle = ""
    private var tts4: TextToSpeech? = null
    private var artPieceDescription = ""
    private lateinit var closeApp: String
    private lateinit var restartApp: String
    private var speechText = ""
    private var toiletSpeech = true
    private var exitSpeech = true
    private var obstaclePopUp = true
    private var twoUserMode = false
    private var finnishing = true
    private var otherusercancel = ""

    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        return sharedPreferences.getInt(key, 0)
    }

    public override fun onDestroy() {
        checkerThread.interrupt()
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if (tts7 != null) {
            tts7!!.stop()
            tts7!!.shutdown()
        }
        if (tts2 != null) {
            tts2!!.stop()
            tts2!!.shutdown()
        }
        if (tts3 != null) {
            tts3!!.stop()
            tts3!!.shutdown()
        }
        if (tts4 != null) {
            tts4!!.stop()
            tts4!!.shutdown()
        }
        if (tts6 != null) {
            tts6!!.stop()
            tts6!!.shutdown()
        }
        if (tts5 != null) {
            tts5!!.stop()
            tts5!!.shutdown()
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
        if (tts7 != null) {
            tts7!!.stop()
            tts7!!.shutdown()
        }
        if (tts2 != null) {
            tts2!!.stop()
            tts2!!.shutdown()
        }
        if (tts3 != null) {
            tts3!!.stop()
            tts3!!.shutdown()
        }
        if (tts4 != null) {
            tts4!!.stop()
            tts4!!.shutdown()
        }
        if (tts6 != null) {
            tts6!!.stop()
            tts6!!.shutdown()
        }
        if (tts5 != null) {
            tts5!!.stop()
            tts5!!.shutdown()
        }
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.DONUT)
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tt
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
            // set US English as language for tt
            val language = intent.getStringExtra("language")
            val result: Int
            when (language) {
                "French" -> {
                    result = tts7!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts7!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts7!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts7!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts7!!.setLanguage(Locale.UK)
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
                    result = tts6!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts6!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts6!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts6!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts6!!.setLanguage(Locale.UK)
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
                    result = tts5!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts5!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts5!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts5!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts5!!.setLanguage(Locale.UK)
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
                    result = tts4!!.setLanguage(Locale.FRENCH)
                }
                "Chinese" -> {
                    result = tts4!!.setLanguage(Locale.CHINESE)
                }
                "Spanish" -> {
                    val spanish = Locale("es", "ES")
                    result = tts4!!.setLanguage(spanish)
                }
                "German" -> {
                    result = tts4!!.setLanguage(Locale.GERMAN)
                }
                else -> {
                    result = tts4!!.setLanguage(Locale.UK)
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
        if (tts4 != null) {
            tts4!!.stop()
            tts4!!.shutdown()
        }
        if (tts5 != null) {
            tts5!!.stop()
            tts5!!.shutdown()
        }
        if (tts6 != null) {
            tts6!!.stop()
            tts6!!.shutdown()
        }
        super.onPause()
    }

    override fun onResume() {
        //This ensures that when the nav activity is minimized and reloaded up, the speech still works
        tts = TextToSpeech(this, this)
        tts2 = TextToSpeech(this, this)
        tts3 = TextToSpeech(this, this)
        tts4 = TextToSpeech(this, this)
        tts5 = TextToSpeech(this, this)
        tts6 = TextToSpeech(this, this)
        tts7 = TextToSpeech(this, this)
        onInit(0)
        super.onResume()
        if (checkerThread.state == Thread.State.NEW) {
            checkerThread.start()
        }
    }

    private fun switchToFinnished() {
        checkerThread.interrupt()
        runOnUiThread {
            if (userid == "1") {
                async {
                    sendPUTNEW(16, "F")
                }
            } else if (userid == "2") {
                async {
                    sendPUTNEW(17, "F")
                }
            }
        }
        checkerThread.interrupt()
        val message: String
        val message2: String
        val language = intent.getStringExtra("language")
        message = when (language) {
            "French" -> "Merci d'utiliser RoboTour."
            "German" -> "Vielen Dank für die Verwendung von RoboTour."
            "Spanish" -> "Gracias por usar RoboTour."
            "Chinese" -> "感谢您使用萝卜途"
            else -> "Thank you for using RoboTour."
        }
        message2 = when (language) {
            "French" -> "Nous espérons que vous avez apprécié votre visite."
            "German" -> "Wir hoffen, Sie haben Ihre Tour genossen."
            "Spanish" -> "Esperamos que hayas disfrutado tu recorrido."
            "Chinese" -> "希望您喜欢这次旅程"
            else -> "We hope you enjoyed your tour."
        }
        when (language) {
            "French" -> {
                speechText = "Thank you for using Ro-bow-tour"
                restartApp = "START AGAIN"
                closeApp = "FERMER APP"
            }
            "German" -> {
                restartApp = "ANFANG"
                closeApp = "SCHLIEßE APP"
            }
            "Spanish" -> {
                restartApp = "COMIENZO"
                closeApp = "CERRAR APP"
            }
            "Chinese" -> {
                restartApp = "重新开始"
                closeApp = "关闭"
            }
            else -> {
                restartApp = "START AGAIN"
                closeApp = "CLOSE APP"
            }
        }
        speakOutThanks()
        alert {
            customView {
                linearLayout {
                    leftPadding = dip(4)
                    orientation = LinearLayout.VERTICAL
                    textView {
                        text = message
                        textSize = 22f
                        typeface = Typeface.DEFAULT_BOLD
                        verticalPadding = dip(10)
                    }
                    textView {
                        text = message2
                        textSize = 18f
                    }
                }
            }
            cancellable(false)
            setFinishOnTouchOutside(false)
            positiveButton {
                clearFindViewByIdCache()
                deleteCache(applicationContext)
                val i = baseContext.packageManager
                        .getLaunchIntentForPackage(baseContext.packageName)
                i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }.show()
    }

    private fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
        }
    }

    private fun speakOutThanks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            when (language) {
                "French" -> {
                    text = "Merci d'utiliser RoboTour"
                }
                "Chinese" -> {
                    text = "感谢您使用RoboTour"
                }
                "Spanish" -> {
                    text = "Gracias por usar RoboTour"
                }
                "German" -> {
                    text = "Vielen Dank für die Verwendung von RoboTour"
                }
                else -> {
                    text = "Thanks for using Robot Tour"
                    //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
                }
            }
            tts4!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun speakOutToilet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            when (language) {
                "French" -> {
                    text = "Nous sommes arrivés aux toilettes"
                }
                "Chinese" -> {
                    text = "我们已经到了厕所"
                }
                "Spanish" -> {
                    text = "Hemos llegado al baño"
                }
                "German" -> {
                    text = "Wir sind auf der Toilette angekommen"
                }
                else -> {
                    text = "We have arrived at the toilet"
                    //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
                }
            }
            tts6!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun speakOutExit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            when (language) {
                "French" -> {
                    text = "Nous sommes arrivés à la sortie"
                }
                "Chinese" -> {
                    text = "我们已经到达出口"
                }
                "Spanish" -> {
                    text = "Hemos llegado a la salida"
                }
                "German" -> {
                    text = "Wir sind am Ausgang angekommen"
                }
                else -> {
                    text = "We have arrived at the exit"
                    //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
                }
            }
            tts5!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun deleteDir(dir: File): Boolean {
        return when {
            dir.isDirectory -> {
                val children = dir.list()
                children.indices
                        .map { deleteDir(File(dir, children[it])) }
                        .filterNot { it }
                        .forEach { return false }
                dir.delete()
            }
            dir.isFile -> dir.delete()
            else -> false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        userid = loadInt("user").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigating)
        if (url == "https://proparoxytone-icing.000webhostapp.com/receiverPhone.php") {
            toast("Warning, in receiverPhone mode")
        }
        tts = TextToSpeech(this, this)
        tts2 = TextToSpeech(this, this)
        tts4 = TextToSpeech(this, this)
        async {
            val twoUserOrNot = URL(url).readText()[18]
            uiThread {
                twoUserMode = twoUserOrNot == 'T'
            }
        }
        supportActionBar?.hide() //hide actionbar
        //vibrate()
        async {
            Thread.sleep(1500)
            speakOutOnCreate()
        }
        val language = intent.getStringExtra("language")
        when (language) {
            "French" -> {
                speechText = "Thank you for using Ro-bow-tour"
                restartApp = "START AGAIN"
                closeApp = "FERMER APP?"
                otherusercancel = "D'autres utilisateurs souhaitent à cette peinture:\n"
            }
            "German" -> {
                restartApp = "ANFANG"
                closeApp = "SCHLIEßE APP?"
                otherusercancel = "Andere Benutzerwünsche zu diesem Bild:\n"
            }
            "Spanish" -> {
                restartApp = "COMIENZO"
                closeApp = "CERRAR APP?"
                otherusercancel = "Otro usuario desea esta pintura:\n"
            }
            "Chinese" -> {
                restartApp = "重新开始"
                closeApp = "关闭?"
                otherusercancel = "其他用户希望这幅画：\n"
            }
            else -> {
                restartApp = "START AGAIN"
                closeApp = "Close the app?"
                otherusercancel = "Other user wishes to this painting: \n "
            }
        }
        when (language) {
            "French" -> {
                obstacleRemovePlease = "S'il vous plaît retirer l'obstacle devant RoboTour\n"
            }
            "Chinese" -> {
                obstacleRemovePlease = "请移除RoboTour前面的障碍物\n"
            }
            "Spanish" -> {
                obstacleRemovePlease = "Quita el obstáculo delante de Ro-bow-Tour\n"
            }
            "German" -> {
                obstacleRemovePlease = "Bitte entfernen Sie das Hindernis vor der RoboTour\n"
            }
            else -> {
                obstacleRemovePlease = "Please remove the obstacle in front of RoboTour"
                //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
            }
        }
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
                cancelTour = "Cancel"
                cancelDesc = "Are you sure you want to cancel the tour?"
                exit = "Exit"
                exitDesc = "Do you want to go to the exit"
                toilet = "Toilet"
                toiletDesc = "Do you want to go to the toilet?"
                changeSpeed = "SPEED"
                startRoboTour = "Press START when you are ready for RoboTour to resume"
                otherUseCancel = "Other user wishes to cancel, allow cancel?: "
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
                closeApp = "Do you want to close the app"
                exitDesc = "Voulez-vous aller à la sortie?"
                toilet = "W.C."
                toiletDesc = "Voulez-vous aller aux toilettes?"
                changeSpeed = "Changer Vitesse"
                otherUseCancel = "D'autres utilisateurs souhaitent annuler, autoriser l'annulation?: "
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
                otherUseCancel = "其他用户希望取消，允许取消？: "
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
                otherUseCancel = "Otro usuario desea cancelar, ¿permite cancelar?: "
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
                otherUseCancel = "Andere Benutzer möchten stornieren, Abbrechen zulassen? :"
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
                exitDesc = "Do you want to cancel the tour?"
                toilet = "W.C."
                toiletDesc = "Do you want to go to the toilet?"
                changeSpeed = "SPEED"
                otherUseCancel = "Other user wishes to cancel, allow cancel?: "
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
                    allArtPieces.mapTo(
                            //change to sortedChosenArtPieces
                            listPaintings) {
                        imageButton {
                            backgroundColor = Color.TRANSPARENT
                            image = resources.getDrawable(it.imageID)
                            horizontalPadding = dip(5)
                            visibility = View.GONE
                        }
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
                            //Text-to-speech
                            speakOutButton(currentPic)
                        }
                    }
                    floatingActionButton {
                        //Alert
                        imageResource = R.drawable.ic_chat_black_24dp
                        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.roboTourTeal))
                        lparams { alignParentLeft(); topMargin = dip(100); leftMargin = dip(20) }
                        onClick {
                            //UI
                            //ColorStateList usually requires a list of states but this works for a single color
                            if (!(currentPic in 0..9)) {
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
                                        artPieceDescription = allArtPieces[currentPic].LongFrench
                                    }
                                    "Chinese" -> {
                                        artPieceTitle = allArtPieces[currentPic].nameChinese
                                        artPieceDescription = allArtPieces[currentPic].LongChinese
                                    }
                                    "Spanish" -> {
                                        artPieceTitle = allArtPieces[currentPic].nameSpanish
                                        artPieceDescription = allArtPieces[currentPic].LongSpanish
                                    }
                                    "German" -> {
                                        artPieceTitle = allArtPieces[currentPic].nameGerman
                                        artPieceDescription = allArtPieces[currentPic].LongGerman
                                    }
                                    else -> {
                                        artPieceTitle = allArtPieces[currentPic].name
                                        artPieceDescription = allArtPieces[currentPic].LongEnglish
                                    }
                                }
                            }
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
                                            text = "ETA: <30s"
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
                        /*lparams { width = matchParent }
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
                    }*/

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
            relativeLayout {
                tableLayout {
                    isStretchAllColumns = true
                    tableRow {
                        button(skip) {
                            background = ResourcesCompat.getDrawable(resources, R.drawable.buttonxml, null)
                            textSize = btnTextSize
                            verticalPadding = dip(0)
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
                            verticalPadding = dip(0)
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
                            verticalPadding = dip(0)
                            height = dip(btnHgt)
                            width = matchParent
                            onClick {
                                if (isNetworkConnected()) {
                                    alert(cancelDesc) {
                                        positiveButton(positive) {
                                            async {
                                                val aB = URL(url).readText()
                                                if (aB[18] == 'F') {
                                                    //If single user tell roboTour to cancel
                                                    sendPUTNEW(12, "T")
                                                    sendPUTNEW(userid.toInt(), "F")
                                                    sendPUTNEW(11, "F")
                                                }
                                            }
                                            if (userid == "1") {
                                                async {
                                                    sendPUTNEW(18,"F") //Set two user mode to false
                                                    sendPUTNEW(16, "F")
                                                }
                                            } else if (userid == "2") {
                                                async {
                                                    sendPUTNEW(18,"F") //Set two user mode to false
                                                    sendPUTNEW(17, "F")
                                                }
                                            }
                                            async {
                                                Thread.sleep(230)
                                                val a = URL(url).readText()
                                                if (a[16] == 'F' && a[17] == 'F') {
                                                    sendPUTNEW(12, "T")
                                                }
                                            }
                                            checkerThread.interrupt()
                                            clearFindViewByIdCache()
                                            runOnUiThread {
                                                switchToFinnished()
                                            }
                                            checkerThread.interrupt()
                                        }
                                        negativeButton(negative) {
                                            //onBackPressed()
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
                            verticalPadding = dip(0)
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
                            verticalPadding = dip(0)
                            height = dip(btnHgt)
                            width = matchParent
                            onClick {
                                alert(toiletDesc) {
                                    positiveButton(positive) {
                                        if (isNetworkConnected()) {
                                            async {
                                                sendPUTNEW(11, "F")
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
                            verticalPadding = dip(0)
                            height = dip(btnHgt)
                            width = matchParent
                            onClick {
                                if (isNetworkConnected()) {
                                    alert(exitDesc) {
                                        positiveButton(positive) {
                                            async {
                                                exitDoor()
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
                }.lparams {
                    alignParentBottom()
                }
            }
        }
    }

    /////
    private val checkerThread: Thread = object : Thread() {
        /*This thread will update the pictures,
        this feature can be sold as an advertisement opportunity as well*/
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            val language = intent.getStringExtra("language")
            while (!isInterrupted) {
                try {
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            async {
                                val a = URL(url).readText()
                                /*This updates the picture and text for the user*/
                                twoUserMode = a[18]=='T'
                                val paintings = a.substring(0, 10)
                                runOnUiThread { updateScrollView(paintings) }
                                val counter = (0..16).count { a[it] == 'F' }
                                if (counter >= 17 && finnishing) {
                                    finnishing = false
                                    runOnUiThread {
                                        switchToFinnished()
                                        killThread = true
                                    }
                                }
                                if (a[14] == 'N' || a[14] == 'T') {
                                    runOnUiThread {
                                        //User going to the toilet
                                        imageView?.setImageResource(R.drawable.toilet)
                                        titleView?.text = toilet
                                        descriptionView?.text = toilet
                                    }
                                }
                                if (a[15] == 'N' || a[15] == 'T') {
                                    runOnUiThread {
                                        //User going to the toilet
                                        imageView?.setImageResource(R.drawable.exit)
                                        titleView?.text = exit
                                        descriptionView?.text = exit
                                    }
                                }
                                if (a[15] == 'N' || a[15] == 'T') {
                                    runOnUiThread {
                                        //User going to the toilet
                                        imageView?.setImageResource(R.drawable.exit)
                                        titleView?.text = exit
                                        descriptionView?.text = exit
                                    }
                                }
                                if (a[14] == 'A' && toiletSpeech) {
                                    println(">>>>2")
                                    toiletSpeech = false
                                    speakOutToilet()
                                }
                                if (a[15] == 'A' && exitSpeech) {
                                    exitSpeech = false
                                    speakOutExit()
                                }
                                if (a[14] == 'F') {
                                    toiletSpeech = true
                                }
                                if (a[15] == 'F') {
                                    exitSpeech = true
                                }
                                for (i in 0..9) {
                                    println(">>>> in the for loop")

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
                                if (userid == 1.toString() && !cancelRequest && a[23] == 'Q') {
                                    runOnUiThread {
                                        println(">>>>>the integer" + a[20].toInt())
                                        cancelRequest = true
                                        val title = when (language) {
                                            "French" -> {
                                                otherUseCancel + allArtPieces[a[20].toString().toInt()].nameFrench
                                            }
                                            "Chinese" -> {
                                                otherUseCancel + allArtPieces[a[20].toString().toInt()].nameChinese
                                            }
                                            "Spanish" -> {
                                                otherUseCancel + allArtPieces[a[20].toString().toInt()].nameSpanish
                                            }
                                            "German" -> {
                                                otherUseCancel + allArtPieces[a[20].toString().toInt()].nameGerman
                                            }
                                            else -> {
                                                otherUseCancel + allArtPieces[a[20].toString().toInt()].name
                                            }
                                        }
                                        alert(title) {
                                            cancellable(false)
                                            setFinishOnTouchOutside(false)
                                            positiveButton(positive) {
                                                async {
                                                    sendPUTNEW(a[20].toString().toInt(), "F")
                                                    sendPUTNEW(23, "F")
                                                    cancelRequest = false
                                                }
                                            }
                                            negativeButton(negative) {
                                                async {
                                                    sendPUTNEW(23, "F")
                                                    cancelRequest = false
                                                }
                                            }
                                        }.show()
                                    }
                                } else if (userid == 2.toString() && !cancelRequest && a[23] == 'W') {
                                    runOnUiThread {
                                        println(">>>>> in here ahas")
                                        cancelRequest = true
                                        println(">>>>>the integer" + a[20].toInt())
                                        val title: String
                                        when (language) {
                                            "French" -> {
                                                title = otherUseCancel + allArtPieces[a[20].toString().toInt()].nameFrench
                                            }
                                            "Chinese" -> {
                                                title = otherUseCancel + allArtPieces[a[20].toString().toInt()].nameChinese
                                            }
                                            "Spanish" -> {
                                                title = otherUseCancel + allArtPieces[a[20].toString().toInt()].nameSpanish
                                            }
                                            "German" -> {
                                                title = otherUseCancel + allArtPieces[a[20].toString().toInt()].nameGerman
                                            }
                                            else -> {
                                                title = otherUseCancel + allArtPieces[a[20].toString().toInt()].name
                                            }
                                        }
                                        alert(title) {
                                            cancellable(false)
                                            setFinishOnTouchOutside(false)
                                            positiveButton(positive) {
                                                async {
                                                    sendPUTNEW(a[20].toString().toInt(), "F")
                                                    sendPUTNEW(23, "F")
                                                    cancelRequest = false
                                                }
                                            }
                                            negativeButton(negative) {
                                                async {
                                                    sendPUTNEW(23, "F")
                                                    cancelRequest = false
                                                }
                                            }
                                        }.show()
                                    }
                                } else if (a[23] == 'F') {
                                    cancelRequest = false
                                }

                                if (a[20] == 'T' && obstaclePopUp) {
                                    obstaclePopUp = false
                                    speakOutObstacle()
                                    runOnUiThread {
                                        obstacleAlert = alert(obstacleRemovePlease) {
                                            cancellable(false)
                                            setFinishOnTouchOutside(false)
                                        }.show()
                                    }
                                } else if (a[20] == 'F' && !obstaclePopUp) {
                                    obstaclePopUp = true
                                    try {
                                        obstacleAlert.dismiss()
                                    } catch (e: Exception) {

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
                                                        sendPUTNEW(14, "F")
                                                    }
                                                } else {
                                                    Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }.show()
                                    }
                                } else if (a[14] == 'F' && !toiletPopUpBool) {
                                    //If the other user selects, the pop up for the other use will be removed
                                    runOnUiThread {
                                        toiletPopUpBool = true
                                        try {
                                            toiletPopUp.dismiss()
                                        } catch (e: Exception) {

                                        }
                                    }
                                } else {
                                    //Do nothing
                                }
                                if (a[15] == 'A' && exitPop) {
                                    exitPop = false
                                    runOnUiThread {
                                        exitPopUp = alert(startRoboTour) {
                                            cancellable(false)
                                            setFinishOnTouchOutside(false)
                                            positiveButton(positive) {
                                                if (isNetworkConnected()) {
                                                    async {
                                                        sendPUTNEW(11, "F")
                                                        sendPUTNEW(15, "F")
                                                    }
                                                } else {
                                                    Toast.makeText(applicationContext, "Check network connection then try again", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }.show()
                                    }
                                } else if (a[15] == 'F' && !exitPop) {
                                    //If the other user selects, the pop up for the other use will be removed
                                    runOnUiThread {
                                        exitPop = true
                                        try {
                                            exitPopUp.dismiss()
                                        } catch (e: Exception) {

                                        }
                                    }
                                } else {
                                    //Do nothing
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
                try {
                    Thread.sleep(1500)
                } catch (e: InterruptedException) {

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

    private fun speakOutObstacle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            when (language) {
                "French" -> {
                    text = "S'il vous plaît retirer l'obstacle devant Ro-bow-Tour\n"
                }
                "Chinese" -> {
                    text = "请移除Ro-bow-Tour前面的障碍物\n"
                }
                "Spanish" -> {
                    text = "Quita el obstáculo delante de Ro-bow-Tour\n"
                }
                "German" -> {
                    text = "Bitte entfernen Sie das Hindernis vor der Ro-Bow-Tour\n"
                }
                else -> {
                    text = "Please remove the obstacle in front of Ro-bow-Tour"
                    //The misspelling of RobotTour in English is deliberate to ensure we get the correct pronunciation
                }
            }
            tts7!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
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
            if (input in 0..9) {
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
            } else if (input == -1) {
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

    private fun isNetworkConnected(): Boolean {
        /*Function to check if a data connection is available, if a data connection is
              * return true, otherwise false*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun exitDoor() {
        //This function will tell the robot to take the user to the exit
        val a = loadInt("user")
        async {
            sendPUTNEW(11, "F")
            when (a) {
                1 -> sendPUTNEW(16, "F")
                2 -> sendPUTNEW(17, "F")
                else -> {
                    //Do nothing
                }
            }
        }
        if (isNetworkConnected()) {
            sendPUTNEW(11, "F")
            sendPUTNEW(15, "T")
        } else {
            toast("Check your network connection, command not sent")
        }
    }

    override fun onBackPressed() {
        /*Overriding on back pressed, otherwise user can go back to previous maps and we do not want that
        Send the user back to MainActivity */
        alert(closeApp) {
            positiveButton(positive) {
                checkerThread.interrupt()
                async {
                    val aB = URL(url).readText()
                    if (aB[18] == 'F') {
                        //If single user tell roboTour to cancel
                        sendPUTNEW(12, "T")
                        sendPUTNEW(userid.toInt(), "F")
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
                    Thread.sleep(300)
                    val a = URL(url).readText()
                    if (a[16] == 'F' && a[17] == 'F') {
                        sendPUTNEW(12, "T")
                    }
                }
                checkerThread.interrupt()
                clearFindViewByIdCache()
                runOnUiThread {
                    switchToFinnished()
                }
            }
            negativeButton(negative) { /*Do nothing*/ }
        }.show()
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

    private fun requestCancel(paintingID: Int) {
        /*
        * Q == USER1
        * W == USER2
        * */
        when {
            !twoUserMode -> async {
                sendPUTNEW(paintingID, "F")
            }
            userid == 1.toString() -> async {
                sendPUTNEW(20, paintingID.toString())
                sendPUTNEW(23, "W")
            }
            userid == 2.toString() -> async {
                sendPUTNEW(20, paintingID.toString())
                sendPUTNEW(23, "Q")
            }
        }
    }

    private fun updateScrollViewPictures(sortedNums: MutableCollection<Int>) {
        val numSelectedPaintings = sortedNums.size
        (numSelectedPaintings..10)
                .filter { it < 10 }
                .forEach { listPaintings[it].visibility = View.GONE }
        for (i in 0 until numSelectedPaintings) {
            listPaintings[i].visibility = View.VISIBLE
        }
        sortedNums.withIndex().forEach { (listIndex, i) ->
            when (i) {
                0 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.birthofvenus))
                1 -> listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.creationofadam))
                2 -> {
                    listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.david))
                }
                3 -> {
                    listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.girlwithpearlearring))
                }
                4 -> {
                    listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.monalisa))
                }
                5 -> {
                    listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.napoleoncrossingthealps))
                }
                6 -> {
                    listPaintings[listIndex].setImageDrawable(resources.getDrawable(R.drawable.starrynight))
                }
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

    private fun getETA(paintingIndex: Int): String {
        /*This function will get the ETA*/
        val position = map[paintingIndex]!!
        return ("ETA: " + (30 * (1 + position)) + " Seconds")
        // return (20 * (1+paintingIndex)).toString()
    }

    @SuppressLint("SetTextI18n")
    private fun paintingAlertUpdate(paintIndex: Int) {
        alertETA = getETA(paintIndex)
        val language = intent.getStringExtra("language")
        when (language) {
            "French" -> {
                alertTitle = allArtPieces[paintIndex].nameFrench
                alertDescription = allArtPieces[paintIndex].LongEnglish
            }
            "Chinese" -> {
                alertTitle = allArtPieces[paintIndex].nameChinese
                alertDescription = allArtPieces[paintIndex].LongChinese
            }
            "Spanish" -> {
                alertTitle = allArtPieces[paintIndex].nameSpanish
                alertDescription = allArtPieces[paintIndex].LongSpanish
            }
            "German" -> {
                alertTitle = allArtPieces[paintIndex].nameGerman
                alertDescription = allArtPieces[paintIndex].LongGerman
            }
            else -> {
                alertTitle = allArtPieces[paintIndex].name
                alertDescription = allArtPieces[paintIndex].LongEnglish
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
                    }
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
                            requestCancel(paintIndex)
                            //listPaintings[map[paintIndex]!!].visibility = View.GONE
                            if (twoUserMode) {
                                toast(cancelRequestSent)
                            }
                            dismiss()
                        }
                    }
                }
            }
        }.show()
    }
}