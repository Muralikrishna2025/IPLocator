package uk.ac.tees.mad.iplocator.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class MapScreenViewModel(): ViewModel(){

    fun reverseGeocodeLocation(context: Context, latitude: Double?, longitude: Double?): String{
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(latitude!!, longitude!!)
        val addresses: MutableList<Address>? = geocoder.getFromLocation(coordinate.latitude,coordinate.longitude, 1)
        return if(addresses != null && addresses.isNotEmpty()){
            addresses[0].getAddressLine(0)
        } else{
            "Address not found"
        }
    }

}