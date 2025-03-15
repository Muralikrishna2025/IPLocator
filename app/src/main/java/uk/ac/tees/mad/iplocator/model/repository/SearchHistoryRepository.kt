package uk.ac.tees.mad.iplocator.model.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.iplocator.model.dataclass.SearchHistoryItem
import uk.ac.tees.mad.iplocator.model.room.SearchHistoryDao
import java.time.LocalDateTime

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {
    suspend fun insertSearchHistory(searchHistoryItem: SearchHistoryItem) {
        searchHistoryDao.insert(searchHistoryItem)
    }

    suspend fun getSearchHistoryForUser(userId: String): List<SearchHistoryItem> {
        return searchHistoryDao.getSearchHistoryForUser(userId)
    }

    suspend fun clearSearchHistoryForUser(userId: String) {
        searchHistoryDao.clearSearchHistoryForUser(userId)
    }

    suspend fun clearAllSearchHistory() {
        searchHistoryDao.clearAllSearchHistory()
    }

    suspend fun deleteSearchHistoryItem(id: Int) {
        searchHistoryDao.deleteSearchHistoryItem(id)
    }

    suspend fun getSearchHistoryItem(id: Int): SearchHistoryItem? {
        return searchHistoryDao.getSearchHistoryItem(id)
    }

    suspend fun updateTimestampForExistingQuery(userId: String, query: String, newTimestamp: LocalDateTime) {
        searchHistoryDao.updateTimestampForExistingQuery(userId, query, newTimestamp)
    }

    suspend fun isQueryPresent(userId: String, query: String): Boolean {
        val count = searchHistoryDao.countQueriesForUser(userId, query)
        return count > 0
    }
}