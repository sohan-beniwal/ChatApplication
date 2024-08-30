package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var LoginButton: Button
    private lateinit var Email: EditText
    private lateinit var Name: EditText
    private lateinit var Password: EditText
    private lateinit var SignUpButton:Button
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        LoginButton=findViewById(R.id.login_button_signup)
        Email=findViewById(R.id.signup_email)
        Name=findViewById(R.id.signup_name)
        Password=findViewById(R.id.signup_password)
        SignUpButton=findViewById(R.id.signup_button)
        mAuth=FirebaseAuth.getInstance()

        LoginButton.setOnClickListener {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

        SignUpButton.setOnClickListener {
            val name = Name.text.toString()
            val email= Email.text.toString()
            val password = Password.text.toString()

            FirebaseSignUp(name,email,password)
        }
    }
    private fun FirebaseSignUp(name:String,email:String,password:String){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){
            if(it.isSuccessful){
                AddUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                val intent= Intent(this@SignUp,MainActivity::class.java)
                finish()
                startActivity(intent)
            }
            else{
                Toast.makeText(this@SignUp,it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun AddUserToDatabase(name : String,email : String , uid : String){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("User").child(uid).setValue(User(name,email,uid))

    }
}