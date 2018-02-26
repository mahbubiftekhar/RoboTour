package com.example.david.robotour

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import java.io.IOException
import java.util.ArrayList

class PicturesUI(private val PicturesAdapter: PicturesAdapter, val language: String, val ctx: Context) : AnkoComponent<PicturesActivity> {
    private lateinit var a: String
    private var navigate = ""
    lateinit var navigateButton: Button
    private var toastText = ""
    private var positive = ""
    private var negative = ""

    private fun notifyUser() {
        Toast.makeText(ctx,toastText,Toast.LENGTH_LONG).show()
    }

    override fun createView(ui: AnkoContext<PicturesActivity>): View = with(ui) {
        return relativeLayout {
            when (language) {
                "Spanish" -> {
                    a = "Empezar recorrido"
                    navigate = "Navegar a obras de arte seleccionadas?"
                    toastText = "Seleccione 1 o más obras de arte para visitar"
                    positive = "Sí"
                    negative = "No"
                }
                "German" -> {
                    a = "Tour starten"
                    navigate = "zu ausgewählten Bildern navigieren?"
                    toastText = "Bitte wähle 1 oder mehr Kunstwerke aus, die du besuchen möchtest"
                    positive = "Ja"
                    negative = "Nein"
                }
                "French" -> {
                    a = "Tour initial"
                    navigate = "naviguer vers l'illustration sélectionnée?"
                    toastText = "Veuillez sélectionner une ou plusieurs œuvres à visiter"
                    positive = "Oui"
                    negative = "Non"
                }
                "Chinese" -> {
                    a = "开始旅游"
                    navigate = "导航到选定的艺术品？"
                    toastText = "请选择一件或多件作品参观"
                    positive = "是"
                    negative = "没有"
                }
                else -> {
                    a = "Start tour"
                    navigate = "Navigate to selected artwork?"
                    toastText = "Please select 1 or more artworks to visit"
                    positive = "Yes"
                    negative = "No"
                }
            }
            linearLayout {
                listView {
                    adapter = PicturesAdapter
                    //val selected = BooleanArray(adapter.count, { _ -> false})
                    onItemClick { _, view, i, _ ->
                        val picID = PicturesAdapter.getItem(i)
                        if (allArtPieces[picID].selected) {
                            view?.background = ColorDrawable(resources.getColor(R.color.androidsBackground))
                        } else {
                            view?.background = ColorDrawable(resources.getColor(R.color.highlighted))
                        }
                        allArtPieces[picID].selected = !allArtPieces[picID].selected
                    }
                }.lparams { width = matchParent; height = dip(0); weight = 1.0f }
                navigateButton = button(a) {
                    textSize = 32f
                    background = ColorDrawable(resources.getColor(R.color.roboTourTeal))
                    onClick {
                        var isSelected = 0
                        allArtPieces
                                .filter { it.selected }
                                .map { it.eV3ID }
                                .forEach { isSelected++ }
                        if (isSelected == 0) {
                            notifyUser()
                        } else {
                            //need to translate here
                            alert(navigate) {
                                positiveButton(positive) {
                                    async{
                                        sendList()
                                        sendPUTNEW(16, "T")
                                    }
                                    async {
                                        val a = PicturesActivity()
                                        a.t.interrupt() //Stops the thread
                                    }
                                    startActivity<Waiting>("language" to language)
                                }
                                negativeButton(negative) {
                                    // navigateButton.background = ColorDrawable(Color.parseColor("#D3D3D3"))
                                }
                            }.show()
                        }

                    }
                }.lparams { width = matchParent; height = wrapContent; weight = 0.0f }
                lparams { width = matchParent; height = matchParent; orientation = LinearLayout.VERTICAL }
            }
        }
    }
    private fun sendList() {
        /*This function will upload to the server the required items simply - positive uploads only*/
        allArtPieces
                .filter { it.selected }
                .forEach { sendPUTNEW(it.eV3ID, "T") }
    }

    private fun sendPUTNEW(identifier: Int, command: String) {
        val url = "http://homepages.inf.ed.ac.uk/s1553593/receiver.php"
        /*DISCLAIMER: When calling this function, if you don't run in an async, you will get
        * as security exception - just a heads up */
        val httpclient = DefaultHttpClient()
        val httPpost = HttpPost(url)
        try { val nameValuePairs = ArrayList<NameValuePair>(4)
            nameValuePairs.add(BasicNameValuePair("command$identifier", command))
            httPpost.entity = UrlEncodedFormEntity(nameValuePairs)
            httpclient.execute(httPpost)
        } catch (e: ClientProtocolException) {
        } catch (e: IOException) {
        }
    }
}