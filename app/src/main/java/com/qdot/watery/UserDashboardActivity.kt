package com.qdot.watery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.*
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.markodevcic.peko.Peko
import com.markodevcic.peko.PermissionResult
import com.qdot.watery.adapters.ProductAdapter
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityUserDashboardBinding
import com.qdot.watery.room.CartedProductViewModel
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class UserDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDashboardBinding
    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val productViewModel = ViewModelProvider(this)[CartedProductViewModel::class.java]

        val client = Client(applicationContext)
            .setEndpoint(Constant.APP_WRITE_URL)
            .setProject("625a8333a56a338ac7d5")

        val account = Account(client)
        val database = Database(client)

        productViewModel.productCount.observe(this){
            val badge = BadgeDrawable.create(this)
            badge.number = it

            BadgeUtils.attachBadgeDrawable(
                badge,
                binding.toolBar,
                R.id.shoppingCart
            )
        }

        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.shoppingCart -> {
                    startActivity(Intent(this,CartActivity::class.java))
                    true
                }
                R.id.orderList -> {
                    startActivity(Intent(this,MyOrdersActivity::class.java))
                    true
                }
                else -> false
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            val result = Peko.requestPermissionsAsync(this@UserDashboardActivity,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)

            if (result is PermissionResult.Granted) {
                doLocationWork()
            } else {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@UserDashboardActivity,
                        "Give permission to access location",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        ViewCompat.setNestedScrollingEnabled(binding.ourBrandRecyclerView,false)
        ViewCompat.setNestedScrollingEnabled(binding.bisleriBrandRecyclerView,false)

        binding.ourBrandRecyclerView.layoutManager = GridLayoutManager(this,2)
        binding.bisleriBrandRecyclerView.layoutManager = GridLayoutManager(this,2)


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val accountDetails = account.get()

                val productList = database.listDocuments(
                    collectionId = "watrey-products",
                )
                val bisleriProductList = database.listDocuments(
                    collectionId = "bisleri-products",
                )
                withContext(Dispatchers.Main){
                    binding.toolBar.title = accountDetails.name
                    binding.toolBar.setNavigationIcon(R.drawable.ic_baseline_account_circle)
                    val ourAdapter = ProductAdapter(productList.documents,productViewModel,this@UserDashboardActivity)
                    binding.ourBrandRecyclerView.adapter = ourAdapter
                    val bisleriAdapter = ProductAdapter(bisleriProductList.documents,productViewModel,this@UserDashboardActivity)
                    binding.bisleriBrandRecyclerView.adapter = bisleriAdapter
                }

            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@UserDashboardActivity,
                        e.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun doLocationWork() {

        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 300
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        LocationServices.getFusedLocationProviderClient(applicationContext)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult : LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(applicationContext)
                        .removeLocationUpdates(this)

                    if (!locationResult.equals(null) && locationResult.locations.size > 0) {
                        val latestLocIndex: Int = locationResult.locations.size - 1
                        val latitude: Double =
                            locationResult.locations[latestLocIndex].latitude
                        val longitude: Double =
                            locationResult.locations[latestLocIndex].longitude

                        val sharedPref = getSharedPreferences(
                            Constant.PREF_NAME, Context.MODE_PRIVATE)

                        with (sharedPref.edit()) {
                            putLong(Constant.LAT_KEY,latitude.toLong())
                            putLong(Constant.LONG_KEY,longitude.toLong())
                            apply()
                        }

                        val location = Location("providerNA")
                        location.longitude = longitude
                        location.latitude = latitude
                        val geoCoder = Geocoder(this@UserDashboardActivity, Locale.getDefault())
                        try {
                            val addresses = geoCoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1)

                            val currentAddress = addresses[0]

                            CoroutineScope(Dispatchers.Main).launch{
                                binding.toolBar.subtitle= currentAddress.locality
                            }

                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }

                }

                override fun onLocationAvailability(p0: LocationAvailability) {
                    super.onLocationAvailability(p0)
                    if (!p0.isLocationAvailable){
                        CoroutineScope(Dispatchers.Main).launch{
                            Toast.makeText(this@UserDashboardActivity,
                                "Location not available",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }, Looper.getMainLooper())

    }

}