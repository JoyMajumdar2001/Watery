package com.qdot.watery

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qdot.watery.adapters.OrderAdapter
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityMyOrdersBinding
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.services.Account
import io.appwrite.services.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val client = Client(applicationContext)
            .setEndpoint(Constant.APP_WRITE_URL)
            .setProject("625a8333a56a338ac7d5")

        val account = Account(client)
        val orderDatabase = Database(client)

        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val accountDetails = account.get()
                val listData = orderDatabase.listDocuments(
                    collectionId = "orders",
                    queries = listOf(Query.equal("buyerId", accountDetails.id))
                )
                withContext(Dispatchers.Main) {
                    val adapter = OrderAdapter(listData.documents, this@MyOrdersActivity)
                    binding.orderRecyclerView.adapter = adapter
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MyOrdersActivity,e.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}