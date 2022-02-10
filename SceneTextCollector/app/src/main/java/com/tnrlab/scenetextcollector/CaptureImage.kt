package com.tnrlab.scenetextcollector

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.get
import java.io.IOException

// ANOTHER ACTIVITY USES THE IMAGE HENCE DECLARED GLOBAL
var picture: Bitmap? = null

class CaptureImage : AppCompatActivity() {

  // val CameraBtn = findViewById<Button>(R.id.CameraBtn)
    private var mSelectedImageFileURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)


       // CameraBtn.isEnabled = false

        // HANDLING PERMISSIONS FOR CAMERA
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 111)
        } else {
         //  CameraBtn.isEnabled = trueload bitmap image from uri
        }

        val CameraBtn = findViewById<Button>(R.id.CameraBtn)
       CameraBtn.setOnClickListener {
           var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
           startActivityForResult(intent, 101)
        }

        // FOR GALLERY
        val GalleryBtn = findViewById<Button>(R.id.GalleryBtn)
        GalleryBtn.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Select Image
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101) {
            picture = data?.getParcelableExtra<Bitmap>("data")
           /* val MainIV = findViewById<ImageView>(R.id.MainIV)
            MainIV.setImageBitmap(picture) */
            // SENDING THE IMAGE TO ANNOTATION ACTIVITY
            if(picture != null) {
                var intent = Intent(this, ImageAnnotation::class.java)
                startActivity(intent)
            }
        }
        else if(requestCode == 222){
            if (data != null) {
                try {
                    // Get Path
                    mSelectedImageFileURI = data.data!!
                    // Load A Bitmap Image from Uri
                    picture = MediaStore.Images.Media.getBitmap(this.contentResolver, mSelectedImageFileURI)

                    var intent = Intent(this, ImageAnnotation::class.java)
                    intent.putExtra("ImageUri", mSelectedImageFileURI)
                    startActivity(intent)

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