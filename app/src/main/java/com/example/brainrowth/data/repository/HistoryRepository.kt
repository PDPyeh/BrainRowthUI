package com.example.brainrowth.data.repository

import com.example.brainrowth.data.local.HistoryDao
import com.example.brainrowth.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()
    
    suspend fun insert(history: HistoryEntity): Long {
        return historyDao.insertHistory(history)
    }
    
    suspend fun delete(history: HistoryEntity) {
        historyDao.deleteHistory(history)
    }
    
    suspend fun deleteById(id: Long) {
        historyDao.deleteHistoryById(id)
    }
    
    suspend fun deleteAll() {
        historyDao.deleteAllHistory()
    }
    
    suspend fun getById(id: Long): HistoryEntity? {
        return historyDao.getHistoryById(id)
    }
    
    suspend fun getCount(): Int {
        return historyDao.getHistoryCount()
    }
}
