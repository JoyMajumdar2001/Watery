package com.qdot.watery.models

import android.os.Parcelable
import java.io.Serializable

data class Order(
    val orderId: String,
    val buyerName : String,
    val buyerEmail : String,
    val buyerPhone : String,
    val buyerId : String,
    val orderPlacedTime : Long,
    val orderAddress : String,
    val orderLat: Double,
    val orderLon : Double,
    val price : Double,
    val tax : Double,
    val deliveryFee : Double,
    val status : Int,
    val deliveryPartner : String,
    val paymentType : Int,
    val paymentId : String,
    val orderItems : String,
    val currentTime : Long
) : Serializable
