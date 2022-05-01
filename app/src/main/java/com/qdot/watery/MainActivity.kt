package com.qdot.watery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityMainBinding
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPref = getSharedPreferences(
            Constant.PREF_NAME, Context.MODE_PRIVATE)

        if (sharedPref.getBoolean(Constant.LOGIN_KEY,false) &&
            sharedPref.getInt(Constant.USER_TYPE_KEY, Constant.AS_USER) == Constant.AS_USER){
            startActivity(Intent(this,
                UserDashboardActivity::class.java))
            finish()
        }

        val client = Client(applicationContext)
            .setEndpoint(Constant.APP_WRITE_URL)
            .setProject("625a8333a56a338ac7d5")

        val account = Account(client)

        binding.customerLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            Animatoo.animateSlideUp(this)
            finish()
        }

        binding.delPartnerLogin.setOnClickListener {
            InputSheet().show(this){
                title("Delivery partner login")
                with(InputEditText{
                    required(true)
                    label("Your Email")
                    hint("email@email.com")
                })
                with(InputEditText{
                    required(true)
                    label("Your Password")
                    hint("********")
                })
                onNegative {  }
                onPositive("Login") { result ->
                    val email = result.getString("0").toString().trim()
                    val pass = result.getString("1").toString().trim()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            account.createSession(
                                email = email,
                                password = pass
                            )
                            this@MainActivity.startActivity(Intent(this@MainActivity,DeliveryBoyActivity::class.java))
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}