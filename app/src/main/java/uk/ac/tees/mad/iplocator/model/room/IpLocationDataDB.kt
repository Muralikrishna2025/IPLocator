package uk.ac.tees.mad.iplocator.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocationData

@Database(entities = [IpLocationData::class], version = 1)
@TypeConverters(IpLocationDataConverters::class) // Add this line
abstract class IpLocationDataDB : RoomDatabase() {
    abstract fun ipLocationDao(): IpLocationDataDao
}