package com.tnrlab.scenetextcollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val RegisterTv = findViewById<TextView>(R.id.RegisterTv)

        RegisterTv.setOnClickListener{
            startActivity(Intent(this, RegisterPage::class.java))
        }

        val LoginConfirmBtn = findViewById<Button>(R.id.LoginConfirmBtn)
        val LoginEmailEt = findViewById<EditText>(R.id.LoginEmailEt)
        val LoginPasswordEt = findViewById<EditText>(R.id.LoginPasswordEt)

        LoginConfirmBtn.setOnClickListener{
            when {
                TextUtils.isEmpty(LoginEmailEt.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Please Enter Email", Toast.LENGTH_LONG).show()
                }

                TextUtils.isEmpty(LoginPasswordEt.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Please Enter Password", Toast.LENGTH_LONG).show()
                }
                else -> {
                    val email: String = LoginEmailEt.text.toString().trim { it <= ' ' }
                    val password: String = LoginPasswordEt.text.toString().trim { it <= ' ' }

                    // CREATE A FIREBASE INSTANCE AND REGISTER A USER WITH EMAIL AND PASS
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(
                        OnCompleteListener<AuthResult> { task ->
                            if (task.isSuccessful) {

                                Toast.makeText(this, "You are logged in successfully", Toast.LENGTH_LONG).show()

                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    ) // FIREBASE AUTH
                } //ELSE
            } // WHEN

        } // REGISTER BUTTON ONCLICK

    }
}