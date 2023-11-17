package com.example.schoolfinders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.schoolfinders.models.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etId: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etPhoneNumbers: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var txLogin: TextView

    // Auth
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        etName = findViewById(R.id.nameEditText)
        etSurname = findViewById(R.id.surnameEditText)
        etId = findViewById(R.id.idEditText)
        etConfirmPassword = findViewById(R.id.confirmPasswordEditText)
        etPhoneNumbers = findViewById(R.id.phoneNumbersEditText)
        etEmail = findViewById(R.id.emailEditText)
        etPassword = findViewById(R.id.passwordEditText)
        btnRegister = findViewById(R.id.registerButton)
        txLogin = findViewById(R.id.loginTextView)

        // Register button click listener
        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val id = etId.text.toString()
            val phoneNumbers = etPhoneNumbers.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString() // Get confirmPassword from the EditText

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(id)
                || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phoneNumbers)
                || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            } else {
                // Check if password and confirmPassword match
                if (password == confirmPassword) {
                    // Create a new user with email and password
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Create a Student instance
                                val user = user(name, surname, id, phoneNumbers, email, password, confirmPassword)

                                val currentUser = auth.currentUser

                                if (currentUser != null) {
                                    // Add the student to Firestore
                                    db.collection("users").document(currentUser.uid)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show()
                                            startActivity(Intent(this, Login::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Failed to register", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Failed to register", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this, "Failed to register", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Login textview click listener
        txLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}
