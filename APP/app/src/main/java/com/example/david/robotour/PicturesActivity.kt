package com.example.david.robotour

import android.content.ActivityNotFoundException
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.*
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.InputType.TYPE_CLASS_TEXT
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.inputmethod.InputMethodManager
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.android.synthetic.*
import java.io.InterruptedIOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import java.util.Random


val allArtPieces = ArrayList<PicturesActivity.ArtPiece>()

@Suppress("DEPRECATION")
class PicturesActivity : AppCompatActivity() {

    data class ArtPiece(val name: String, val artist: String, val nameChinese: String, val nameGerman: String, val nameSpanish: String, val nameFrench: String, val English_Desc: String, val German_Desc: String, val French_Desc: String, val Chinese_Desc: String, val Spanish_Desc: String, val imageID: Int, val eV3ID: Int, var selected: Boolean)

    private var frequencyList = ArrayList<Int>()
    private var frequencyListReady = false
    private var shownArtPieces = ArrayList<ArtPiece>()
    private val reqSpeechCode = 100
    private var queriedArtPieces = ArrayList<ArtPiece>()
    private var searchedForPainting = false //true if we've searched for a painting
    private var adapter = PicturesAdapter(shownArtPieces, "") //initialise adapter for global class use
    lateinit var t: Thread
    private var language = ""
    private val random = Random()
    private var tts_recommendations: TextToSpeech? = null
    private var tts_results: TextToSpeech? = null

    private fun translate(textToTranslate: List<String>): MutableList<String> {
        /*This function takes a list and returns a list of translated text using Google's API
        * This function MUST be called ASYNCHRONOUSLY, if it is not you will crash the activity with a
        * network on main thread exception */
        val translated: MutableList<String> = mutableListOf()
        val apiKey = "AIzaSyCYryDwlXkmbUfHZS5HLJIIoGoO8Yy5yGw" //My API key, MUST be removed after course finnished
        for (i in textToTranslate) {
            val options = TranslateOptions.newBuilder().setApiKey(apiKey).build()
            val translate = options.service
            val translation = translate.translate(i, Translate.TranslateOption.targetLanguage("en"))
            translated.add(translation.translatedText)
        }
        return translated
    }

    override fun onDestroy() {
        // Shutdown TTS
        if (tts_recommendations != null) {
            tts_recommendations!!.stop()
            tts_recommendations!!.shutdown()
        }
        if (tts_results != null) {
            tts_results!!.stop()
            tts_results!!.shutdown()
        }

        super.onDestroy()
    }

    override fun onStop() {
        if (tts_recommendations != null) {
            tts_recommendations!!.stop()
            tts_recommendations!!.shutdown()
        }
        if (tts_results != null) {
            tts_results!!.stop()
            tts_results!!.shutdown()
        }
        t.interrupt()
        t.interrupt()
        super.onStop()
    }

    fun onInit() {
        val language = intent.getStringExtra("language")
        var result: Int
        when (language) {
            "French" -> {
                result = tts_recommendations!!.setLanguage(Locale.FRENCH)
            }
            "Chinese" -> {
                result = tts_recommendations!!.setLanguage(Locale.CHINESE)
            }
            "Spanish" -> {
                val spanish = Locale("es", "ES")
                result = tts_recommendations!!.setLanguage(spanish)
            }
            "German" -> {
                result = tts_recommendations!!.setLanguage(Locale.GERMAN)
            }
            else -> {
                result = tts_recommendations!!.setLanguage(Locale.UK)
            }
        }
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        } else {
        }
        when (language) {
            "French" -> {
                result = tts_results!!.setLanguage(Locale.FRENCH)
            }
            "Chinese" -> {
                result = tts_results!!.setLanguage(Locale.CHINESE)
            }
            "Spanish" -> {
                val spanish = Locale("es", "ES")
                result = tts_results!!.setLanguage(spanish)
            }
            "German" -> {
                result = tts_results!!.setLanguage(Locale.GERMAN)
            }
            else -> {
                result = tts_results!!.setLanguage(Locale.UK)
            }
        }
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        } else {
        }
    }

    private fun speakOut_recommendations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            when (language) {
                "French" -> {
                    text = "Voici vos recommandations"
                }
                "Chinese" -> {
                    text = "这是你的建议"
                }
                "Spanish" -> {
                    text = "Aquí están tus recomendaciones"
                }
                "German" -> {
                    text = "Hier sind deine Empfehlungen"
                }
                else -> {
                    text = "Here are your recommendations"
                }
            }
            tts_recommendations!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun speakOut_results() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val text: String
            val language = intent.getStringExtra("language")
            when (language) {
                "French" -> {
                    text = "Voici les résultats"
                }
                "Chinese" -> {
                    text = "结果如下"
                }
                "Spanish" -> {
                    text = "Aquí están los resultados"
                }
                "German" -> {
                    text = "Here are the results"
                }
                else -> {
                    text = "Here are the results"
                }
            }
            tts_recommendations!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun translateText(textToTranslate: String): String? {
        /*This function takes a list and returns a list of translated text using Google's API
        * This function MUST be called ASYNCHRONOUSLY, if it is not you will crash the activity with a
        * network on main thread exception */
        val apiKey = "AIzaSyCYryDwlXkmbUfHZS5HLJIIoGoO8Yy5yGw" //My API key, MUST be removed after course finished
        val options = TranslateOptions.newBuilder().setApiKey(apiKey).build()
        val translate = options.service
        val translation = translate.translate(textToTranslate, Translate.TranslateOption.targetLanguage("en"))
        return translation.translatedText
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        allArtPieces.clear()
        async {
            storeFrequencies()
        }
        tts_recommendations = TextToSpeech(this, null)
        tts_results = TextToSpeech(this, null)
        onInit()
        //Obtain language from SelectLanguageActivity
        language = intent.getStringExtra("language")
        when (language) {
            "English" -> supportActionBar?.title = "Select Picture"
            "German" -> supportActionBar?.title = "Wähle ein Bild"
            "Spanish" -> supportActionBar?.title = "Seleccionar imagen"
            "French" -> supportActionBar?.title = "Sélectionnez une image"
            "Chinese" -> supportActionBar?.title = "选择图片"
            "other" -> supportActionBar?.title = "Select Picture"
            "else" -> supportActionBar?.title = "Select Picture"
        }

        //Populate List
        allArtPieces.run {
            add(ArtPiece(name = "The Birth of Venus",
                    artist = "Sandro Botticelli", nameChinese = "金星的诞生", nameGerman = "Die Geburt der Venus", nameSpanish = "El nacimiento de Venus", nameFrench = "La naissance de Vénus",
                    English_Desc = "Depicts the goddess Venus arriving at the shore after her birth",
                    German_Desc = "Stellt die Göttin Venus dar, die nach ihrer Geburt am Ufer ankommt",
                    French_Desc = "Représente la déesse Vénus arrivant au rivage après sa naissance",
                    Chinese_Desc = "《维纳斯的诞生》是意大利文艺复兴时期画家桑德罗·波提切利最著名的作品之一，根据波利齐安诺的长诗吉奥斯特纳而作，描绘罗马神话中女神维纳斯从海中诞生的情景。",
                    Spanish_Desc = "Representa a la diosa Venus llegando a la orilla después de su nacimiento",
                    imageID = R.drawable.birthofvenus, eV3ID = 0, selected = false))
            add(ArtPiece(name = "The Creation of Adam",
                    artist = "Michelangelo", nameChinese = "亚当的创造", nameGerman = "Die Schaffung von Adam", nameSpanish = "La creación de adam", nameFrench = "La création d'Adam",
                    English_Desc = "A fresco painting by Michelangelo, which forms part of the Sistine Chapel's ceiling",
                    German_Desc = "Ein Fresko von Michelangelo, das Teil der Sixtinischen Kapelle ist",
                    French_Desc = "Une fresque de Michel-Ange, qui fait partie du plafond de la chapelle Sixtine",
                    Chinese_Desc = "《创造亚当》是米开朗基罗创作的西斯廷礼拜堂天顶画《创世纪》的一部分，创作于1511至1512年间的文艺复兴全盛期。这幅壁画描绘的是《圣经·创世纪》中上帝创造人类始祖亚当的情形，按照事情发展顺序是创世纪天顶画中的第四幅。",
                    Spanish_Desc = "Una pintura al fresco de Miguel Ángel, que forma parte del techo de la Capilla Sixtina",
                    imageID = R.drawable.creationofadam, eV3ID = 1, selected = false))
            add(ArtPiece(name = "David", artist = "Michelangelo", nameChinese = "大卫", nameGerman = "David", nameSpanish = "David", nameFrench = "David",
                    English_Desc = "A masterpiece of Renaissance sculpture created in marble between 1501 and 1504 by Michelangelo",
                    German_Desc = "Ein Meisterwerk der Renaissanceskulpturen, das zwischen 1501 und 1504 von Michelangelo aus Marmor geschaffen wurde",
                    French_Desc = "Une fresque de Michel-Ange, qui fait partie du plafond de la chapelle Sixtine",
                    Chinese_Desc = "《大卫像》是文艺复兴时代米开朗基罗的杰作，于1501年至1504年雕成。雕像为白色大理石雕成的站立的男性裸体，高5.17米，重约6吨。用以表现圣经中的犹太英雄大卫王。",
                    Spanish_Desc = "Una obra maestra de la escultura renacentista creada en mármol entre 1501 y 1504 por Miguel Ángel",
                    imageID = R.drawable.david, eV3ID = 2, selected = false))
            add(ArtPiece(name = "Girl with a Pearl Earring", artist = "Johannes Vermeer", nameChinese = "珍珠耳环的女孩", nameGerman = "Das Mädchen mit dem Perlenohrring", nameSpanish = "Chica con un pendiente de perla", nameFrench = "une fille avec une boucle d'oreille",
                    English_Desc = "Showcasing the electrifying gaze of a young girl adorned with a blue and gold turban.",
                    German_Desc = "Den elektrisierenden Blick eines jungen Mädchens zeigen, das mit einem Blau- und Goldturban geschmückt wird.",
                    French_Desc = "Mettant en vedette le regard électrisant d'une jeune fille avec un turban bleu et or.",
                    Chinese_Desc = "《戴珍珠耳环的少女》是十七世纪荷兰画家杨·弗美尔的作品。画作以少女戴着的珍珠耳环作为视角的焦点。",
                    Spanish_Desc = "Exhibiendo la mirada electrizante de una niña adornada con un turbante azul y dorado.",
                    imageID = R.drawable.girlwithpearlearring, eV3ID = 3, selected = false))
            add(ArtPiece(name = "Mona Lisa", artist = "Leonardo da Vinci", nameChinese = "蒙娜丽莎", nameGerman = "Mona Lisa", nameSpanish = "Mona Lisa", nameFrench = "Mona Lisa",
                    English_Desc = "The title of the painting, which is known in English as Mona Lisa, comes from a description by Renaissance art historian Giorgio Vasari",
                    German_Desc = "Der Titel des Gemäldes, der auf Englisch als Mona Lisa bekannt ist, stammt aus einer Beschreibung des Renaissance-Kunsthistorikers Giorgio Vasari",
                    French_Desc = "Le titre de la peinture, qui est connu en anglais comme Mona Lisa, vient d'une description par l'historien d'art de la Renaissance Giorgio Vasari",
                    Chinese_Desc = "《蒙娜丽莎》是文艺复兴时期画家列奥纳多·达·芬奇所绘的肖像画。画中描绘了一位表情内敛的、微带笑容的女士，她的笑容有时被称作是\"神秘的笑容\"。",
                    Spanish_Desc = "El título de la pintura, que se conoce en inglés como Mona Lisa, proviene de una descripción del historiador del arte del Renacimiento Giorgio Vasari.",
                    imageID = R.drawable.monalisa, eV3ID = 4, selected = false))

            add(ArtPiece(name = "Napoleon Crossing the Alps",
                    artist = "Jacques-Louis David", nameChinese = "拿破仑穿越阿尔卑斯山", nameGerman = "Napoleon über die Alpen", nameSpanish = "Napoleón cruzando los Alpes", nameFrench = "Napoléon franchissant les Alpes",
                    English_Desc = "Oil on canvas equestrian portrait of Napoleon Bonaparte painted by the French artist Jacques-Louis David between 1801 and 1805",
                    German_Desc = "Öl auf Leinwand Reiterporträt von Napoleon Bonaparte von dem französischen Künstler Jacques-Louis David zwischen 1801 und 1805 gemalt",
                    French_Desc = "Huile sur toile portrait équestre de Napoléon Bonaparte peint par l'artiste français Jacques-Louis David entre 1801 et 1805",
                    Chinese_Desc = "《拿破仑翻越阿尔卑斯山》是雅克-路易·大卫绘制的五幅油画的统称，绘制了拿破仑·波拿巴在发动马伦哥战役前越过圣伯纳隘道时的情景。",
                    Spanish_Desc = "Óleo sobre lienzo retrato ecuestre de Napoleón Bonaparte pintado por el artista francés Jacques-Louis David entre 1801 y 1805",
                    imageID = R.drawable.napoleoncrossingthealps, eV3ID = 5, selected = false))
            add(ArtPiece(name = "The Starry Night", artist = "Vincent van Gogh", nameChinese = "星夜", nameGerman = "Die Sternreiche Nacht", nameSpanish = "La noche estrellada", nameFrench = "La nuit étoilée",
                    English_Desc = "The night sky depicted by van Gogh in the Starry Night painting is brimming with whirling clouds, shining stars, and a bright crescent moon.",
                    German_Desc = "Der Nachthimmel, den van Gogh in der Sternennacht zeigt, ist voll von wirbelnden Wolken, leuchtenden Sternen und einer hellen Mondsichel." + "",
                    French_Desc = "Le ciel nocturne représenté par Van Gogh dans la peinture de la nuit étoilée déborde de nuages tourbillonnants, ",
                    Chinese_Desc = "《星夜》是荷兰后印象派画家文森特·梵高于1890年在法国圣雷米的一家精神病院里创作的一幅著名油画。",
                    Spanish_Desc = "El cielo nocturno representado por Van Gogh en la pintura de la Noche Estrellada rebosa de nubes giratorias, estrellas brillantes y una brillante luna creciente." +
                            "", imageID = R.drawable.starrynight, eV3ID = 6, selected = false))
            add(ArtPiece(name = "The Last Supper", artist = "Leonardo da Vinci", nameChinese = "最后的晚餐", nameGerman = "Das letzte Abendmahl", nameSpanish = "La última cena", nameFrench = "Le dernier souper",
                    English_Desc = "The theme was a traditional one for refectories, although the room was not a refectory at the time that Leonardo painted it.",
                    German_Desc = "Das Thema war ein traditionelles Thema für die Mensen, obwohl das Zimmer zu der Zeit, als Leonardo es malte, kein Refektorium war.",
                    French_Desc = "Le thème était traditionnel pour les réfectoires, bien que la salle n'était pas un réfectoire à l'époque où Léonard la peignait.",
                    Chinese_Desc = "《最后的晚餐》是文艺复兴时期由列奥纳多·达·芬奇于米兰的天主教恩宠圣母的多明我会院食堂墙壁上绘成，取材自基督教圣经马太福音第26章，描绘了耶稣在遭罗马兵逮捕的前夕和十二宗徒共进最后一餐时预言\"你们其中一人将出卖我\"后，门徒们显得困惑、哀伤与骚动，纷纷询问耶稣：\"主啊，是我吗？\"的瞬间情景。唯有坐在耶稣右侧的叛徒犹达斯惊恐地将身体后倾，一手抓着出卖耶稣的酬劳，脸部显得阴暗。",
                    Spanish_Desc = "El tema era tradicional para los refectorios, aunque la sala no era un refectorio en el momento en que Leonardo la pintó.",
                    imageID = R.drawable.thelastsupper, eV3ID = 7, selected = false))
            add(ArtPiece(name = "The Great Wave of Kanagawa", artist = "Hokusai", nameChinese = "神奈川的大浪", nameGerman = "Die Große Welle vor Kanagawa", nameSpanish = "La gran ola de Kanagawa", nameFrench = "La grande vague de Kanagawa",
                    English_Desc = "The Great Wave off Kanagawa, also known as The Great Wave or simply The Wave, is a woodblock print by the Japanese ukiyo-e artist Hokusai.",
                    German_Desc = "Die Große Welle vor Kanagawa, auch bekannt als The Great Wave oder einfach The Wave, ist ein Holzschnitt des japanischen Ukiyo-e Künstlers Hokusai.",
                    French_Desc = "La Gran Ola de Kanagawa, también conocida como La Gran Ola o simplemente La Ola, es un grabado en madera del artista ukiyo-e japonés Hokusai.",
                    Chinese_Desc = "《神奈川冲浪里》是日本浮世绘画家葛饰北斋的著名木版画，于1832年出版，是《富岳三十六景》系列作品之一。画中描绘的惊涛巨浪掀卷着渔船，船工们为了生存而努力抗争的图像，远景是富士山。",
                    Spanish_Desc = "La Grande Vague de Kanagawa, également connue sous le nom de La Grande Vague ou simplement La Vague, est une gravure sur bois de l'artiste japonais Ukiyo-e Hokusai.",
                    imageID = R.drawable.tsunami, eV3ID = 8, selected = false))
            add(ArtPiece(name = "Water Lilies", artist = "Claude Monet", nameChinese = "睡莲", nameGerman = "Wasserlilien", nameSpanish = "Nenúfares", nameFrench = "Nénuphars",
                    English_Desc = "The white water lily is a perennial plant that often form dense colonies. The leaves arise on flexible stalks from large thick rhizomes.",
                    German_Desc = "Die Weiße Seerose ist eine mehrjährige Pflanze, die oft dichte Kolonien bildet. Die Blätter entstehen auf flexiblen Stielen aus großen, dicken Rhizomen",
                    French_Desc = "Le nénuphar blanc est une plante vivace qui forme souvent des colonies denses. Les feuilles apparaissent sur des tiges flexibles provenant de gros rhizomes épais.",
                    Chinese_Desc = "《睡莲》是法国印象派画家莫奈所绘的系列油画作品，主要描绘的是莫奈在吉维尼花园中的睡莲。",
                    Spanish_Desc = "El lirio de agua blanca es una planta perenne que a menudo forma colonias densas. Las hojas surgen en tallos flexibles de grandes rizomas gruesos.",
                    imageID = R.drawable.waterlillies, eV3ID = 9, selected = false))
        }

        //Copies arraylist with a new pointer (simple copy creates search bug)
        shownArtPieces.clear() // clear the arraylist
        for (artPiece in allArtPieces) {
            shownArtPieces.add(artPiece)
        }
        clearFindViewByIdCache()
        adapter = PicturesAdapter(shownArtPieces, language)      //update adapter
        val ui = PicturesUI(adapter, language, applicationContext)                //define Anko UI Layout to be used
        ui.setContentView(this)//Set Anko UI to this Activity
        // duration that the device is discoverable
        t = object : Thread() {
            /*This thread will check if the user has selected at least one picture, if they haven't then it will change the background
            * colour of the start button to grey*/
            override fun run() {
                while (!isInterrupted) {
                    try {
                        val count = allArtPieces.count { it.selected }
                        if (count > 0) {
                            runOnUiThread { ui.navigateButton.background = ColorDrawable(Color.parseColor("#24E8EA")) }
                        } else {
                            runOnUiThread { ui.navigateButton.background = ColorDrawable(Color.parseColor("#505050")) }

                        }
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    } catch (e: InterruptedIOException) {
                        Thread.currentThread().interrupt()
                    }
                }
            }
        }
        t.start() /*Start to run the thread*/
    }

    private fun getNewest(): Int {
        var id = 1000
        (0..9)
                .asSequence()
                .filter { allArtPieces[it].eV3ID <= id }
                .forEach { id = allArtPieces[it].eV3ID }
        return id
    }

    private fun getOldest(): Int {
        var id = -1
        (0..9)
                .asSequence()
                .filter { allArtPieces[it].eV3ID > id }
                .forEach { id = allArtPieces[it].eV3ID }
        return id
    }

    private fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }

    private fun surpriseMe(): Int {
        //This should return a number between 0 and 9, Yay!
        return rand(0, 9)
    }

    private fun storeFrequencies() {
        //This function is to be called in oncCreate, the reason is to reduce the users wait time
        for (i in 0..9) {
            //Sets the frequencyList
            try {
                frequencyList[i] = (URL("http://homepages.inf.ed.ac.uk/s1553593/$i.php").readText()).toInt()
            } catch (e: Exception) {
                //Catching all the exceptions that can be thrown
            }
        }
        runOnUiThread {
            frequencyListReady = true //Updates the variable to true
        }
    }

    private fun popularArtPieces(): Int {
        var max = -1000
        if (frequencyListReady) {

        } else {
            Thread.sleep(200)
        }
        try {
            (0..9)
                    .asSequence()
                    .filter { frequencyList[it] > max }
                    .forEach { max = frequencyList[it] }
        } catch (e: Exception) {
            //Catching all the exceptions that can be thrown
        }
        return 1
    }


    //Add mic & search icons in actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pictures, menu)
        return true
    }

    //Define Functions upon actionbar button pressed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        //If searched paintings bring back all paintings, else go back to SelectLanguageActivity
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.search_button -> {
                alert {
                    //Force Keyboard to open
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    var search = ""
                    when (language) {
                        "English" -> {
                            title = "Please enter painting you wish to go to"
                            search = "Search"
                        }
                        "German" -> {
                            title = "Bitte geben Sie ein Gemälde ein, zu dem Sie gehen möchten"
                            search = "Suche"

                        }
                        "Spanish" -> {
                            title = "Por favor, ingrese la pintura a la que desea ir"
                            search = "buscar"

                        }
                        "French" -> {
                            title = "S'il vous plaît entrer la peinture que vous souhaitez aller à"
                            search = "chercher"

                        }
                        "Chinese" -> {
                            title = "请输入你想要去看的作品"
                            search = "搜索"

                        }
                        "other" -> {
                            title = "Please enter painting you wish to go to"
                            search = "Search"

                        }
                        "else" -> {
                            title = "Please enter painting you wish to go to"
                            search = "Search"

                        }
                    }
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    customView {
                        val input = editText {
                            inputType = TYPE_CLASS_TEXT
                        }
                        positiveButton(search) {
                            //Hide keyboard
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0) //translateText
                            if (language == "English") {
                                //No translation needed hence we skip it
                                afterAsync(input.text.toString())
                            } else {
                                //Not english hence we need to translate
                                async {
                                    val transTEXT = translateText(input.text.toString())!!
                                    println("+++++" + transTEXT)
                                    uiThread {
                                        afterAsync(transTEXT)
                                    }
                                }
                            }
                        }
                        negativeButton("Cancel") {
                            //Hide keyboard
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        }
                    }

                    onCancel {
                        //Hide keyboard if alert is cancelled
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    }
                }.show()
            }
            R.id.mic_button -> {
                askSpeechInput()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun afterAsync(transTEXT: String) {
        val regEx = Regex("[^A-Za-z0-9]")
        searchedForPainting = true
        allArtPieces
                .filter {
                    //if substring either way return true (ignoring case & special chars) i.e "Mona" & "MonaLisa" return true
                    regEx.replace(transTEXT, "").contains(regEx.replace(it.artist, ""), ignoreCase = true) || (regEx.replace(transTEXT, "").contains(regEx.replace(it.name, ""), ignoreCase = true)) || regEx.replace(it.artist, "").contains(regEx.replace(transTEXT, ""), ignoreCase = true) || (regEx.replace(it.name, "").contains(regEx.replace(transTEXT, ""), ignoreCase = true))
                }
                .forEach { queriedArtPieces.add(it) }
        shownArtPieces.clear()
        for (artPiece in queriedArtPieces) {
            shownArtPieces.add(artPiece)
        }
        queriedArtPieces.clear()
        adapter.notifyDataSetChanged()
        speakOut_results()
    }

    override fun onBackPressed() {
        if (searchedForPainting) {
            shownArtPieces.clear()
            for (artPiece in allArtPieces) {
                shownArtPieces.add(artPiece)
            }
            adapter.notifyDataSetChanged()
            searchedForPainting = false
        } else {
            val count = allArtPieces.count { it.selected }
            if (count == 0) {
                /*If the user has not made any selections, let them press back no questions asked*/
                super.onBackPressed()
            } else {
                alert("Are you sure you want to leave? Your selection will be lost") {
                    positiveButton {
                        t.interrupt() //Stops the thread
                        async {
                            clearFindViewByIdCache()
                            allArtPieces.clear()
                            switchToMain()
                        }
                        //super.onBackPressed() // Call super.onBackPressed
                    }
                    negativeButton {
                        /*Do nothing*/
                    }
                }.show()
            }
        }
    }

    private fun switchToMain() {
        startActivity<MainActivity>()
    }

    private fun askSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        when (language) {
            "English" -> {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What Art Pieces Are You Looking For?")
            }
            "German" -> {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE")
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nach Welchen Kunstwerken Suchst Du?")
            }
            "Spanish" -> {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Qué Piezas de Arte Estás Buscando?")
            }
            "French" -> {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Quelles Pièces d'Art Recherchez-vous?")
            }
            "Chinese" -> {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "cmn-Hans-CN")
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "你在找什么艺术品？")
            }
            "else" -> {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What Art Pieces Are You Looking For?")
            }
        }
        try {
            async {
                searchedForPainting = true
                startActivityForResult(intent, reqSpeechCode)
            }
        } catch (a: ActivityNotFoundException) {
        } catch (e: java.lang.RuntimeException) {
        } catch (e: java.lang.IllegalArgumentException) {
        }
    }

    private fun afterAsyncSpeech(result: ArrayList<String>) {
        for (i in 0 until result.size) {
            val test = result[i]
            val regEx = Regex("[^A-Za-z0-9 ]")
            if (language == "English") {
                allArtPieces
                        .filter {
                            //if substring either way return true (ignoring case & special chars) i.e "Mona" & "MonaLisa" return true
                            (regEx.replace(test, "").contains(regEx.replace(it.artist, ""), ignoreCase = true) || (regEx.replace(test, "").contains(regEx.replace(it.name, ""), ignoreCase = true)) || regEx.replace(it.artist, "").contains(regEx.replace(test, ""), ignoreCase = true) || (regEx.replace(it.name, "").contains(regEx.replace(test, ""), ignoreCase = true))) && !queriedArtPieces.contains(it)
                        }
                        .forEach {
                            /*This only adds the art piece iff it has not already been added*/
                            queriedArtPieces.add(it)
                        }
            } else {
                val insignificantWords = mutableListOf("the", "of", "a")

                //Get the significant words from the speech
                val cleanSentence1 = regEx.replace(test, "")
                val cleanWords1 = ArrayList(cleanSentence1.split(" "))

                //Convert strings to lowercase for string matching
                for (j in 0 until cleanWords1.size) {
                    cleanWords1[j] = cleanWords1[j].toLowerCase()
                }

                cleanWords1.removeAll(insignificantWords) //removes the insignificant words

                for (artPiece in allArtPieces) {
                    //Get the significant words from the artpiece name
                    val cleanSentence2 = artPiece.name
                    val cleanWords2 = ArrayList(cleanSentence2.split(" "))

                    //Get the significant words from the artpiece artist
                    val cleanSentence3 = artPiece.artist
                    val cleanWords3 = ArrayList(cleanSentence3.split(" "))

                    //Convert strings to lowercase for string matching
                    for (j in 0 until cleanWords2.size) {
                        cleanWords2[j] = cleanWords2[j].toLowerCase()
                    }
                    cleanWords2.removeAll(insignificantWords) //removes the insignificant words

                    for (j in 0 until cleanWords3.size) {
                        cleanWords3[j] = cleanWords3[j].toLowerCase()
                    }
                    cleanWords3.removeAll(insignificantWords) //removes the insignificant words

                    //Find all common significant words
                    val commonWords1 = cleanWords1.intersect(cleanWords2)
                    val commonWords2 = cleanWords1.intersect(cleanWords3)

                    if ((commonWords1.isNotEmpty() || commonWords2.isNotEmpty()) && !queriedArtPieces.contains(artPiece)) queriedArtPieces.add(artPiece)
                }
            }
        }
        shownArtPieces.clear()
        for (artPiece in queriedArtPieces) {
            shownArtPieces.add(artPiece)
        }
        if (queriedArtPieces.size == 0) {
            //Do something if no results are found
        }
        queriedArtPieces.clear()
        adapter.notifyDataSetChanged()
        println("text getting here")
        speakOut_results()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED && requestCode != RESULT_CANCELED) {
            if (data != null) {
                when (requestCode) {
                    reqSpeechCode -> {
                        if (resultCode == RESULT_OK) {
                            var result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            if (language == "English") {
                                afterAsyncSpeech(result)
                                //If the language is english, continue no problemo
                            } else {
                                //If language is not english or other, we run the translator
                                async {
                                    println("+++ Original Text: " + result)
                                    result = translate(result) as ArrayList<String>?
                                    println("+++ Translation: " + result)
                                    uiThread {
                                        afterAsyncSpeech(result)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
