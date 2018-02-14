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
import android.text.InputType.TYPE_CLASS_TEXT
import android.speech.RecognizerIntent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.android.synthetic.*
import java.util.*

val allArtPieces = ArrayList<PicturesActivity.ArtPiece>()

class PicturesActivity : AppCompatActivity() {
    data class ArtPiece(val name: String, val artist: String, val English_Desc: String, val German_Desc: String, val French_Desc: String, val Chinese_Desc: String, val Spanish_Desc: String, val imageID: Int, val eV3ID: Int, var selected: Boolean)

    private var shownArtPieces = ArrayList<ArtPiece>()
    private val REQ_CODE_SPEECH_INPUT = 100
    private var queriedArtPieces = ArrayList<ArtPiece>()
    private var searchedForPainting = false //true if we've searched for a painting
    private var adapter = PicturesAdapter(shownArtPieces, "") //initialise adapter for global class use
    private var voiceInput: TextView? = null
    lateinit var t: Thread
    var language = ""
    fun translate(toTranslate: List<String>): MutableList<String> {
        /*This function takes a list and returns a list of translated text using Google's API
        * This function MUST be called ASYNCHRONOUSLY, if it is not you will crash the activity with a
        * network on main thread exception
        * */
        val translated: MutableList<String> = mutableListOf()
        val API_KEY = "AIzaSyCYryDwlXkmbUfHZS5HLJIIoGoO8Yy5yGw" //My API key, MUST be removed after course finnished
        for (i in toTranslate) {
            val options = TranslateOptions.newBuilder().setApiKey(API_KEY).build()
            val translate = options.service
            val translation = translate.translate(i, Translate.TranslateOption.targetLanguage("en"))
            // println("+++ translated" +translation.translatedText)
            translated.add(translation.translatedText)
        }
        val a = translated
        println("result before sending + $a")
        return a
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        allArtPieces.clear()

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
        allArtPieces.add(ArtPiece("The Birth of Venus",
                "Sandro Botticelli",
                "Depicts the goddess Venus arriving at the shore after her birth",
                "Stellt die Göttin Venus dar, die nach ihrer Geburt am Ufer ankommt",
                "Représente la déesse Vénus arrivant au rivage après sa naissance",
                "意大利文艺复兴时期画家桑德罗·波提切利最著名的作品之一，根据波利齐安诺的长诗吉奥斯特纳而作，描绘罗马神话中女神维纳斯从海中诞生的情景。",
                "Representa a la diosa Venus llegando a la orilla después de su nacimiento",
                R.drawable.birthofvenus, 0, false))
        allArtPieces.add(ArtPiece("The Creation of Adam",
                "Michelangelo", "A fresco painting by Michelangelo, which forms part of the Sistine Chapel's ceiling",
                "Ein Fresko von Michelangelo, das Teil der Sixtinischen Kapelle ist",
                "Une fresque de Michel-Ange, qui fait partie du plafond de la chapelle Sixtine",
                "米开朗基罗的壁画，构成了西斯廷教堂天花板的一部分",
                "Una pintura al fresco de Miguel Ángel, que forma parte del techo de la Capilla Sixtina",
                R.drawable.creationofadam, 1, false))
        allArtPieces.add(ArtPiece("David", "Michelangelo",
                "A masterpiece of Renaissance sculpture created in marble between 1501 and 1504 by Michelangelo",
                "Ein Meisterwerk der Renaissanceskulpturen, das zwischen 1501 und 1504 von Michelangelo aus Marmor geschaffen wurde",
                "Une fresque de Michel-Ange, qui fait partie du plafond de la chapelle Sixtine",
                "米开朗基罗在1501年至1504年之间在大理石中创作的文艺复兴时期雕塑杰作",
                "Una obra maestra de la escultura renacentista creada en mármol entre 1501 y 1504 por Miguel Ángel",
                R.drawable.david, 2, false))
        allArtPieces.add(ArtPiece("Girl with a Pearl Earring", "Johannes Vermeer",
                "Showcasing the electrifying gaze of a young girl adorned with a blue and gold turban.",
                "Den elektrisierenden Blick eines jungen Mädchens zeigen, das mit einem Blau- und Goldturban geschmückt wird.",
                "Mettant en vedette le regard électrisant d'une jeune fille avec un turban bleu et or.",
                "用蓝色和金色的头巾来展示一个年轻女孩的激动凝视。",
                "Exhibiendo la mirada electrizante de una niña adornada con un turbante azul y dorado.",
                R.drawable.girlwithpearlearring, 3, false))
        allArtPieces.add(ArtPiece("Mona Lisa", "Leonardo da Vinci",
                "The title of the painting, which is known in English as Mona Lisa, comes from a description by Renaissance art historian Giorgio Vasari",
                "Der Titel des Gemäldes, der auf Englisch als Mona Lisa bekannt ist, stammt aus einer Beschreibung des Renaissance-Kunsthistorikers Giorgio Vasari",
                "Le titre de la peinture, qui est connu en anglais comme Mona Lisa, vient d'une description par l'historien d'art de la Renaissance Giorgio Vasari",
                "文艺复兴时期画家列奥纳多·达·芬奇所绘的肖像画。",
                "El título de la pintura, que se conoce en inglés como Mona Lisa, proviene de una descripción del historiador del arte del Renacimiento Giorgio Vasari.",
                R.drawable.monalisa, 4, false))

        allArtPieces.add(ArtPiece("Napoleon Crossing the Alps",
                "Jacques-Louis David", "Oil on canvas equestrian portrait of Napoleon Bonaparte painted by the French artist Jacques-Louis David between 1801 and 1805",
                "Öl auf Leinwand Reiterporträt von Napoleon Bonaparte von dem französischen Künstler Jacques-Louis David zwischen 1801 und 1805 gemalt",
                "Huile sur toile portrait équestre de Napoléon Bonaparte peint par l'artiste français Jacques-Louis David entre 1801 et 1805",
                "布面油画法国艺术家雅克·路易·大卫（Jacques-Louis David）在1801年至1805年间绘制的拿破仑·波拿巴的马术画像",
                "Óleo sobre lienzo retrato ecuestre de Napoleón Bonaparte pintado por el artista francés Jacques-Louis David entre 1801 y 1805",
                R.drawable.napoleoncrossingthealps, 5, false))
        allArtPieces.add(ArtPiece("The Starry Night", "Vincent van Gogh",
                "The night sky depicted by van Gogh in the Starry Night painting is brimming with whirling clouds, shining stars, and a bright crescent moon.",
                "Der Nachthimmel, den van Gogh in der Sternennacht zeigt, ist voll von wirbelnden Wolken, leuchtenden Sternen und einer hellen Mondsichel.\n" + "",
                "Le ciel nocturne représenté par Van Gogh dans la peinture de la nuit étoilée déborde de nuages tourbillonnants, ",
                "梵高在“星夜之画”中描绘的夜空充满了旋云，闪亮的星星和明亮的新月。\n" + "梵高在“星夜之画”中描绘的夜空充满了旋云，闪亮的星星和明亮的新月。\n" +
                        "梵高在“星夜之画”中描绘的夜空充满了旋云，闪亮的星星和明亮的新月。\n",
                "El cielo nocturno representado por Van Gogh en la pintura de la Noche Estrellada rebosa de nubes giratorias, estrellas brillantes y una brillante luna creciente.\n" +
                        "", R.drawable.starrynight, 6, false))
        allArtPieces.add(ArtPiece("The Last Supper", "Leonardo da Vinci",
                "The theme was a traditional one for refectories, although the room was not a refectory at the time that Leonardo painted it.",
                "Das Thema war ein traditionelles Thema für die Mensen, obwohl das Zimmer zu der Zeit, als Leonardo es malte, kein Refektorium war.",
                "Le thème était traditionnel pour les réfectoires, bien que la salle n'était pas un réfectoire à l'époque où Léonard la peignait.",
                "这个主题是一个传统的主题，虽然这个房间在莱昂纳多画的时候并不是一个食堂。",
                "El tema era tradicional para los refectorios, aunque la sala no era un refectorio en el momento en que Leonardo la pintó.",
                R.drawable.thelastsupper, 7, false))
        allArtPieces.add(ArtPiece("The Great Wave of Kanagawa", "Hokusai",
                "The Great Wave off Kanagawa, also known as The Great Wave or simply The Wave, is a woodblock print by the Japanese ukiyo-e artist Hokusai.",
                "Die Große Welle vor Kanagawa, auch bekannt als The Great Wave oder einfach The Wave, ist ein Holzschnitt des japanischen Ukiyo-e Künstlers Hokusai.",
                "La Gran Ola de Kanagawa, también conocida como La Gran Ola o simplemente La Ola, es un grabado en madera del artista ukiyo-e japonés Hokusai.",
                "神奈川的大波浪，也被称为“大波浪”，简称“波浪”，是日本浮世绘艺术家北征的木版画。",
                "La Grande Vague de Kanagawa, également connue sous le nom de La Grande Vague ou simplement La Vague, est une gravure sur bois de l'artiste japonais Ukiyo-e Hokusai.",
                R.drawable.tsunami, 8, false))
        allArtPieces.add(ArtPiece("Water Lilies", "Claude Monet",
                "The white water lily is a perennial plant that often form dense colonies. The leaves arise on flexible stalks from large thick rhizomes.",
                "The white water lily is a perennial plant that often form dense colonies. The leaves arise on flexible stalks from large thick rhizomes.",
                "Le nénuphar blanc est une plante vivace qui forme souvent des colonies denses. Les feuilles apparaissent sur des tiges flexibles provenant de gros rhizomes épais.",
                "白色的睡莲是多年生的植物，经常形成密集的菌落。叶子由柔软的茎粗大的根状茎产生。",
                "El lirio de agua blanca es una planta perenne que a menudo forma colonias densas. Las hojas surgen en tallos flexibles de grandes rizomas gruesos.",
                R.drawable.waterlillies, 9, false))

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
                            runOnUiThread { ui.navigateButton.background = ColorDrawable(Color.parseColor("#D3D3D3")) }

                        }
                        Thread.sleep(50)
                    } catch (e: InterruptedException) {
                    }
                }
            }
        }
        t.start()
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
                    title = "Please enter painting you wish to go to"
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    customView {
                        val input = editText {
                            inputType = TYPE_CLASS_TEXT
                        }
                        val regEx = Regex("[^A-Za-z0-9]")
                        positiveButton("Search") {
                            //Hide keyboard
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                            searchedForPainting = true
                            allArtPieces
                                    .filter {
                                        //if substring either way return true (ignoring case & special chars) i.e "Mona" & "MonaLisa" return true
                                        regEx.replace(input.text, "").contains(regEx.replace(it.artist, ""), ignoreCase = true) || (regEx.replace(input.text, "").contains(regEx.replace(it.name, ""), ignoreCase = true)) || regEx.replace(it.artist, "").contains(regEx.replace(input.text, ""), ignoreCase = true) || (regEx.replace(it.name, "").contains(regEx.replace(input.text, ""), ignoreCase = true))
                                    }
                                    .forEach { queriedArtPieces.add(it) }
                            shownArtPieces.clear()
                            for (artPiece in queriedArtPieces) {
                                shownArtPieces.add(artPiece)
                            }
                            queriedArtPieces.clear()
                            adapter.notifyDataSetChanged()
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

    override fun onBackPressed() {
        if (searchedForPainting) {
            shownArtPieces.clear()
            for (artPiece in allArtPieces) {
                shownArtPieces.add(artPiece)
            }
            adapter.notifyDataSetChanged()
            searchedForPainting = false
        } else {
            alert("Are you sure you want to leave? Your selection will be lost") {
                positiveButton {
                    t.interrupt() //Stops the thread
                    async {
                        clearFindViewByIdCache()
                        allArtPieces.clear()
                    }
                    super.onBackPressed() // Call super.onBackPressed
                }
                negativeButton {
                    /*Do nothing*/
                }
            }.show()
        }
    }

    fun askSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What art pieces are you looking for?")
        try {
            async {
                searchedForPainting = true
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
            }
        } catch (a: ActivityNotFoundException) {
        } catch (e: java.lang.RuntimeException) {
        } catch(e: java.lang.IllegalArgumentException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED && requestCode != RESULT_CANCELED) {
            if (data != null) {
                when (requestCode) {
                    REQ_CODE_SPEECH_INPUT -> {
                        var canContinue = false
                        if (resultCode == RESULT_OK) {
                            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            if (language == "English" || language == "Other") {
                                canContinue = true
                            } else {
                                async {
                                    val result2 = translate(result)
                                    uiThread {
                                        println("result + $result2")
                                            canContinue = true
                                            println("turned++ $canContinue")
                                    }
                                }
                            }
                            Thread.sleep(900)

                            voiceInput?.text = result[0]
                            for (i in 0..result.size - 1) {
                                val test = result[i]
                                val regEx = Regex("[^A-Za-z0-9]")
                                allArtPieces
                                        .filter {
                                            //if substring either way return true (ignoring case & special chars) i.e "Mona" & "MonaLisa" return true
                                            (regEx.replace(test, "").contains(regEx.replace(it.artist, ""), ignoreCase = true) || (regEx.replace(test, "").contains(regEx.replace(it.name, ""), ignoreCase = true)) || regEx.replace(it.name, "").contains(regEx.replace(test, ""), ignoreCase = true) || (regEx.replace(it.name, "").contains(regEx.replace(test, ""), ignoreCase = true))) && !queriedArtPieces.contains(it)
                                        }
                                        .forEach {
                                            /*This only adds the art piece iff it has not already been added*/
                                            queriedArtPieces.add(it)
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
                        }
                    }
                }
            }
        }
    }
}
