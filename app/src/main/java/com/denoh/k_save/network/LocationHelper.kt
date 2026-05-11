package com.denoh.k_save.network

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*

object LocationHelper {
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onLocationFound: (String, LatLng?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val address = getAddressFromLocation(context, location.latitude, location.longitude)
                    onLocationFound(address, latLng)
                } else {
                    onLocationFound("Unknown Location", null)
                }
            }
            .addOnFailureListener {
                onLocationFound("Location Error", null)
            }
    }

    private fun getAddressFromLocation(context: Context, lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                address.thoroughfare ?: address.subLocality ?: address.locality ?: "Unknown Street"
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            "Nairobi" // Default fallback
        }
    }
}
