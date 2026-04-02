package com.example.vocabmaster.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Query("SELECT * FROM words ORDER BY isMemorized ASC, lastReviewed ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isMemorized = 0 ORDER BY id DESC")
    fun getUnmemorizedWords(): Flow<List<Word>>

    @Delete
    suspend fun delete(word: Word)

    @Query("UPDATE words SET isMemorized = :isMemorized WHERE id = :id")
    suspend fun updateMemorizedStatus(id: String, isMemorized: Boolean)
}
