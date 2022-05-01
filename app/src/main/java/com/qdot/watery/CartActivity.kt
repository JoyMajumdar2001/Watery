package com.qdot.watery

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.qdot.watery.adapters.CartAdapter
import com.qdot.watery.databinding.ActivityCartBinding
import com.qdot.watery.room.CartedProductViewModel
import kotlin.math.roundToLong

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val productViewModel = ViewModelProvider(this)[CartedProductViewModel::class.java]

        binding.itemRecyclerView.layoutManager = LinearLayoutManager(this)

        val deliveryFee = 30f

        productViewModel.allProducts.observe(this){
            val ourAdapter = CartAdapter(it,productViewModel,this)
            binding.itemRecyclerView.adapter = ourAdapter

            var subPrice = 0f

            it.mapIndexed { _, productCarted ->
                subPrice = (subPrice + (productCarted.price * productCarted.quantity)).toFloat()
            }

            val tax = (subPrice * 4 / 100).roundToLong()

            val totalPrice = subPrice + tax + deliveryFee

            binding.priceText.text = "$subPrice ₹"
            binding.serviceTaxText.text = "$tax ₹"
            binding.deliveryText.text = "$deliveryFee ₹"
            binding.totalPriceText.text = "$totalPrice ₹"

        }

        binding.checkOutFab.setOnClickListener {
            startActivity(Intent(this,LocActivity::class.java))
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.deleteShoppingCart -> {
                    productViewModel.deleteAll()
                    true
                }
                else -> false
            }

        }

    }
}