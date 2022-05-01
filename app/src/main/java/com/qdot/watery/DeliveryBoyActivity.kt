package com.qdot.watery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qdot.watery.adapters.BoyOrderAdapter
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityDeliveryBoyBinding
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeliveryBoyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryBoyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryBoyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val client = Client(applicationContext)
            .setEndpoint(Constant.APP_WRITE_URL)
            .setProject("625a8333a56a338ac7d5")

        val account = Account(client)
        val idDatabase = Database(client)

        binding.orderListRecyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val acc = account.get()
            val listData = idDatabase.listDocuments(
                collectionId = "orders"
            )

            withContext(Dispatchers.Main){

                binding.toolBar.title = acc.name
                binding.toolBar.subtitle = acc.id

                val adapter = BoyOrderAdapter(listData.documents,
                    acc.name,
                    idDatabase
                )
                binding.orderListRecyclerView.adapter = adapter
            }
        }
    }
}