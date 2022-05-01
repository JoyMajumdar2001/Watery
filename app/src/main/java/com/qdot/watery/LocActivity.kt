package com.qdot.watery

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LOCATION_ADDRESS
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityLocBinding
import com.qdot.watery.utils.MeasureDistance

class LocActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocBinding
    private var longitude : Double = 0.0
    private var latitude : Double = 0.0
    private var address : String = "Not detected"

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val sharedPref = getSharedPreferences(
            Constant.PREF_NAME, Context.MODE_PRIVATE)

        binding.infoLay.visibility = View.INVISIBLE
        binding.doneBtn.isEnabled = false

        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(sharedPref.getLong(Constant.LAT_KEY,0).toDouble(), sharedPref.getLong(Constant.LONG_KEY,0).toDouble())
            .withGeolocApiKey("AIzaSyBl4ReTeCNIbTVwgLIfvZ4LdpPaoWwkawA")
            .withSearchZone("es_ES")
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
            .withStreetHidden()
            .withCityHidden()
            .withZipCodeHidden()
            .withSatelliteViewHidden()
            .withGoogleTimeZoneEnabled()
            .withVoiceSearchHidden()
            .withUnnamedRoadHidden()
            .build(applicationContext)

        binding.pickAddressBtn.setOnClickListener {
            getResult.launch(locationPickerIntent)
        }

        binding.doneBtn.setOnClickListener {
            if (binding.mobText.text.isNullOrBlank()){
                Toast.makeText(this,"Enter mobile number",Toast.LENGTH_SHORT).show()
            }else {
                val intent = Intent(this, PaymentActivity::class.java).apply {
                    putExtra(Constant.LAT_KEY_ACTIVITY, latitude)
                    putExtra(Constant.LON_KEY_ACTIVITY, longitude)
                    putExtra(Constant.ADDRESS_KEY_ACTIVITY, address)
                    putExtra(Constant.MOBILE_KEY_ACTIVITY, binding.mobText.text.toString().trim())
                }
                startActivity(intent)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {

                    binding.infoLay.visibility = View.VISIBLE

                    latitude = it.data!!.getDoubleExtra(LATITUDE, 0.0)

                    longitude = it.data!!.getDoubleExtra(LONGITUDE, 0.0)

                    val distance = MeasureDistance.getDistanceFromFactory(latitude,longitude)

                    val address1 = it.data!!.getStringExtra(LOCATION_ADDRESS)

                    address = address1.toString()


                    if (distance <= 25){

                        binding.addressText.text = address1.toString()
                        binding.doneBtn.isEnabled = true

                    }else{
                        binding.textInputLay.visibility = View.GONE
                        binding.addressText.text = address1.toString() + "\n[You are out of our reach, we'll be near your locality soon]"
                    }

                }
            }
        }
}