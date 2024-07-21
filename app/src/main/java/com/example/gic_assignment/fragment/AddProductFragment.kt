package com.example.gic_assignment.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.gic_assignment.databinding.FragmentAddProductBinding
import com.example.gic_assignment.responseModel.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddProductFragment : Fragment() {

    lateinit var binding: FragmentAddProductBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null
    private val maxImageSize = 10 * 1024 * 1024

    private val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getContent()
            } else {
                Toast.makeText(context, "Permission denied. Cannot access external storage.", Toast.LENGTH_SHORT).show()
            }
        }

    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it == null) {
            onResume()
        } else if (isImageSizeWithinLimit(it)) {
            imageUri = it
            binding.ImageProduct.setImageURI(it)
            Toast.makeText(context, "Image upload successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Image size exceeds the limit of 10 MB", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(layoutInflater, container, false)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.ButtonAddImage.setOnClickListener {
            startImagePicker()
        }

        binding.ButtonAdd.setOnClickListener {
            val productName = binding.EditProductName.text.toString()
            val productDescription = binding.EditDescription.text.toString()
            val productPrice = binding.EditRate.text.toString().toDoubleOrNull()
            val productImage = imageUri

            if (productName.isNotEmpty() && productDescription.isNotEmpty() && productPrice != null && productImage != null) {
                uploadImageAndAddProduct(productName, productDescription, productPrice, productImage)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun isImageSizeWithinLimit(uri: Uri): Boolean {
        val inputStream = context?.contentResolver?.openInputStream(uri)
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
        if (ContextCompat.checkSelfPermission(requireContext(), readImagePermission) == PackageManager.PERMISSION_GRANTED) {
            getContent()
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        storagePermissionLauncher.launch(readImagePermission)
    }

    private fun uploadImageAndAddProduct(name: String, description: String, price: Double, imageUri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("product_images/$fileName")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    addProductToFirestore(name, description, price, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addProductToFirestore(name: String, description: String, price: Double, imageUrl: String) {
        val product = Product(name, description, price, imageUrl)
        db.collection("products")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show()

                binding.EditProductName.text.clear()
                binding.EditDescription.text.clear()
                binding.EditRate.text.clear()
                binding.ImageProduct.setImageResource(0)
                imageUri = null
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {

        fun newInstance() =
            AddProductFragment().apply {
            }
    }
}
