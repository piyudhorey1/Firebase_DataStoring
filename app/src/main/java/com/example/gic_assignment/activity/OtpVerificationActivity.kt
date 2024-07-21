package com.example.gic_assignment.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gic_assignment.GicApplication
import com.example.gic_assignment.databinding.ActivityOtpVerificationBinding
import com.example.gic_assignment.responseModel.Customer
import com.example.gic_assignment.utils.PrefHelper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpVerificationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    private var verificationId: String? = null
    private var customerName: String? = null
    private var contactNumber: String? = null
    private var email: String? = null
    private var address: String? = null
    private var city: String? = null
    private var pinCode: String? = null
    private var password: String? = null
    private var addressProofUri: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()

        customerName = intent.getStringExtra("customerName")
        contactNumber = intent.getStringExtra("contactNumber")
        email = intent.getStringExtra("email")
        address = intent.getStringExtra("address")
        city = intent.getStringExtra("city")
        pinCode = intent.getStringExtra("pinCode")
        password = intent.getStringExtra("password")
        addressProofUri = intent.getStringExtra("addressProofUri")

        sendOtp(contactNumber ?: "")

        binding.ButtonVerifyOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString()
            verifyOtp(verificationId!!, otp)
        }

    }
    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OtpVerificationActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@OtpVerificationActivity.verificationId = verificationId
                    Toast.makeText(this@OtpVerificationActivity, "OTP sent", Toast.LENGTH_SHORT).show()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show()
                    // Redirect to home screen or continue with registration
                    val user = auth.currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { idTokenTask ->
                        if (idTokenTask.isSuccessful) {
                            var idToken = idTokenTask.result?.token
                            println("On Auth User: id token: $idToken")

                            saveUserDetails(idToken!!)
                            saveUserToFirestore()
                        }
                    }
                } else {
                    Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verifyOtp(verificationId: String, otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun saveUserDetails(token : String) {

        GicApplication.prefHelper.putString(
            PrefHelper.TOKEN,
            "Bearer $token"
        )
    }

    private fun saveUserToFirestore() {

        val user = Customer(
            customerName = customerName,
            contactNumber = contactNumber,
            email = email,
            address = address,
            city = city,
            pinCode = pinCode,
            password = password,
            addressProofUri = addressProofUri
        )

        db.collection("customers")
            .document(contactNumber!!)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User details saved to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save the data {${it.message}}", Toast.LENGTH_SHORT).show()
            }
    }
}