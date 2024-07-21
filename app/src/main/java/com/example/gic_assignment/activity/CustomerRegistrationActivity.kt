package com.example.gic_assignment.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.gic_assignment.R
import com.example.gic_assignment.databinding.ActivityCustomerRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class CustomerRegistrationActivity : AppCompatActivity() {

    lateinit var binding : ActivityCustomerRegistrationBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var imageUri: Uri
    private val maxImageSize = 5 * 1024 * 1024
    private lateinit var auth: FirebaseAuth

    private val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getContent()
            }
            else {
                Toast.makeText(this, "Permission denied. Cannot access external storage.", Toast.LENGTH_SHORT).show()
            }
        }

    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it == null) {
            onResume()
        }
        else if (isImageSizeWithinLimit(it)){
            imageUri = it
            binding.EditImageName.setText(imageUri.path)
            Toast.makeText(this, "Image upload successful", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Image size exceeds the limit of 5 MB", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.ButtonUpload.setOnClickListener {
            startImagePicker()
        }

        binding.TextGoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        binding.ButtonRegister.setOnClickListener {
//            val customerName = binding.EditCustName.text.toString().trim()
//            val contactNumber = binding.EditContactNumber.text.toString().trim()
//            val email = binding.EditEmailId.text.toString().trim()
//            val address = binding.EditAddress.text.toString().trim()
//            val city = binding.EditCity.text.toString().trim()
//            val pinCode = binding.EditPinCode.text.toString().trim()
//            val password = binding.EditPassword.text.toString().trim()
//
//            if (customerName.isEmpty() || contactNumber.isEmpty() || email.isEmpty() || address.isEmpty() ||
//                city.isEmpty() || pinCode.isEmpty() || password.isEmpty()
//            ) {
//                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val intent = Intent(this, OtpVerificationActivity::class.java)
//            intent.putExtra("customerName", customerName)
//            intent.putExtra("contactNumber", contactNumber)
//            intent.putExtra("email", email)
//            intent.putExtra("address", address)
//            intent.putExtra("city", city)
//            intent.putExtra("pinCode", pinCode)
//            intent.putExtra("password", password)
//            intent.putExtra("addressProofUri", imageUri.toString())
//            startActivity(intent)
//        }

        binding.ButtonRegister.setOnClickListener {
            val customerName = binding.EditCustName.text.toString().trim()
            val contactNumber = binding.EditContactNumber.text.toString().trim()
            val email = binding.EditEmailId.text.toString().trim()
            val address = binding.EditAddress.text.toString().trim()
            val city = binding.EditCity.text.toString().trim()
            val pinCode = binding.EditPinCode.text.toString().trim()
            val password = binding.EditPassword.text.toString().trim()

            if (customerName.isEmpty() || contactNumber.isEmpty() || email.isEmpty() || address.isEmpty() ||
                city.isEmpty() || pinCode.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val text = visionText.text
                        if (text.contains(city) && text.contains(pinCode)) {
                            val intent = Intent(this, OtpVerificationActivity::class.java)
                            intent.putExtra("customerName", customerName)
                            intent.putExtra("contactNumber", contactNumber)
                            intent.putExtra("email", email)
                            intent.putExtra("address", address)
                            intent.putExtra("city", city)
                            intent.putExtra("pinCode", pinCode)
                            intent.putExtra("password", password)
                            intent.putExtra("addressProofUri", imageUri.toString())
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "City and pin code do not match the selected image", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to recognize text from image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isImageSizeWithinLimit(uri: Uri): Boolean {
        val inputStream = this.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val sizeInBytes = stream.available()
            return sizeInBytes <= maxImageSize
        }
        return false
    }

    private fun getContent() {
        contract.launch("image/*")
    }
    private fun startImagePicker() {
        if (ContextCompat.checkSelfPermission(this, readImagePermission) == PackageManager.PERMISSION_GRANTED) {
            getContent()
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        storagePermissionLauncher.launch(readImagePermission)
    }

}