package com.example.schoolfinders

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.schoolfinders.models.user
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var idEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var editProfileButton: Button
    private lateinit var deleteAccountButton: Button
    private lateinit var logoutButton: Button
    private lateinit var bottomnav: BottomNavigationView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        idEditText = findViewById(R.id.idEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneNumbersEditText)
        editProfileButton = findViewById(R.id.editProfileButton)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        logoutButton = findViewById(R.id.logoutButton)
        bottomnav = findViewById(R.id.bottom_navigation)

        // Set bottom navigation bar to selected
        bottomnav.selectedItemId = R.id.profile

        // Force show icon
        bottomnav.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED

        // Set bottom navigation bar to switch activities
        bottomnav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, Homepage::class.java))
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
                    true
                }
            }
            false
        }

        // Set up the UI based on user authentication status
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Retrieve user data from Firestore
            val users = auth.currentUser
            val userUid = users?.uid
            val studentRef = userUid?.let { db.collection("users").document(it) }

            studentRef?.get()
                ?.addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null) {
                        val user = documentSnapshot.toObject(user::class.java)
                        if (user != null) {
                            nameEditText.setText(user.name)
                            surnameEditText.setText(user.surname)
                            idEditText.setText(user.id)
                            emailEditText.setText(user.email)
                            phoneEditText.setText(user.phoneNumbers)
                        }
                    }
                }
                ?.addOnFailureListener { e ->
                    Log.w(TAG, "Error getting document", e)
                }
        }

        // Handle the "Edit Profile" button click
        editProfileButton.setOnClickListener {
            // Enable the name, surname, id, and email EditText for editing
            nameEditText.isEnabled = true
            surnameEditText.isEnabled = true
            idEditText.isEnabled = true
            emailEditText.isEnabled = true
            phoneEditText.isEnabled = true
            editProfileButton.text = "Save Profile"

            // Handle the "Save Profile" button click
            editProfileButton.setOnClickListener {
                // Update the user's display name
                val currentUser = auth.currentUser
                if (currentUser != null) {


                    // Update the student data in Firestore
                    val studentId = currentUser?.uid
                    if (studentId != null) {
                        val userData = user(
                            nameEditText.text.toString(),
                            surnameEditText.text.toString(),
                            idEditText.text.toString(),
                            emailEditText.text.toString(),
                            phoneEditText.text.toString(),
                        )

                        val studentRef = db.collection("students").document(studentId)
                        studentRef.set(userData)
                            .addOnSuccessListener {
                                Log.d(TAG, "Document successfully updated!")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating document", e)
                            }
                    }

                    // Disable editing and reset the button text
                    nameEditText.isEnabled = false
                    surnameEditText.isEnabled = false
                    idEditText.isEnabled = false
                    emailEditText.isEnabled = false
                    phoneEditText.isEnabled = false
                    editProfileButton.text = "Edit Profile"
                }
            }
        }
        // Handle the "Delete Account" button click
        deleteAccountButton.setOnClickListener {
            val user = auth.currentUser

            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Delete user data from Firestore
                        db.collection("users").document(user.uid)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, Login::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error deleting user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to delete account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        // Handle the "Logout" button click
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, Login::class.java))
        }
    }
}

