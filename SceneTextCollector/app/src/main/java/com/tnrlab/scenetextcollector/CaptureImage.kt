package com.tnrlab.scenetextcollector

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.location.Location
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.get
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.io.File
import java.io.FileWriter
import java.io.IOException

// ANOTHER ACTIVITY USES THE IMAGE HENCE DECLARED GLOBAL
var picture: Bitmap? = null
var textFile: File? = null
var latitude: String? =null
var longitude: String? = null

class CaptureImage : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


  // val CameraBtn = findViewById<Button>(R.id.CameraBtn)
    private var selectedImageFileURI: Uri? = null
    private var capturedImageFileURI: Uri? = null
    private var currentPhotoPath: String? = null
    private var textFileURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        // HANDLING PERMISSIONS FOR CAMERA
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 111)
        } else {
         //  CameraBtn.isEnabled = trueload bitmap image from uri
        }

        val CameraBtn = findViewById<Button>(R.id.CameraBtn)
       CameraBtn.setOnClickListener {

           var fileName: String = "photo"
           var storageDirectory: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
           try {
               var imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
               currentPhotoPath = imageFile.absolutePath
               capturedImageFileURI = FileProvider.getUriForFile(this, "com.tnrlab.scenetextcollector.fileprovider",imageFile)

               textFile = File.createTempFile(fileName, ".txt", storageDirectory)
               textFileURI = FileProvider.getUriForFile(this, "com.tnrlab.scenetextcollector.fileprovider", textFile!!)

               /*val fileWriter = FileWriter(textFile)
               fileWriter.write("Testing")
               fileWriter.flush()*/

               var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
               intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageFileURI)
               startActivityForResult(intent, 101)

           } catch (e: IOException) {
               e.printStackTrace()
           }

        }

        // FOR GALLERY
        val GalleryBtn = findViewById<Button>(R.id.GalleryBtn)
        GalleryBtn.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Select Image
                val galleryIntent = Intent(
                    Intent.ACTION_OPEN_DOCUMENT,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent, 222)
            }
            else {
                // Request For Permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 121)
            }
        }

    } // OnCreate Func

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Request result for Camera
        if(requestCode == 101) {
            //picture = data?.getParcelableExtra<Bitmap>("data")
            // SENDING THE IMAGE TO ANNOTATION ACTIVITY
             picture = BitmapFactory.decodeFile(currentPhotoPath)
            if(picture != null) {
                var intent = Intent(this,DrawAndLabel::class.java)
                intent.putExtra("ImageUri", capturedImageFileURI)
                intent.putExtra("TextUri", textFileURI)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error camera onactivityResult", Toast.LENGTH_SHORT).show()
            }
           /* picture = BitmapFactory.decodeFile(currentPhotoPath)
            var intent = Intent(this, DrawAndLabel::class.java)
            startActivity(intent)
            finish() */

        }
        // Request result for Gallery
        else if(requestCode == 222){
            if (data != null) {
                try {
                    // Get Path
                    selectedImageFileURI = data.data!!
                    // Load A Bitmap Image from Uri
                    picture = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageFileURI)
                    var storageDirectory: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    textFile = File.createTempFile("Text", ".txt", storageDirectory)
                    textFileURI = FileProvider.getUriForFile(this, "com.tnrlab.scenetextcollector.fileprovider", textFile!!)
                   /*
                   var intent = Intent(this, ImageAnnotation::class.java)
                    intent.putExtra("ImageUri", mSelectedImageFileURI)
                    startActivity(intent)
                    */
                    var intent = Intent(this, DrawAndLabel::class.java)
                    intent.putExtra("ImageUri", selectedImageFileURI)
                    intent.putExtra("TextUri", textFileURI)
                    startActivity(intent)
                    finish()


                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Image Selection Failed", Toast.LENGTH_LONG ).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //CameraBtn.isEnabled = true
        }
        else if(requestCode == 121 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 222)
        }
    }

}