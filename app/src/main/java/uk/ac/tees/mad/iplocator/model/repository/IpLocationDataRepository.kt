package uk.ac.tees.mad.iplocator.model.repository

import com.google.android.gms.maps.model.LatLng
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocationData
import uk.ac.tees.mad.iplocator.model.room.IpLocationDataDao

class IpLocationDataRepository(private val ipLocationDataDao: IpLocationDataDao) {

    suspend fun upsertIpLocationData(ipLocationData: IpLocationData) {
        ipLocationDataDao.upsertIpLocationData(ipLocationData)
    }

    suspend fun getIpLocationDataByIp(ip: String): IpLocationData? {
        return ipLocationDataDao.getIpLocationDataByIp(ip)
    }

    suspend fun countIpLocationDataByIp(ip: String): Int {
        return ipLocationDataDao.countIpLocationDataByIp(ip)
    }

    suspend fun updateIpLocation(
        ip: String,
        latitude: Double?,
        longitude: Double?,
        coordinates: LatLng,
        address: String
    ) {
        ipLocationDataDao.updateIpLocation(ip, latitude, longitude, coordinates, address)
    }

}