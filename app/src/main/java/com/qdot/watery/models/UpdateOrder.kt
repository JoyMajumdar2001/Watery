package com.qdot.watery.models

data class UpdateOrder(
    val status : Int,
    val deliveryPartner : String,
    val currentTime : Long
)
