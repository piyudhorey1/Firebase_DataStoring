package com.example.gic_assignment.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gic_assignment.R
import com.example.gic_assignment.responseModel.Order

class OrdersAdapter(private val orders: List<Order>,
                    private val onOrderClick: (Order) -> Unit) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressTextView: TextView = itemView.findViewById(R.id.order_address)
        val contactNumberTextView: TextView = itemView.findViewById(R.id.order_contact_number)
        val deliveryDateTextView: TextView = itemView.findViewById(R.id.order_delivery_date)
        val deliveryTimeTextView: TextView = itemView.findViewById(R.id.order_delivery_time)
        val productsTextView: TextView = itemView.findViewById(R.id.order_products)
        val view: View = itemView

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClick(orders[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.addressTextView.text = order.shippingAddress
        holder.contactNumberTextView.text = order.contactNumber
        holder.deliveryDateTextView.text = order.deliveryDate
        holder.deliveryTimeTextView.text = order.deliveryTime

        val productsText = order.products.joinToString(separator = "\n") {
            "${it.name} x ${it.quantity} @ ${it.price}"
        }
        holder.productsTextView.text = productsText
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}
