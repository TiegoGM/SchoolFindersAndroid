package com.example.schoolfinders

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Homepage : AppCompatActivity() {
    private lateinit var btnNewApplication: Button
    private lateinit var btnViewApplication: Button
    private lateinit var bottomnav: BottomNavigationView
    private lateinit var userName: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize UI elements
        btnNewApplication = findViewById(R.id.newApplicationButton)
        btnViewApplication = findViewById(R.id.viewApplicationsButton)
        bottomnav = findViewById(R.id.bottom_navigation)
        userName = findViewById(R.id.userName)

        // Set the bottom navigation bar to the correct page
        bottomnav.selectedItemId = R.id.home

        // Force the bottom navigation bar to always show titles
        bottomnav.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED

        // Retrieve the username from Firestore and update the TextView
        val users = auth.currentUser
        val userUid = users?.uid
        if (userUid != null) {
            val studentRef = db.collection("users").document(userUid)

            studentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val username = documentSnapshot.getString("name")
                        userName.text = "Username: $username"  // Set the retrieved username to the TextView
                    } else {
                        userName.text = "Username: Not Found"  // Handle the case when the document doesn't exist
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error (you can log the error message)
                    userName.text = "Username: Not Found"

                }
        }

        // Set the bottom navigation bar to the correct page
        bottomnav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    true
                }
                R.id.results -> {
                    startActivity(Intent(this, Matricresults::class.java))
                    true
                }
                R.id.university -> {
                    startActivity(Intent(this, University::class.java))
                    true
                }
                R.id.status -> {
                    startActivity(Intent(this, Status::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
            }
            false
        }

        // Set the button to go to the new application page
        btnNewApplication.setOnClickListener {
            startActivity(Intent(this, Matricresults::class.java))
        }

        // Set the button to go to the view applications page
        btnViewApplication.setOnClickListener {
            // Start the view applications activity
            startActivity(Intent(this, Status::class.java))
        }
    }
}
