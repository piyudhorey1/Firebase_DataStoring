package com.example.gic_assignment.responseModel

import android.os.Parcel
import android.os.Parcelable

data class Order(
    var orderId: String = "",
    val customerName: String = "",
    val shippingAddress: String = "",
    val contactNumber: String = "",
    val deliveryDate: String = "",
    val deliveryTime: String = "",
    val products: List<Product> = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Product.CREATOR) ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(customerName)
        parcel.writeString(shippingAddress)
        parcel.writeString(contactNumber)
        parcel.writeString(deliveryDate)
        parcel.writeString(deliveryTime)
        parcel.writeTypedList(products)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}

