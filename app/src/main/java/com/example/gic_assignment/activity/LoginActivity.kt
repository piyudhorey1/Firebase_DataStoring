package com.example.gic_assignment.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gic_assignment.GicApplication
import com.example.gic_assignment.R
import com.example.gic_assignment.databinding.ActivityLoginBinding
import com.example.gic_assignment.utils.PrefHelper
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.ButtonLogin.setOnClickListener {
            val contactNumber = binding.EditContactNumber.text.toString()
            val password = binding.EditPassword.text.toString()
            if (contactNumber.isNotEmpty() && password.isNotEmpty()) {
                checkUserCredentials(contactNumber, password)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserCredentials(contactNumber: String, password: String) {
        db.collection("customers").document(contactNumber).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val storedPassword = document.getString("password")
                    val role = document.getString("role")
                    if (storedPassword == password) {
                        if (role == "admin"){
                            Toast.makeText(this, "Admin Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            val randomToken = UUID.randomUUID().toString()
                            saveUserDetails(randomToken)
                            GicApplication.prefHelper.putString("Contact_number", contactNumber)
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User not registered", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserDetails(token : String) {

        GicApplication.prefHelper.putString(
            PrefHelper.TOKEN,
            "Bearer $token"
        )
    }

}