package com.example.schoolfinders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolfinders.adpaters.UniversityAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.schoolfinders.models.university
import com.google.android.material.bottomnavigation.BottomNavigationView

class Status : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UniversityAdapter
    private lateinit var bottomnav: BottomNavigationView

    // Firestore
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        // Firestore
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UniversityAdapter()
        recyclerView.adapter = adapter

        // Load and display applied universities
        loadAppliedUniversities()

        // Bottom Navigation
        bottomnav = findViewById(R.id.bottom_navigation)

        // Set Home as selected
        bottomnav.selectedItemId = R.id.status

        //force to show icon and title in bottom navigation
        bottomnav.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED

        //set item click listener for bottom navigation
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
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
            }
            false
        }
    }

    private fun loadAppliedUniversities() {
        val user = auth.currentUser
        val userUid = user?.uid

        if (userUid != null) {
            // Reference to the user's document in the "students" collection
            val studentDocRef = db.collection("users").document(userUid)

            // Reference to the "university" sub-collection inside the student's document
            val universityCollectionRef = studentDocRef.collection("university")

            universityCollectionRef.get()
                .addOnSuccessListener { result ->
                    val universities = mutableListOf<university>()
                    for (document in result) {
                        val data = document.data
                        val universityCourse = data["universityCourse"] as String
                        val course = data["course"] as String
                        val status = data["status"] as String
                        val university = university(universityCourse, course, status)
                        universities.add(university)
                    }
                    adapter.setUniversities(universities)
                }
                .addOnFailureListener { exception ->
                    // Handle errors here
                }
        }
    }
}