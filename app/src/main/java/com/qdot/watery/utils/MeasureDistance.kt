package com.qdot.watery.utils

import kotlin.math.*

class MeasureDistance {

    companion object {
        fun getDistanceFromFactory(lat : Double, lon : Double) : Double {
            val rE = 6371
            val factoryLat = 24.9978
            val factoryLon = 88.1350

            val dLat = Math.toRadians(factoryLat-lat)
            val dLon = Math.toRadians(factoryLon-lon)
            val lat1 = Math.toRadians(lat)
            val lon1 = Math.toRadians(lon)

            val a : Double = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lon1)
            val c : Double = 2 * asin(sqrt(a))
            return rE * c
        }
    }

}