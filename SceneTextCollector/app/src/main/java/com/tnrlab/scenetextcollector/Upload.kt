package com.tnrlab.scenetextcollector

import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Upload : AppCompatActivity() {

    var latitude: String? =null
    var longitude: String? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()


        val imageUri = intent.getParcelableExtra<Uri>("ImageUri")
        val textUri = intent.getParcelableExtra<Uri>("TextUri")

        val AnnotationIV = findViewById<ImageView>(R.id.AnnotaionIV)
        AnnotationIV.setImageBitmap(picture)



        val UploadBtn = findViewById<Button>(R.id.UploadBtn)
        val AddAnnotationBtn = findViewById<Button>(R.id.AddAnnotationBtn)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val progressBarTv = findViewById<TextView>(R.id.progressBarTv)
        var ImageFileLink: String? = null
        var TextFileLink: String? = null
        var imageFileName : String? = null
        var textFileName : String? = null
        val currentTime = System.currentTimeMillis() / 1000

        progressBar.visibility = View.GONE
        progressBarTv.visibility = View.GONE

        AddAnnotationBtn.setOnClickListener{
            uploadToDatabase(userId!!.toString(),
            userEmail!!,
            imageFileName!!,
            ImageFileLink!!,
            textFileName!!,
            TextFileLink!!,
            latitude!!,
            longitude!!)

            latitude = null
            longitude = null

            progressBar.progress = 0
            progressBarTv.text = "Uploaded 100%"
        }



        UploadBtn.setOnClickListener{



            if(imageUri != null) {
               val imageExtension = MimeTypeMap.getSingleton()
                   .getExtensionFromMimeType(contentResolver.getType(imageUri!!))

                imageFileName = userEmail + currentTime + "." + imageExtension


                val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                    "collected_data/${imageFileName!!}"
                )

                textFileName = userEmail + currentTime + ".txt"


                val sRef2 : StorageReference = FirebaseStorage.getInstance().reference.child(
                    "collected_data/${textFileName!!}"
                )

                sRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                Toast.makeText(this, "Uploaded to: $url", Toast.LENGTH_LONG).show()
                                ImageFileLink = url.toString()
                            }.addOnFailureListener{ exception ->
                                Toast.makeText(
                                    this,
                                    exception.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e(javaClass.simpleName, exception.message, exception)
                            }
                    }

                progressBar.visibility = View.VISIBLE
                progressBarTv.visibility = View.VISIBLE


                sRef2.putFile(textUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                Toast.makeText(this, "Uploaded to: $url", Toast.LENGTH_LONG).show()
                                TextFileLink = url.toString()

//                                uploadToDatabase(userId!!.toString(),
//                                    userEmail!!,
//                                    imageFileName!!,
//                                    ImageFileLink!!,
//                                    textFileName!!,
//                                    TextFileLink!!,
//                                    latitude!!,
//                                    longitude!!)
//
//                                progressBar.progress = 0
//                                progressBarTv.text = "Uploaded 100%"

                            }.addOnFailureListener{ exception ->
                                Toast.makeText(
                                    this,
                                    exception.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e(javaClass.simpleName, exception.message, exception)
                            }
                    }.addOnProgressListener{ taskSnapshot ->
                        val progress = (100*taskSnapshot.bytesTransferred)/taskSnapshot.totalByteCount
                        progressBar.progress = progress.toInt()
                        progressBarTv.text = "${progress} %"
                    }




                // Uploading Done, Going to a new activity
//                val intent = Intent(this, UploadDetails::class.java)
//                intent.putExtra("ImageLink", ImageFileLink)
//                startActivity(intent)
//                finish()

            }
            else {
                Toast.makeText(
                    this,
                    "Please select the image to upload",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }





    class dataObject(val email: String?, val imageName: String?, val imageLink: String?, val textName: String?, val textLink: String?, val latitude: String?, val longitude: String?)

    private fun uploadToDatabase(Uid: String, Email: String, ImageName: String, ImageLink: String, TextName: String, TextLink: String, Lat: String, Long: String) {

        println("user id " + Uid)
        println("user email " + Email)
        println("image name " + ImageName)
        println("image link " + ImageLink)
        println("text name " + TextName)
        println("text link " + TextLink)
        println("latitude " + Lat)
        println("Longitude " + Long)

        

        if(!Uid.isEmpty() && !Email.isEmpty() && !ImageName.isEmpty() && !ImageLink.isEmpty() && !TextName.isEmpty() && !TextLink.isEmpty()) {
            val ImageNameForFirebase = ImageName.replace(".", "")
            val data = dataObject(Email, ImageName, ImageLink, TextName, TextLink, Lat, Long)
            val database: DatabaseReference = FirebaseDatabase.getInstance("https://bn-scn-txt-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("data")

            database.child(ImageNameForFirebase).setValue(data).addOnSuccessListener {
                Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this, "Found a null in uploadToDatabase()", Toast.LENGTH_SHORT).show()
        }
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
                latitude = it.latitude.toString()
                longitude = it.longitude.toString()
                Toast.makeText(applicationContext, "${it.latitude} and ${it.longitude}", Toast.LENGTH_LONG).show()
            }
        }
    }

}