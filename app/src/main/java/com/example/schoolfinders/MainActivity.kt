package com.example.schoolfinders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the buttons
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // Set an onclick listener for the login button
        btnLogin.setOnClickListener {
            // Start the login activity
            startActivity(Intent(this, Login::class.java))
        }

        // Set an onclick listener for the register button
        btnRegister.setOnClickListener {
            // Start the register activity
            startActivity(Intent(this, Register::class.java))
        }
    }
}