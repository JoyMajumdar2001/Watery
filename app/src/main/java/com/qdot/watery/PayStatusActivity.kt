package com.qdot.watery

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityPayStatusBinding

class PayStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayStatusBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayStatusBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val paySts = intent.getIntExtra(Constant.ORDER_STATUS_KEY,1)
        val orderId = intent.getStringExtra(Constant.ORDER_ID_KEY)

        if (paySts == Constant.ORDER_SUCCESS){
            binding.stsImg.load(R.drawable.ic_baseline_done_outline)
            binding.stsText.text = "Your order #$orderId is successfully placed"
        }else{
            binding.stsImg.load(R.drawable.ic_baseline_error_outline)
            binding.stsText.text = "Your order #$orderId is failed"
        }

        binding.doneButton.setOnClickListener {
            startActivity(Intent(this,UserDashboardActivity::class.java))
            finishAffinity()
        }

    }

    override fun onBackPressed() {

    }
}