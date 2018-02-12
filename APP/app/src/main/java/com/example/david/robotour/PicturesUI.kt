package com.example.david.robotour

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.LinearLayout
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.*
import java.io.IOException
import java.util.ArrayList


class PicturesUI(private val PicturesAdapter: PicturesAdapter, val language: String) : AnkoComponent<PicturesActivity> {
    lateinit var a: String
    var navigate = ""

    override fun createView(ui: AnkoContext<PicturesActivity>): View = with(ui) {
        return relativeLayout {
            when (language) {
                "Spanish" -> {
                    a = "Empezar recorrido"
                    navigate = "Navegar a obras de arte seleccionadas?"
                }
                "German" -> {
                    a = "Tour starten"
                    navigate = "zu ausgewählten Bildern navigieren?"
                }
                "French" -> {
                    a = "Tour initial"
                    navigate = "naviguer vers l'illustration sélectionnée?"
                }
                "Chinese" -> {
                    a = "开始旅游"
                    navigate = "导航到选定的艺术品？"
                }
                else -> {
                    a = "Start tour"
                    navigate = "navigate to selected artwork?"
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
                button(a) {
                    textSize = 32f
                    background = ColorDrawable(resources.getColor(R.color.roboTourTeal))
                    onClick {
                        //need to translate here
                        alert(navigate) {
                            positiveButton("Yes") {
                                async {
                                    sendList()
                                }
                                val progressDialog = indeterminateProgressDialog("Waiting for other user to select paintings...")
                                progressDialog.show()
                                async {
                                    startActivity<Waiting>("language" to language)
                                    uiThread {
                                        progressDialog.dismiss()
                                    }
                                }
                            }
                            negativeButton("No") { }
                        }.show()
                    }
                }.lparams { width = matchParent; height = wrapContent; weight = 0.0f }
                lparams { width = matchParent; height = matchParent; orientation = LinearLayout.VERTICAL }
            }
        }
    }

    fun sendList() {
        /*This function will upload to the server the required items simply - positive uploads only*/
        allArtPieces
                .filter { it.selected }
                .map { it.eV3ID }
                .forEach { sendPUT("T", "http://homepages.inf.ed.ac.uk/s1553593/$it.php") }
    }

    fun sendPUT(command: String, url: String) {
        async {
            val httpclient = DefaultHttpClient()
            val httpPost = HttpPost(url)
            try {
                val nameValuePairs = ArrayList<NameValuePair>(4)
                nameValuePairs.add(BasicNameValuePair("command", command))
                httpPost.entity = UrlEncodedFormEntity(nameValuePairs)
                httpclient.execute(httpPost)
            } catch (e: ClientProtocolException) {
            } catch (e: IOException) {
            }
        }
    }

}