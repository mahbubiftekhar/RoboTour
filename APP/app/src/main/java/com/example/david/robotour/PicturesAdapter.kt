package com.example.david.robotour

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import org.jetbrains.anko.*
import java.util.ArrayList

@Suppress("DEPRECATION")
class PicturesAdapter(private val list: ArrayList<PicturesActivity.ArtPiece>, private val language: String) : BaseAdapter() {

    //Describe ListView Layout
    override fun getView(i: Int, v: View?, parent: ViewGroup?): View {
        //Change text depending on language selected
        val languageText = when (language) {
            "English" -> list[i].English_Desc
            "French" -> list[i].French_Desc
            "Chinese" -> list[i].Chinese_Desc
            "Spanish" -> list[i].Spanish_Desc
            "German" -> list[i].German_Desc
            else -> ""
        }
        val element = with(parent!!.context) {
            //Layout for a list view item
            linearLayout {
                id = 0
                lparams(width = matchParent, height = wrapContent)
                padding = dip(10)
                orientation = LinearLayout.HORIZONTAL

                imageView {
                    val imageID = list[i].imageID
                    image = resources.getDrawable(imageID)
                }
                tableLayout {
                    textView {
                        id = 0
                        text = when (language) {
                            "German" -> list[i].nameGerman
                            "French" -> list[i].nameFrench
                            "Spanish" -> list[i].nameSpanish
                            "Chinese" -> list[i].nameChinese
                            else -> list[i].name

                        }
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                        padding = dip(5)
                    }
                    textView {
                        id = 0
                        text = languageText
                        textSize = 16f
                        typeface = Typeface.DEFAULT
                        padding = dip(5)
                    }
                }
            }
        }
        if (list[i].selected) {
            element.background = ColorDrawable(Color.parseColor("#00C9FF"))
        } else {
            element.background = ColorDrawable(Color.parseColor("#FFFFFF"))
        }
        return element
    }

    //Returns the String stored at position x of the list
    override fun getItem(i: Int): Int {
        return list[i].eV3ID
    }

    //Returns the length of the list
    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        //can be used to return the item's ID column of table
        return 0L
    }

}