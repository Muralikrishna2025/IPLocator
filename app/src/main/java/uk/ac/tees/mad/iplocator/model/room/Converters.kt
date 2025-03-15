package uk.ac.tees.mad.iplocator.model.room

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

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