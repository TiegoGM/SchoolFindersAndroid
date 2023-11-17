package com.example.schoolfinders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.schoolfinders.models.university

class University : AppCompatActivity() {
    private lateinit var universityspinner1: Spinner
    private lateinit var coursespinner1: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var bottomnav: BottomNavigationView

    // Firestore
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_university)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize the spinners
        universityspinner1 = findViewById(R.id.universityspinner1)
        coursespinner1 = findViewById(R.id.courseSpinner1)


        // Set up the adapters for the spinners
        val universityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.university_array,
            android.R.layout.simple_spinner_item
        )
        universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        universityspinner1.adapter = universityAdapter

        val courseAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.courses_array,
            android.R.layout.simple_spinner_item
        )
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        coursespinner1.adapter = courseAdapter

        // Initialize the bottom navigation bar
        bottomnav = findViewById(R.id.bottom_navigation)

        // Set the bottom navigation bar to the correct page
        bottomnav.selectedItemId = R.id.university

        // Force the bottom navigation bar to always show titles
        bottomnav.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED

        // Set the bottom navigation bar to switch activities
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

        // Initialize the submit button
        btnSubmit = findViewById(R.id.btnSubmit)

        // Set the button to submit the application
        btnSubmit.setOnClickListener {
            val universityCourse = universityspinner1.selectedItem.toString()
            val course = coursespinner1.selectedItem.toString()

            val user = auth.currentUser

            if (user != null) {
                if (universityCourse.isNotEmpty() && course.isNotEmpty()) {
                    // Create a University object
                    val universityData = university(
                        universityCourse,
                        course
                    )

                    // Get the user's UID
                    val userUid = user.uid

                    // Reference to the university data for this user
                    val universityRef = db.collection("users").document(userUid)
                        .collection("university")

                    // Add the university data to Firestore
                    universityRef.add(universityData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "University data added successfully", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to add university data", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Please select university and course for both options", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}
