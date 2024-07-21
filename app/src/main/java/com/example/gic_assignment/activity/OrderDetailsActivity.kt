package com.example.gic_assignment.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gic_assignment.R
import com.example.gic_assignment.databinding.ActivityOrderDetailsBinding
import com.example.gic_assignment.responseModel.Order
import com.example.gic_assignment.utils.FcmNotification
import com.example.gic_assignment.utils.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var order: Order
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        order = intent.getParcelableExtra("order") ?: return

        displayOrderDetails()

        binding.acceptOrderButton.setOnClickListener {
            acceptOrder()
        }
        binding.rejectOrderButton.setOnClickListener {
            rejectOrder()
        }

    }

    private fun displayOrderDetails() {
        binding.customerName.text = "Customer: ${order.customerName}"
        binding.shippingAddress.text = "Address: ${order.shippingAddress}"
        binding.contactNumber.text = "Contact: ${order.contactNumber}"
        binding.deliveryDateTime.text = "Delivery: ${order.deliveryDate} ${order.deliveryTime}"

        val productsText = order.products.joinToString(separator = "\n") {
            "${it.name} x ${it.quantity} @ ${it.price}"
        }
        binding.productsList.text = productsText

        binding.rejectionReason.visibility = View.GONE
    }

    private fun acceptOrder() {
        sendNotification("Order accepted", order.contactNumber)
        finish()
    }

    private fun rejectOrder() {
        binding.rejectionReason.visibility = View.VISIBLE
        val reason = binding.rejectionReason.text.toString()
        if (reason.isBlank()) {
            Toast.makeText(this, "Please provide a reason for rejection", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("orders").document(order.orderId)
            .delete()
            .addOnSuccessListener {
                sendNotification("Order rejected. Reason: $reason", order.contactNumber)
                Toast.makeText(this, "Order rejected and deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendNotification(message: String, contactNumber: String) {
        val notification = FcmNotification(
            to = "/topics/$contactNumber",
            data = mapOf("message" to message)
        )

        RetrofitClient.fcmService.sendNotification(notification).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Notification sent successfully")
                } else {
                    println("Failed to send notification: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }



    companion object {
        private const val TAG = "OrderDetailsActivity"
    }
}
