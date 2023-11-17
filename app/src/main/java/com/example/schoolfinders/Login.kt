package com.example.schoolfinders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.schoolfinders.models.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize UI elements
        emailEditText = findViewById(R.id.loginEmail)
        passwordEditText = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        registerTextView = findViewById(R.id.registerTextView)

        // Set up login button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Authenticate the user with Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Authentication successful
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                // Retrieve user data from Firestore
                                db.collection("users").document(currentUser.uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            // User data found, you can access it using document.toObject(user::class.java)
                                            val user = document.toObject(user::class.java)
                                            // Handle successful login and navigate to the main activity
                                            startActivity(Intent(this, Homepage::class.java))
                                            finish()
                                        } else {
                                            // User data not found
                                            Toast.makeText(this, "User data not found", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle Firestore retrieval failure
                                        Toast.makeText(this,"Failed to retrieve user data: $e", Toast.LENGTH_LONG).show()

                                    }
                            }
                        } else {
                            // Authentication failed
                            Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_LONG).show()
            }


        }

        // Set up register text view
        registerTextView.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}