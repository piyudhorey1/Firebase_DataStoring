package com.example.gic_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.gic_assignment.activity.CustomerRegistrationActivity
import com.example.gic_assignment.activity.HomeActivity
import com.example.gic_assignment.utils.PrefHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper())
            .postDelayed({
                checkNextScreenAndOpen()

            }, 2000)

    }

    private fun checkNextScreenAndOpen(){
        if (GicApplication.prefHelper.getString(PrefHelper.TOKEN).equals("")) {
            openCustomerRegistration()
        } else {
            openHomeScreen()
        }
    }

    private fun openCustomerRegistration() {
        val intent = Intent(this, CustomerRegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}