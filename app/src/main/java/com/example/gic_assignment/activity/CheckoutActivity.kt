package com.example.gic_assignment.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gic_assignment.GicApplication
import com.example.gic_assignment.databinding.ActivityCheckoutBinding
import com.example.gic_assignment.responseModel.Order
import com.example.gic_assignment.responseModel.Product
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity() : AppCompatActivity() {

    lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GicApplication.prefHelper.getString("Contact_number")?.let { fetchDefaultUserDetails(it) }

        binding.submitOrderBtn.setOnClickListener {
            handleOrderSubmission()
        }
    }
    private fun fetchDefaultUserDetails(contactNumber: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("customers").document(contactNumber)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val defaultAddress = document.getString("address") ?: ""
                    val defaultContact = document.getString("contactNumber") ?: ""

                    binding.shippingAddress.setText(defaultAddress)
                    binding.contactNumber.setText(defaultContact)
                } else {
                    Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleOrderSubmission() {
        val address = binding.shippingAddress.text.toString()
        val contactNumber = binding.contactNumber.text.toString()
        val deliveryDate = binding.deliveryDate.text.toString()
        val deliveryTime = binding.deliveryTime.text.toString()

        if (address.isEmpty() || contactNumber.isEmpty() || deliveryDate.isEmpty() || deliveryTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }


        val cartProducts = intent.getParcelableArrayListExtra<Product>("CART_PRODUCTS") ?: arrayListOf()
        val order = Order(
            shippingAddress = address,
            contactNumber = contactNumber,
            deliveryDate = deliveryDate,
            deliveryTime = deliveryTime,
            products = cartProducts
        )

        sendOrderToAdmin(order)
    }

    private fun sendOrderToAdmin(order: Order) {
        val firestore = FirebaseFirestore.getInstance()
        val ordersCollection = firestore.collection("orders")

        ordersCollection.add(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Order submitted successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}