package com.qdot.watery.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qdot.watery.models.ProductCarted
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: ProductCarted)

    @Query("DELETE FROM product_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM product_table")
    fun getCartedProducts(): Flow<List<ProductCarted>>

    @Query("SELECT quantity FROM product_table WHERE sku = :sku")
    fun getQuantity(sku: String): Flow<Int>

    @Query("DELETE FROM product_table WHERE sku = :sku")
    suspend fun deleteBySku(sku : String)

    @Query("SELECT EXISTS(SELECT * FROM product_table WHERE sku = :sku)")
    fun getCheckExistence(sku: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM product_table")
    fun getCount(): Flow<Int>

    @Query("UPDATE product_table SET quantity = quantity + 1 WHERE sku = :sku")
    suspend fun increaseQuantity(sku: String)

    @Query("UPDATE product_table SET quantity = quantity - 1 WHERE sku = :sku")
    suspend fun decreaseQuantity(sku: String)
}