package uk.ac.tees.mad.iplocator.model.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.google.android.gms.maps.model.LatLng
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocationData

@Dao
interface IpLocationDataDao {
    @Upsert
    suspend fun upsertIpLocationData(ipLocationData: IpLocationData)

    @Query("SELECT * FROM ip_location_data WHERE ip = :ip")
    suspend fun getIpLocationDataByIp(ip: String): IpLocationData?

    @Query("SELECT COUNT(*) FROM ip_location_data WHERE ip = :ip")
    suspend fun countIpLocationDataByIp(ip: String): Int

    @Query("UPDATE ip_location_data SET latitude = :latitude, longitude = :longitude, coordinates = :coordinates, address = :address WHERE ip = :ip")
    suspend fun updateIpLocation(
        ip: String,
        latitude: Double?,
        longitude: Double?,
        coordinates: LatLng,
        address: String
    )
}