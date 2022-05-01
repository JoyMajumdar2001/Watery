package com.qdot.watery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.qdot.watery.constants.Constant
import com.qdot.watery.databinding.ActivityTrackOrderBinding
import com.qdot.watery.models.Order
import java.text.SimpleDateFormat
import java.util.*

class TrackOrderActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTrackOrderBinding
    private val MAPVIEW_BUNDLE_KEY = "Your api key"
    private lateinit var orderDetail : Order
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        orderDetail = intent.getSerializableExtra(Constant.ORDER_DET_PASS_KEY) as Order
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)

        binding.timeText.text = getDateWithLong(orderDetail.currentTime)

        when(orderDetail.status){
            Constant.ORDER_CREATED -> binding.orderStatusText.text = "Order has been placed"
            Constant.ORDER_ACCEPTED -> binding.orderStatusText.text = "Order has been accepted"
            Constant.ORDER_SHIPPED -> {
                binding.orderStatusText.text = "Order has been shipped!"
                binding.delName.text = orderDetail.deliveryPartner
            }
            Constant.ORDER_DELIVERED -> binding.orderStatusText.text = "Order has been delivered!"
        }

        binding.delLay.visibility = View.GONE
        binding.delText.visibility = View.GONE

        binding.delName.text = orderDetail.deliveryPartner

        if (orderDetail.status == Constant.ORDER_SHIPPED){
            binding.delLay.visibility = View.VISIBLE
            binding.delText.visibility = View.VISIBLE
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        binding.mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onMapReady(gMap: GoogleMap) {

        val source = LatLng(24.9978,88.1350)
        val destination = LatLng(orderDetail.orderLat,orderDetail.orderLon)

        gMap.run {
            addMarker(MarkerOptions().position(source)
                .title("Factory"))
            addMarker(MarkerOptions().position(destination)
                .title("Home"))
            addPolyline(PolylineOptions().add(source,destination).width(5F).geodesic(true))
            animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15F))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateWithLong(timeLong : Long): String {
        val format = SimpleDateFormat("dd MMM yyyy hh:mm")
        return format.format(Date(timeLong))
    }
}