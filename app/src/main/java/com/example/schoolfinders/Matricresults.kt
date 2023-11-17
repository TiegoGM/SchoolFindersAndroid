package com.example.schoolfinders

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolfinders.models.MatricResults
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Matricresults : AppCompatActivity() {

    private lateinit var idImageView: ImageView
    private lateinit var matricImageView: ImageView
    private lateinit var btnIdCamera: Button
    private lateinit var btnIdGallery: Button
    private lateinit var btnMatricCamera: Button
    private lateinit var btnMatricGallery: Button
    private lateinit var btnSubmit: Button
    private lateinit var bottomnav: BottomNavigationView
    private lateinit var idImageUri: Uri
    private lateinit var matricImageUri: Uri

    // Firestore
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var imageRef: StorageReference

    private val REQUEST_CAMERA_ID = 1
    private val REQUEST_GALLERY_ID = 2
    private val REQUEST_CAMERA_MATRIC = 3
    private val REQUEST_GALLERY_MATRIC = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matricresults)

        // Firestore
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        imageRef = storage.reference

        idImageView = findViewById(R.id.idImageView)
        matricImageView = findViewById(R.id.matricImageView)
        btnIdCamera = findViewById(R.id.btnIdCamera)
        btnIdGallery = findViewById(R.id.btnIdGallery)
        btnMatricCamera = findViewById(R.id.btnMatricCamera)
        btnMatricGallery = findViewById(R.id.btnMatricGallery)
        btnSubmit = findViewById(R.id.btnSubmit)
        bottomnav = findViewById(R.id.bottom_navigation)

        // Set the bottom navigation bar to the correct page
        bottomnav.selectedItemId = R.id.results

        // Force the bottom navigation bar to always show the title
        bottomnav.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED

        // Set the bottom navigation bar to the correct page
        bottomnav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, Homepage::class.java))
                    true
                }
                R.id.results -> {
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
                else -> false
            }
        }

        // Set up camera button for ID image
        btnIdCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CAMERA_ID)
        }

        // Set up gallery button for ID image
        btnIdGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_GALLERY_ID)
        }

        // Set up camera button for Matric image
        btnMatricCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CAMERA_MATRIC)
        }

        // Set up gallery button for Matric image
        btnMatricGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_GALLERY_MATRIC)
        }

        // Set up submit button
        btnSubmit.setOnClickListener {
            // Check if both ID and Matric images are selected
            if (idImageUri != null && matricImageUri != null) {
                // Call the function to upload both images
                uploadImageToFirebaseStorage(idImageUri, matricImageUri)

                // Rest of your code here
                val matricResults = MatricResults(
                    idImageUri.toString(),
                    matricImageUri.toString()
                )

                val user = auth.currentUser
                if (user != null) {
                    // Reference to the user's document in the "students" collection
                    val userUid = user.uid
                    val studentDocRef = db.collection("users").document(userUid)

                    // Reference to the "matricresults" subcollection inside the student's document
                    val resultCollectionRef = studentDocRef.collection("matricresults")

                    // Add matric results to the "matricresults" subcollection under the student's document
                    resultCollectionRef.add(matricResults)
                        .addOnSuccessListener {
                            // Calculate and update total points
                            Toast.makeText(this, "Document added successfully", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add Document", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please select both ID and Matric images", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA_ID -> {
                    val image = data?.extras?.get("data") as Bitmap?
                    if (image != null) {
                        idImageView.setImageBitmap(image)
                        idImageUri = getImageUri(image)
                    } else {
                        Toast.makeText(this, "Failed to capture ID image", Toast.LENGTH_SHORT).show()
                    }
                }

                REQUEST_GALLERY_ID -> {
                    data?.data?.let { uri ->
                        idImageUri = uri
                        idImageView.setImageURI(idImageUri)
                    } ?: run {
                        Toast.makeText(this, "Failed to select ID image from gallery", Toast.LENGTH_SHORT).show()
                    }
                }

                REQUEST_CAMERA_MATRIC -> {
                    val image = data?.extras?.get("data") as Bitmap?
                    if (image != null) {
                        matricImageView.setImageBitmap(image)
                        matricImageUri = getImageUri(image)
                    } else {
                        Toast.makeText(this, "Failed to capture Matric image", Toast.LENGTH_SHORT).show()
                    }
                }

                REQUEST_GALLERY_MATRIC -> {
                    data?.data?.let { uri ->
                        matricImageUri = uri
                        matricImageView.setImageURI(matricImageUri)
                    } ?: run {
                        Toast.makeText(this, "Failed to select Matric image from gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Function to upload ID and Matric images to Firebase Storage
    // Function to upload ID and Matric images to Firebase Storage
    private fun uploadImageToFirebaseStorage(idImageUri: Uri, matricImageUri: Uri) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Upload ID image
            val idImageFilename = "id_image.jpg"
            val idImageRef = storage.reference.child("images/${currentUser.uid}/$idImageFilename")
            idImageRef.putFile(idImageUri)
                .addOnSuccessListener {
                    // ID image uploaded successfully
                    // Now, upload the Matric image
                    val matricImageFilename = "matric_image.jpg"
                    val matricImageRef = storage.reference.child("images/${currentUser.uid}/$matricImageFilename")
                    matricImageRef.putFile(matricImageUri)
                        .addOnSuccessListener {
                            // Matric image uploaded successfully
                            // You can add further actions here
                            Toast.makeText(this, "ID and Matric images uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { matricException ->
                            // Handle any errors that occur during Matric image upload
                            Toast.makeText(this, "Failed to upload Matric image: ${matricException.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { idException ->
                    // Handle any errors that occur during ID image upload
                    Toast.makeText(this, "Failed to upload ID image: ${idException.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getImageUri(inImage: Bitmap): Uri {
        val outImage = Bitmap.createScaledBitmap(inImage, 800, 800, true)
        val path = MediaStore.Images.Media.insertImage(contentResolver, outImage, "Title", null)
        return Uri.parse(path)
    }
}
