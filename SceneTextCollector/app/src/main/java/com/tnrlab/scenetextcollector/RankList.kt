package com.tnrlab.scenetextcollector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class RankList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_list)

        val listView = findViewById<ListView>(R.id.RankListLv)

        val users = arrayOf(
            "Yearat",
            "Rakib",
            "Shourav",
            "Alamin",
            "Priyanka",
            "Peter Parker"
        )

        val userRatings = mapOf("Yearat" to 80,
            "Rakib" to 66,
            "Shourav" to 62

            )

        val arrayAdapter:ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, users
        )


        listView.adapter = arrayAdapter



    }
}