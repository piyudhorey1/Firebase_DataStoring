package com.example.gic_assignment.activity.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gic_assignment.R
import com.example.gic_assignment.responseModel.Product
import kotlin.reflect.KFunction1

class CartAdapter(
    private val cartProducts: MutableList<Product>,
    private val onTotalAmountUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productImage: ImageView = itemView.findViewById(R.id.imgRestImage)
        val quantityEditText: EditText = itemView.findViewById(R.id.product_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartProducts[position]
        holder.productName.text = product.name
        holder.productPrice.text = "Price: ${product.price.toString()}"
        Glide.with(holder.productImage.context)
            .load(product.imageUrl)
            .into(holder.productImage)

        holder.quantityEditText.setText(product.quantity.toString())
        holder.quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull() ?: 1
                product.quantity = quantity
                onTotalAmountUpdate()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int {
        return cartProducts.size
    }

    fun getProducts(): List<Product> {
        return cartProducts
    }

    fun clearCart() {
        cartProducts.clear()
        notifyDataSetChanged()
        onTotalAmountUpdate()
    }
}

