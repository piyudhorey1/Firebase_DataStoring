package com.example.gic_assignment.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gic_assignment.R
import com.example.gic_assignment.activity.HomeActivity
import com.example.gic_assignment.responseModel.Product

class ProductAdapter(
    val productList: List<Product>,
    private val onProductClick: (Int) -> Unit,
    private val onAddToCartClick: (Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productDescription: TextView = itemView.findViewById(R.id.product_description)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val addToCartButton: Button = itemView.findViewById(R.id.add_to_cart_button)

        init {
            itemView.setOnClickListener {
                onProductClick(adapterPosition)
            }
            addToCartButton.setOnClickListener {
                onAddToCartClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = "Price: $ ${product.price.toString()}"
        Glide.with(holder.productImage.context)
            .load(product.imageUrl)
            .into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

