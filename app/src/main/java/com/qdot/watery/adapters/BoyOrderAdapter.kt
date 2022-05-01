package com.qdot.watery.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.BoyOrderLayoutBinding
import com.qdot.watery.models.UpdateOrder
import io.appwrite.models.Document
import io.appwrite.services.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BoyOrderAdapter(private var docsList: List<Document>, private var myName : String, private var database: Database) :
    RecyclerView.Adapter<BoyOrderAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BoyOrderLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BoyOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            var state = Constant.ORDER_CREATED
            with(docsList[position]) {
                binding.orderItemsText.text = this.data["orderItems"].toString()
                binding.addressTextView.text = this.data["orderAddress"].toString() + "\n" + this.data["buyerPhone"].toString()
                val statusData1 = this.data["status"] as Long
                when(statusData1.toInt()){
                    Constant.ORDER_CREATED -> {
                        binding.currentStatusOrder.text = "Order created"
                        binding.orderStatusBtn.text = "Accept order"
                        state = Constant.ORDER_ACCEPTED
                    }
                    Constant.ORDER_ACCEPTED -> {
                        binding.currentStatusOrder.text = "Order accepted"
                        binding.orderStatusBtn.text = "Ship order"
                        state = Constant.ORDER_SHIPPED
                    }
                    Constant.ORDER_SHIPPED -> {
                        binding.currentStatusOrder.text = "Order shipped"
                        binding.orderStatusBtn.text = "Deliver order"
                        state = Constant.ORDER_DELIVERED
                    }
                    Constant.ORDER_DELIVERED -> {
                        binding.orderStatusBtn.visibility = View.GONE
                        binding.currentStatusOrder.text = "Order delivered (${getDateWithLong(this.data["currentTime"] as Long)})"
                    }
                }

                val doc = this

                binding.orderStatusBtn.setOnClickListener {
                    when(state){
                        Constant.ORDER_ACCEPTED -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                database.updateDocument(
                                    collectionId = "orders",
                                    documentId = doc.id,
                                    data = UpdateOrder(Constant.ORDER_ACCEPTED,myName,System.currentTimeMillis())
                                )
                            }
                        }
                        Constant.ORDER_SHIPPED -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                database.updateDocument(
                                    collectionId = "orders",
                                    documentId = doc.id,
                                    data = UpdateOrder(Constant.ORDER_SHIPPED,myName,System.currentTimeMillis())
                                )
                            }
                        }
                        Constant.ORDER_DELIVERED -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                database.updateDocument(
                                    collectionId = "orders",
                                    documentId = doc.id,
                                    data = UpdateOrder(Constant.ORDER_DELIVERED,myName,System.currentTimeMillis())
                                )
                            }
                        }
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return docsList.size
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateWithLong(timeLong : Long): String {
        val format = SimpleDateFormat("dd MMM yyyy")
        return format.format(Date(timeLong))
    }

}