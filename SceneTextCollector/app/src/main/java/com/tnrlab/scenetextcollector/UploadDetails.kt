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

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var latitude: String? =null
    var longitude: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_details)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()

        val UploadUserIdTv = findViewById<TextView>(R.id.UploadUserIdTv)
        val UploadLocationTv = findViewById<TextView>(R.id.UploadLocationTv)
        val UploadImageLinkTv = findViewById<TextView>(R.id.UploadImageLinkTv)
        val UploadTimeTv = findViewById<TextView>(R.id.UploadTimeTv)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        UploadUserIdTv.text = "User: $uid"

        // This code doesn't work as previous intent does not wait to send link
        val ImageLink = intent.getStringExtra("ImageLink")
        UploadImageLinkTv.text = "Image Link: $ImageLink"

        UploadLocationTv.text = " $latitude and $longitude "


    }

    private  fun fetchLocation() {

        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it != null){
                latitude = {it.latitude}.toString()
                longitude = {it.longitude}.toString()
                Toast.makeText(applicationContext, "${it.latitude} and ${it.longitude}", Toast.LENGTH_LONG).show()
            }
        }
    }
}