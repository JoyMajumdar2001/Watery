package com.qdot.watery.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "product_table")
class ProductCarted(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val productName: String,
    @ColumnInfo(name = "desc") val description: String,
    @ColumnInfo(name = "sku") val sku: String,
    @ColumnInfo(name = "imageUrl") val imageUrl: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "cartTime") val time: Long,
    @ColumnInfo(name = "quantity") val quantity: Int,
)
