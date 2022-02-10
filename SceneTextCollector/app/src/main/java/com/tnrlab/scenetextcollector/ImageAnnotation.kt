package com.tnrlab.scenetextcollector

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.logging.Level.parse

class ImageAnnotation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_annotation)

        var imageUri = intent.getParcelableExtra<Uri>("ImageUri")

        val AnnotationIV = findViewById<ImageView>(R.id.AnnotaionIV)
        AnnotationIV.setImageBitmap(picture)


        val UploadBtn = findViewById<Button>(R.id.UploadBtn)

        UploadBtn.setOnClickListener{
            if(imageUri != null) {
               val imageExtension = MimeTypeMap.getSingleton()
                   .getExtensionFromMimeType(contentResolver.getType(imageUri!!))

                val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                    "Image " + System.currentTimeMillis() + "." + imageExtension
                )

                sRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                Toast.makeText(this, "Uploaded to: $url", Toast.LENGTH_LONG).show()
                            }.addOnFailureListener{ exception ->
                                Toast.makeText(
                                    this,
                                    exception.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e(javaClass.simpleName, exception.message, exception)
                            }
                    }

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
}