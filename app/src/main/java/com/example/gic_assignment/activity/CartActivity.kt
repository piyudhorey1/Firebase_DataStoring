package com.example.gic_assignment.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gic_assignment.activity.adapter.CartAdapter
import com.example.gic_assignment.databinding.ActivityCartBinding
import com.example.gic_assignment.responseModel.Product

class CartActivity : AppCompatActivity() {

    lateinit var binding: ActivityCartBinding
    lateinit var cartAdapter: CartAdapter
    private val checkoutRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val cartProducts = intent.getParcelableArrayListExtra<Product>("CART_PRODUCTS") ?: arrayListOf()

        cartAdapter = CartAdapter(cartProducts, ::updateTotalAmount)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRecyclerView.adapter = cartAdapter

        updateTotalAmount()

        binding.checkoutButton.setOnClickListener {
            if (cartAdapter.itemCount > 0) {
                binding.TextEmptyCart.visibility = View.GONE
                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putParcelableArrayListExtra("CART_PRODUCTS", ArrayList(cartAdapter.getProducts()))
                }
                startActivityForResult(intent, checkoutRequestCode)
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotalAmount() {
        val products = cartAdapter.getProducts()
        val totalAmount = products.sumOf { it.price * it.quantity }
        binding.TextTotalAmount.text = "Total: $${String.format("%.2f", totalAmount)}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == checkoutRequestCode && resultCode == RESULT_OK) {
            cartAdapter.clearCart()
            binding.TextEmptyCart.visibility = View.VISIBLE
            Toast.makeText(this, "Cart cleared after successful checkout", Toast.LENGTH_SHORT).show()
        }
    }

}