package com.qdot.watery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.qdot.watery.databinding.CartProductLayoutBinding
import com.qdot.watery.models.ProductCarted
import com.qdot.watery.room.CartedProductViewModel

class CartAdapter(private var docsList: List<ProductCarted>,
                  private var viewModel: CartedProductViewModel,
                  private var lifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CartProductLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.ViewHolder {
        val binding = CartProductLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(docsList[position]){
                binding.productTitle.text = this.productName
                binding.productDescription.text = this.description
                binding.productImage.load(this.imageUrl){

                }
                viewModel.getQuantity(this.sku).observe(lifecycleOwner){
                    if (it <= 0){
                        viewModel.deleteBySku(this.sku)
                    }
                    binding.quantityText.text = it.toString()
                }

                binding.minusBtn.setOnClickListener {
                    viewModel.decreaseQuantity(this.sku)
                }
                binding.plusBtn.setOnClickListener {
                    viewModel.increaseQuantity(this.sku)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return docsList.size
    }

}