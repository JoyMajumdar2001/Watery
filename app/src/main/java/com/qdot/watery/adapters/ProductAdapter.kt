package com.qdot.watery.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.qdot.watery.R
import com.qdot.watery.databinding.ProductLayoutBinding
import com.qdot.watery.models.ProductCarted
import com.qdot.watery.room.CartedProductViewModel
import io.appwrite.models.Document

class ProductAdapter(private var docsList: List<Document>,
                     private var viewModel: CartedProductViewModel,
                     private var lifecycleOwner: LifecycleOwner
                     ): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ProductLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(docsList[position]){
                binding.prodTitle.text = this.data["name"].toString()
                binding.prodImage.load(this.data["imageUrl"].toString()){
                    placeholder(R.drawable.ic_bottle_svgrepo_com)
                    error(R.drawable.ic_bottle_svgrepo_com)
                }
                binding.prodDesc.text = this.data["description"].toString()
                binding.prodPrice.text = this.data["price"].toString() + " ₹"
                binding.cartButton.isEnabled = false

                viewModel.getExistence(this.data["sku"].toString()).observe(lifecycleOwner){
                    if (it){
                        binding.cartButton.text = "Carted"
                        binding.cartButton.icon = ResourcesCompat.getDrawable(binding.root.context.resources,
                            R.drawable.ic_baseline_check,
                            null)
                    }else{
                        binding.cartButton.text = "Add"
                        binding.cartButton.icon = ResourcesCompat.getDrawable(binding.root.context.resources,
                            R.drawable.ic_baseline_shopping_cart,
                            null)
                    }
                    binding.cartButton.isEnabled = true
                }

                binding.cartButton.setOnClickListener {
                    if (binding.cartButton.text.equals("Carted")){
                        viewModel.deleteBySku(this.data["sku"].toString())

                    }else if (binding.cartButton.text.equals("Add")){
                        viewModel.insert(ProductCarted(
                            id = 0,
                            productName = this.data["name"].toString(),
                            sku = this.data["sku"].toString(),
                            imageUrl = this.data["imageUrl"].toString(),
                            price = this.data["price"] as Double,
                            time = System.currentTimeMillis(),
                            quantity = 1,
                            description = this.data["description"].toString()
                        ))

                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return docsList.size
    }

}