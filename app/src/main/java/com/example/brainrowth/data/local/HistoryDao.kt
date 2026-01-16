package com.example.brainrowth.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>
    
    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: Long): HistoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long
    
    @Delete
    suspend fun deleteHistory(history: HistoryEntity)
    
    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)
    
    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()
    
    @Query("SELECT COUNT(*) FROM history")
    suspend fun getHistoryCount(): Int
}
