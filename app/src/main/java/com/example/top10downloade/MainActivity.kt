package com.example.top10downloade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var rvAdapter: RVAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var btnGet: Button
    private var list = ArrayList<String>()
    private var name = ""
    private var text = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(list)
        recyclerView.adapter = rvAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnGet = findViewById(R.id.btnGet)

        btnGet.setOnClickListener { parseRRS() }

    }

    private fun parseRRS() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { XMLParser() }.await()
            try {
                withContext(Dispatchers.Main) {
                    rvAdapter.notifyDataSetChanged()
                }
            } catch (e: java.lang.Exception) {
                Log.d("MAIN", "Unable to get data")
            }
        }
    }

    fun XMLParser(){
        try{
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            val url = URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
            parser.setInput(url.openStream(), null)
            var eventType = parser.eventType
            while(eventType != XmlPullParser.END_DOCUMENT){
                val tagName = parser.name
                when(eventType){
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> when {
                        tagName.equals("im:name", true) -> {
                            name = text
                            list.add(name)
                        }
                        else -> {}
                    }
                    else -> {}
                }
                eventType = parser.next()
            }
        }catch(e: XmlPullParserException){
            e.printStackTrace()
            Log.d("MAIN", "${e}")

        }catch(e: IOException){
            e.printStackTrace()
            Log.d("MAIN", "${e}")

        }
    }
}