package uk.ac.tees.mad.iplocator.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uk.ac.tees.mad.iplocator.model.dataclass.SearchHistoryItem
import java.time.LocalDateTime

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insert(searchHistoryItem: SearchHistoryItem)

    @Query("SELECT * FROM search_history WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getSearchHistoryForUser(userId: String): List<SearchHistoryItem>

    @Query("DELETE FROM search_history WHERE user_id = :userId")
    suspend fun clearSearchHistoryForUser(userId: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearchHistory()

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistoryItem(id: Int)

    @Query("SELECT * FROM search_history WHERE id = :id")
    suspend fun getSearchHistoryItem(id: Int): SearchHistoryItem?

    @Query("SELECT COUNT(*) FROM search_history WHERE user_id = :userId AND searched_query = :query")
    suspend fun countQueriesForUser(userId: String, query: String): Int

    @Query("UPDATE search_history SET timestamp = :newTimestamp WHERE user_id = :userId AND searched_query = :query")
    suspend fun updateTimestampForExistingQuery(
        userId: String, query: String, newTimestamp: LocalDateTime
    )


}