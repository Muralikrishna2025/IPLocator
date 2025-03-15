package uk.ac.tees.mad.iplocator.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.ac.tees.mad.iplocator.model.dataclass.SearchHistoryItem

@Database(entities = [SearchHistoryItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SearchHistoryDB : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}