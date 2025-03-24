package uk.ac.tees.mad.iplocator.model.room

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uk.ac.tees.mad.iplocator.model.dataclass.Language
import uk.ac.tees.mad.iplocator.model.dataclass.Location
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneOffset

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }
}

@ProvidedTypeConverter
class IpLocationDataConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toLocation(locationString: String?): Location? {
        return gson.fromJson(locationString, Location::class.java)
    }

    @TypeConverter
    fun fromLanguageList(languages: List<Language?>?): String? {
        return gson.toJson(languages)
    }

    @TypeConverter
    fun toLanguageList(languageString: String?): List<Language?>? {
        val listType: Type = object : TypeToken<List<Language?>?>() {}.type
        return gson.fromJson(languageString, listType)
    }

    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    @TypeConverter
    fun toLatLng(latLngString: String): LatLng {
        val parts = latLngString.split(",")
        return LatLng(parts[0].toDouble(), parts[1].toDouble())
    }
}