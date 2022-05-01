package com.qdot.watery.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qdot.watery.TrackOrderActivity
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.OrderLayoutBinding
import com.qdot.watery.models.Order
import io.appwrite.models.Document
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private var docsList: List<Document>, private var context: Context) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>()  {

    inner class ViewHolder(val binding: OrderLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(docsList[position]) {
                binding.orderIdText.text = "Order #${position+1}"
                binding.itemsText.text = this.data["orderItems"].toString()
                binding.orderPriceText.text = this.data["price"].toString() + " ₹"
                binding.orderDateText.text = getDateWithLong(this.data["orderPlacedTime"] as Long)
                val statusData1 = this.data["status"] as Long
                when(statusData1.toInt()){
                    Constant.ORDER_CREATED -> binding.orderStatus.text = "Order created"
                    Constant.ORDER_ACCEPTED -> binding.orderStatus.text = "Order accepted"
                    Constant.ORDER_SHIPPED -> binding.orderStatus.text = "Order shipped"
                    Constant.ORDER_DELIVERED -> {
                        binding.trackBtn.visibility = View.GONE
                        binding.orderStatus.text = "Order delivered (${getDateWithLong(this.data["currentTime"] as Long)})"
                    }
                }
                binding.trackBtn.setOnClickListener {
                    val priceData = this.data["price"] as Long
                    val taxData = this.data["tax"] as Long
                    val delFeeData = this.data["deliveryFee"] as Long
                    val statusData = this.data["status"] as Long
                    val payTypeData = this.data["paymentType"] as Long
                    val zyz = Order(
                        this.data["orderId"].toString(),
                        this.data["buyerName"].toString(),
                        this.data["buyerEmail"].toString(),
                        this.data["buyerPhone"].toString(),
                        this.data["buyerId"].toString(),
                        this.data["orderPlacedTime"] as Long,
                        this.data["orderAddress"].toString(),
                        this.data["orderLat"] as Double,
                        this.data["orderLon"] as Double,
                        priceData.toDouble(),
                        taxData.toDouble(),
                        delFeeData.toDouble(),
                        statusData.toInt(),
                        this.data["deliveryPartner"].toString(),
                        payTypeData.toInt(),
                        this.data["paymentId"].toString(),
                        this.data["orderItems"].toString(),
                        this.data["currentTime"] as Long
                    )
                    val intent = Intent(context,TrackOrderActivity::class.java).apply {
                        putExtra(Constant.ORDER_DET_PASS_KEY,zyz)
                    }
                    context.startActivity(intent)
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