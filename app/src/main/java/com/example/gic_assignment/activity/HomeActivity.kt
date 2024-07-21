package com.example.gic_assignment.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gic_assignment.GicApplication
import com.example.gic_assignment.R
import com.example.gic_assignment.activity.adapter.ProductAdapter
import com.example.gic_assignment.databinding.ActivityHomeBinding
import com.example.gic_assignment.responseModel.Product
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var productAdapter: ProductAdapter
    lateinit var  db: FirebaseFirestore
    private var lastClickedPosition: Int = -1
    private val cart = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.IconLogout.setOnClickListener {
            GicApplication.prefHelper.clear()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        fetchProducts()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                println("Back button pressed")
                // Code that you need to execute on back press, e.g. finish()
                finish()
            }
        })

    }


    private fun setupRecyclerView(productList: List<Product>) {
        productAdapter = ProductAdapter(productList, ::onProductClick, ::onAddToCartClick)
        binding.RcyProducts.layoutManager = LinearLayoutManager(this)
        binding.RcyProducts.adapter = productAdapter
    }

    fun onProductClick(position: Int) {
        val productViewHolder = binding.RcyProducts.findViewHolderForAdapterPosition(position) as? ProductAdapter.ProductViewHolder
        productViewHolder?.let {
            val isVisible = it.addToCartButton.visibility == View.VISIBLE
            it.addToCartButton.visibility = if (isVisible) View.GONE else View.VISIBLE
            lastClickedPosition = if (isVisible) -1 else position
        }
    }

    private fun onAddToCartClick(position: Int) {
        val product = productAdapter.productList[position]
        cart.add(product)
        val intent = Intent(this, CartActivity::class.java).apply {
            putParcelableArrayListExtra("CART_PRODUCTS", ArrayList(cart))
        }
        startActivity(intent)
    }

    private fun fetchProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val productList = mutableListOf<Product>()
                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                setupRecyclerView(productList)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching products: ${e.message}")
            }
    }

}