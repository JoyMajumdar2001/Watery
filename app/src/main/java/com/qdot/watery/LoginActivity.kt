package com.qdot.watery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityLoginBinding
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var toggleState : MutableLiveData<Boolean> = MutableLiveData(false)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPref = getSharedPreferences(
            Constant.PREF_NAME, Context.MODE_PRIVATE)

        if (sharedPref.getBoolean(Constant.LOGIN_KEY,false) &&
            sharedPref.getInt(Constant.USER_TYPE_KEY,Constant.AS_USER) == Constant.AS_USER){
            startActivity(Intent(this,
                UserDashboardActivity::class.java))
            finish()
        }

        binding.topAppBar.subtitle = "Login for customer"

        val client = Client(applicationContext)
            .setEndpoint(Constant.APP_WRITE_URL)
            .setProject("625a8333a56a338ac7d5")

        val account = Account(client)

        binding.toggleTextBtn.setOnClickListener {
            toggleState.value = !toggleState.value!!
        }

        binding.sendLinkBtn.setOnClickListener {
            if (toggleState.value == true){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val loginResponse = account.createSession(
                            email = binding.emailEditText.text.toString().trim(),
                            password = binding.passEditText.text.toString().trim()
                        )
                        with (sharedPref.edit()) {
                            putBoolean(Constant.LOGIN_KEY,true)
                            putInt(Constant.USER_TYPE_KEY,Constant.AS_USER)
                            putString(Constant.SESSION_KEY, loginResponse.id)
                            apply()
                        }
                        withContext(Dispatchers.Main){
                            startActivity(Intent(this@LoginActivity,
                                UserDashboardActivity::class.java))
                            finish()
                        }
                    }catch (e:Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@LoginActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }else if (toggleState.value == false){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val createAccountResponse = account.create(
                            userId = UUID.randomUUID().toString(),
                            email = binding.emailEditText.text.toString().trim(),
                            password = binding.passEditText.text.toString().trim(),
                            name = binding.nameEditText.text.toString().trim(),
                        )
                        if (createAccountResponse.status){
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@LoginActivity,
                                    "Account created",
                                    Toast.LENGTH_SHORT).show()
                            }

                            val loginResponse = account.createSession(
                                email = binding.emailEditText.text.toString().trim(),
                                password = binding.passEditText.text.toString().trim()
                            )
                            if (loginResponse.current){
                                with (sharedPref.edit()) {
                                    putBoolean(Constant.LOGIN_KEY,true)
                                    putInt(Constant.USER_TYPE_KEY,Constant.AS_USER)
                                    putString(Constant.SESSION_KEY, loginResponse.id)
                                    apply()
                                }
                                withContext(Dispatchers.Main){
                                    startActivity(Intent(this@LoginActivity,
                                        UserDashboardActivity::class.java))
                                    finish()
                                }
                            }

                        }else{
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@LoginActivity,
                                    "Failed to create account",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }catch (e:AppwriteException){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@LoginActivity,
                                e.message.toString(),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        toggleState.observe(this){
            if (it){
                binding.nameLayout.visibility = View.GONE
                binding.sendLinkBtn.text = "Login"
                binding.toggleTextBtn.text = "Don't have an account? Create an account"
            }else{
                binding.nameLayout.visibility = View.VISIBLE
                binding.sendLinkBtn.text = "Create account"
                binding.toggleTextBtn.text = "Already have an account? Login"
            }
        }

    }
}