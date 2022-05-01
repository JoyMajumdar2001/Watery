package com.qdot.watery.room

import android.app.Application
import androidx.lifecycle.*
import com.qdot.watery.models.ProductCarted
import kotlinx.coroutines.launch

class CartedProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : ProductRepository

    init {
        val productDao = ProductDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }

    val allProducts: LiveData<List<ProductCarted>> = repository.allCartedProducts.asLiveData()

    val productCount = repository.productsCount.asLiveData()

    fun getExistence(sku: String): LiveData<Boolean> {
      return repository.getExistence(sku).asLiveData()
    }

    fun getQuantity(sku: String): LiveData<Int> {
        return repository.getQuantity(sku).asLiveData()
    }

    fun insert(productCarted: ProductCarted) = viewModelScope.launch {
        repository.insert(productCarted)
    }

    fun deleteBySku(sku : String) = viewModelScope.launch {
        repository.deleteBySku(sku)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun decreaseQuantity(sku: String) = viewModelScope.launch {
        repository.decreaseQuantity(sku)
    }

    fun increaseQuantity(sku: String) = viewModelScope.launch {
        repository.increaseQuantity(sku)
    }

}