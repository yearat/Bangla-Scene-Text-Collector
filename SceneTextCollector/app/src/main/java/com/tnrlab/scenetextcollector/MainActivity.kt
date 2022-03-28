package com.tnrlab.scenetextcollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       val CaptureBtn = findViewById<Button>(R.id.CaptureBtn)
        val RankBtn = findViewById<Button>(R.id.RankBtn)
        val LogoutBtn = findViewById<Button>(R.id.LogoutBtn)
        val testAFunctionBtn = findViewById<Button>(R.id.testAFunctionBtn)

        CaptureBtn.setOnClickListener {
            val intent = Intent(this, CaptureImage::class.java)
            startActivity(intent)
        }

        RankBtn.setOnClickListener {
            val intent = Intent(this, RankList::class.java)
            startActivity(intent)
        }

        LogoutBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        testAFunctionBtn.setOnClickListener {
            Toast.makeText(this, "Test A Function", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DrawAndLabel::class.java)
            startActivity(intent)
        }

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")

        val UserIdTv = findViewById<TextView>(R.id.UserIdTv)
        val UserEmailTv = findViewById<TextView>(R.id.UserEmailTv)

        //UserIdTv.text = "User ID:: $userId"
        UserEmailTv.text = "Email ID:: $emailId"

        LogoutBtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

    }
}