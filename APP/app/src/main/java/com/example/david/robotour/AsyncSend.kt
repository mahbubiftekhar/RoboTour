package com.example.david.robotour

import java.io.IOException
import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException

/**
* Created by MAHBUBIFTEKHAR on 12/03/2018.
*/



class AsyncSend : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg urls: String): String {
        return try {
            return ""
        } catch (e: IOException) {
            e.printStackTrace()
            /*If we encounter an issue, set downloadXML finished as false, thus the user
            * will be able to retry the download from the MapsActivity*/
            ""
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            /* If we encounter an issue, set downloadXML finished as false, thus the user
             * will be able to retry the download from the MapsActivity*/
            ""
        }
    }


}