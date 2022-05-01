package com.qdot.watery.room

import androidx.annotation.WorkerThread
import com.qdot.watery.models.ProductCarted
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val allCartedProducts: Flow<List<ProductCarted>> = productDao.getCartedProducts()

    val productsCount = productDao.getCount()

    fun getExistence(sku: String): Flow<Boolean> {
        return productDao.getCheckExistence(sku)
    }

    fun getQuantity(sku: String): Flow<Int> {
        return productDao.getQuantity(sku)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(productCarted: ProductCarted) {
        productDao.insert(productCarted)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        productDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteBySku(sku:String) {
        productDao.deleteBySku(sku)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun increaseQuantity(sku: String) {
        productDao.increaseQuantity(sku)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun decreaseQuantity(sku: String) {
        productDao.decreaseQuantity(sku)
    }

}