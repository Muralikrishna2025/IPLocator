package uk.ac.tees.mad.iplocator.model.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "search_history")
data class SearchHistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generated primary key

    @ColumnInfo(name = "user_id")
    val userId: String, // Firebase user ID

    @ColumnInfo(name = "searched_query")
    val searchedQuery: String, // The IP address or query searched

    @ColumnInfo(name = "timestamp")
    val timestamp: LocalDateTime // Timestamp of the search
)