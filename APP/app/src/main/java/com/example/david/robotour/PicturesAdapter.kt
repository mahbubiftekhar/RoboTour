package com.example.david.robotour

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import org.jetbrains.anko.*
import java.util.ArrayList

class PicturesAdapter(val list: ArrayList<PicturesActivity.ArtPiece>, val language:String) : BaseAdapter() {

    // /Describe ListView Layout using Anko
    override fun getView(i: Int, v: View?, parent: ViewGroup?): View {

        //Change text depending on language selected
        val languageText = when (language) {
            "English" ->  list[i].English_Desc
            "French" ->  list[i].French_Desc
            "Chinese" ->  list[i].Chinese_Desc
            "Spanish" ->  list[i].Spanish_Desc
            "German" ->  list[i].German_Desc
            else -> ""
        }
        return with(parent!!.context) {
            //Layout for a list view item
            linearLayout {
                id = 0
                lparams(width = matchParent, height = wrapContent)
                padding = dip(10)
                orientation = LinearLayout.HORIZONTAL

                imageView {
                    val imageID = list.get(i).imageID
                    image = resources.getDrawable(imageID)
                }
                tableLayout {
                    textView {
                        id = 0
                        text = list[i].name
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
    }
     //Returns the String stored at position x of the list
    override fun getItem(i: Int): String {
        return list[i].name
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