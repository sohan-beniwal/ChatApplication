package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var Email:EditText
    private lateinit var Password:EditText
    private lateinit var LoginButton:Button
    private lateinit var SignUpButton:Button
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        Email=findViewById(R.id.login_email)
        Password=findViewById(R.id.login_password)
        LoginButton=findViewById(R.id.login_button)
        SignUpButton=findViewById(R.id.signup_button_login)
        mAuth=FirebaseAuth.getInstance()

        SignUpButton.setOnClickListener{
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        LoginButton.setOnClickListener {
            val email = Email.text.toString()
            val password = Password.text.toString()

            FirebaseLogin(email,password)
        }

    }
    private fun FirebaseLogin(email:String,password:String)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            if(it.isSuccessful){
                val intent= Intent(this@Login,MainActivity::class.java)
                finish()
                startActivity(intent)
            }
            else{
                Toast.makeText(this@Login,it.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}