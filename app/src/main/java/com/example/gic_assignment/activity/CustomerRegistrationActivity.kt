package com.example.gic_assignment.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gic_assignment.R
import com.example.gic_assignment.databinding.ActivityCustomerRegistrationBinding

class CustomerRegistrationActivity : AppCompatActivity() {

    lateinit var binding : ActivityCustomerRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TextGoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}