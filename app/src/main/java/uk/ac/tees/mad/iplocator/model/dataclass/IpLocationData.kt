package uk.ac.tees.mad.iplocator.model.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "ip_location_data")
data class IpLocationData(
    @PrimaryKey val ip: String = "1.1.1.1",
    val type: String? = null,
    val continentCode: String? = null,
    val continentName: String? = null,
    val countryCode: String? = null,
    val countryName: String? = null,
    val regionCode: String? = null,
    val regionName: String? = null,
    val city: String? = null,
    val zip: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val msa: String? = null,
    val dma: String? = null,
    val radius: String? = null,
    val ipRoutingType: String? = null,
    val connectionType: String? = null,
    val location: Location? = null,
    val coordinates: LatLng = LatLng(0.0, 0.0),
    val address: String? = null
)