package com.tnrlab.scenetextcollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import  com.google.android.gms.tasks.OnCompleteListener
import  com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        val LoginTv = findViewById<TextView>(R.id.LoginTv)

        LoginTv.setOnClickListener{
            startActivity(Intent(this, LoginPage::class.java))
        }

        val RegisterConfirmBtn = findViewById<Button>(R.id.RegisterConfirmBtn)
        val RegisterEmailEt = findViewById<EditText>(R.id.RegisterEmailEt)
        val RegisterPasswordEt = findViewById<EditText>(R.id.RegisterPasswordEt)

        RegisterConfirmBtn.setOnClickListener{
            when {
                TextUtils.isEmpty(RegisterEmailEt.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Please Enter Email", Toast.LENGTH_LONG).show()
                }

                TextUtils.isEmpty(RegisterPasswordEt.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Please Enter Password", Toast.LENGTH_LONG).show()
                }
                else -> {
                    val email: String = RegisterEmailEt.text.toString().trim { it <= ' ' }
                    val password: String = RegisterPasswordEt.text.toString().trim { it <= ' ' }

                    // CREATE A FIREBASE INSTANCE AND REGISTER A USER WITH EMAIL AND PASS
                   FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                      OnCompleteListener<AuthResult> { task ->
                           if (task.isSuccessful) {
                               val firebaseUser: FirebaseUser = task.result!!.user!!

                               Toast.makeText(this, "You are registered successfully", Toast.LENGTH_LONG).show()

                               val intent = Intent(this, MainActivity::class.java)
                               intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                               intent.putExtra("user_id", firebaseUser.uid)
                               intent.putExtra("email_id", email)
                               startActivity(intent)
                               finish()
                           } else {
                               Toast.makeText(this, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                           }
                       }
                   ) // FIREBASE AUTH
                } //ELSE
            } // WHEN

        } // REGISTER BUTTON ONCLICK


    }
}