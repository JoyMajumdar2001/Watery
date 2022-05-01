package com.qdot.watery

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityPaymentBinding
import com.qdot.watery.models.Order
import com.qdot.watery.room.CartedProductViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import io.appwrite.Client
import io.appwrite.extensions.toJson
import io.appwrite.services.Account
import io.appwrite.services.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToLong

class PaymentActivity : AppCompatActivity(), PaymentResultListener {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var newOrder : Order
    private lateinit var orderId : String
    private lateinit var database : Database
    private lateinit var orderItems : String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        orderId = UUID.randomUUID().toString()
        Checkout.preload(applicationContext)
        var name = "User"
        var email = "user@email.com"
        var userId = "ff0X"
        val mobNo = intent.getStringExtra(Constant.MOBILE_KEY_ACTIVITY)
        val lat = intent.getDoubleExtra(Constant.LAT_KEY_ACTIVITY,0.0)
        val lon = intent.getDoubleExtra(Constant.LON_KEY_ACTIVITY,0.0)
        val address = intent.getStringExtra(Constant.ADDRESS_KEY_ACTIVITY)
        val deliveryFee = 30f
        var paymentType: Int
        var subPrice = 0f
        var tax = 0L
        var totalPrice =  0.0

        val productViewModel = ViewModelProvider(this)[CartedProductViewModel::class.java]

        val client = Client(applicationContext)
            .setEndpoint(Constant.APP_WRITE_URL)
            .setProject("625a8333a56a338ac7d5")
        val account = Account(client)
        database = Database(client)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val accountDetails = account.get()
                name = accountDetails.name
                email = accountDetails.email
                userId = accountDetails.id
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        productViewModel.allProducts.observe(this){

            val sB = StringBuilder()
            it.mapIndexed { _, productCarted ->
                sB.append(productCarted.productName+"("+ productCarted.description+") ×" + productCarted.quantity + "\n")
                subPrice = (subPrice + (productCarted.price * productCarted.quantity)).toFloat()
            }

            orderItems = sB.toString()

            tax = (subPrice * 4 / 100).roundToLong()

            totalPrice = (subPrice + tax + deliveryFee).toDouble()

            binding.priceText.text = "$subPrice ₹"
            binding.serviceTaxText.text = "$tax ₹"
            binding.deliveryText.text = "$deliveryFee ₹"
            binding.totalPriceText.text = "${totalPrice.roundToLong()} ₹"

        }

        binding.codBtn.setOnClickListener {

            paymentType = Constant.PAYMENT_CASH_ON_DELIVERY
            newOrder = Order(
                orderId,
                name,
                email,
                mobNo!!,
                userId,
                System.currentTimeMillis(),
                address!!,
                lat,
                lon,
                totalPrice.roundToLong().toDouble(),
                tax.toDouble(),
                deliveryFee.toDouble(),
                Constant.ORDER_CREATED,
                "NULL",
                paymentType,
                "COD${System.currentTimeMillis()}IN",
                orderItems,
                System.currentTimeMillis()
            )

            CoroutineScope(Dispatchers.IO).launch {

                try {
                    database.createDocument(
                        "orders",
                        "unique()",
                        newOrder
                    )

                    val intent = Intent(this@PaymentActivity,PayStatusActivity::class.java).apply {
                        putExtra(Constant.ORDER_ID_KEY,orderId)
                        putExtra(Constant.ORDER_STATUS_KEY,Constant.ORDER_SUCCESS)
                    }
                    startActivity(intent)

                }catch (e:Exception){
                    val intent = Intent(this@PaymentActivity,PayStatusActivity::class.java).apply {
                        putExtra(Constant.ORDER_ID_KEY,orderId)
                        putExtra(Constant.ORDER_STATUS_KEY,Constant.ORDER_FAILED)
                    }
                    startActivity(intent)
                }
            }
        }


        binding.onlineBtn.setOnClickListener {
            paymentType = Constant.PAYMENT_ONLINE
            newOrder = Order(
                orderId,
                name,
                email,
                mobNo!!,
                userId,
                System.currentTimeMillis(),
                address!!,
                lat,
                lon,
                totalPrice.roundToLong().toDouble(),
                tax.toDouble(),
                deliveryFee.toDouble(),
                Constant.ORDER_CREATED,
                "NULL",
                paymentType,
                "RZP${System.currentTimeMillis()}IN",
                orderItems,
                System.currentTimeMillis()
            )

            val co = Checkout()
            co.setKeyID(getString(R.string.RZP_KEY))
            co.setImage(R.drawable.app_logo)
            val jsonObj = JSONObject()
            jsonObj.put("amount",totalPrice.roundToLong() * 100)
            jsonObj.put("currency","INR")
            jsonObj.put("receipt","rcpt_watery_${System.currentTimeMillis()}")
            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val credential = Credentials.basic(getString(R.string.RZP_KEY),
                getString(R.string.RZP_PASSWORD))
            val okClient = OkHttpClient()
            val body = jsonObj.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(Constant.BASE_URL)
                .addHeader("Authorization",credential)
                .post(body)
                .build()
            okClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Toast.makeText(this@PaymentActivity,
                        e.toString(),
                        Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonResponse = JSONObject(response.body!!.string())
                    val orderIdMain = jsonResponse.getString("id")
                    try {
                        val options = JSONObject()
                        options.put("name","Watery")
                        options.put("description","for order $orderId")
                        options.put("theme.color","#923A91")
                        options.put("currency","INR")
                        options.put("order_id",orderIdMain)
                        options.put("amount",totalPrice.roundToLong().toString())
                        val preFill = JSONObject()
                        preFill.put("email",email)
                        preFill.put("contact",mobNo)

                        options.put("prefill",preFill)
                        co.open(this@PaymentActivity,options)
                    } catch (e : Exception){
                        Toast.makeText(this@PaymentActivity,e.message,Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }

            })

        }


    }

    override fun onPaymentSuccess(paymentId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            database.createDocument(
                "orders",
                "unique()",
                newOrder.toJson()
            )
            val intent = Intent(this@PaymentActivity,PayStatusActivity::class.java).apply {
                putExtra(Constant.ORDER_ID_KEY,orderId)
                putExtra(Constant.ORDER_STATUS_KEY,Constant.ORDER_SUCCESS)
            }
            startActivity(intent)
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        val intent = Intent(this,PayStatusActivity::class.java).apply {
            putExtra(Constant.ORDER_ID_KEY,orderId)
            putExtra(Constant.ORDER_STATUS_KEY,Constant.ORDER_FAILED)
        }
        startActivity(intent)
    }
}