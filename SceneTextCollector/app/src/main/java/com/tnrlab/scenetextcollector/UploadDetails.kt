package com.tnrlab.scenetextcollector

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class UploadDetails : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_details)



        val UploadUserIdTv = findViewById<TextView>(R.id.UploadUserIdTv)
        val UploadLocationTv = findViewById<TextView>(R.id.UploadLocationTv)
        val UploadImageLinkTv = findViewById<TextView>(R.id.UploadImageLinkTv)
        val UploadTimeTv = findViewById<TextView>(R.id.UploadTimeTv)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        UploadUserIdTv.text = "User: $uid"

        // This code doesn't work as previous intent does not wait to send link
        val ImageLink = intent.getStringExtra("ImageLink")
        UploadImageLinkTv.text = "Image Link: $ImageLink"

        //UploadLocationTv.text = " $latitude and $longitude "


    }


}