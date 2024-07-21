package com.example.gic_assignment.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gic_assignment.GicApplication
import com.example.gic_assignment.R
import com.example.gic_assignment.activity.adapter.OrdersAdapter
import com.example.gic_assignment.databinding.ActivityAdminBinding
import com.example.gic_assignment.fragment.AddProductFragment
import com.example.gic_assignment.responseModel.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class AdminActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminBinding
    private lateinit var ordersAdapter: OrdersAdapter
    private var ordersList: MutableList<Order> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.RcyOrders.layoutManager = LinearLayoutManager(this)
        ordersAdapter = OrdersAdapter(ordersList) { order ->
            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("order", order)
            startActivity(intent)
        }
        binding.RcyOrders.adapter = ordersAdapter

        fetchOrders()

        binding.ImageAddProduct.setOnClickListener {
            addNewFragment(AddProductFragment.newInstance(), "add_product", Bundle())
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                println("Back button pressed")
                // Code that you need to execute on back press, e.g. finish()
                GicApplication.prefHelper.clear()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchOrders()
    }

    private fun fetchOrders() {
        val firestore = FirebaseFirestore.getInstance()
        val ordersCollection = firestore.collection("orders")

        ordersCollection.get()
            .addOnSuccessListener { documents ->
                ordersList.clear()
                for (document in documents) {
                    val order = document.toObject(Order::class.java).apply {
                        orderId = document.id
                    }
                    ordersList.add(order)
                }
                ordersAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch orders: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun addNewFragment(fragment: Fragment?, tag:String, bundle: Bundle) {
        fragment?.arguments = bundle
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutMainTab, fragment!!,tag).addToBackStack(null).commit()
    }
}