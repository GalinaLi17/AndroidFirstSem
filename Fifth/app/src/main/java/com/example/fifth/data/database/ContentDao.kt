package com.example.fifth.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity): Long

    @Query("SELECT * FROM content WHERE user_id = :userId ORDER BY added_date DESC")
    fun getAllContentByUser(userId: Long): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE user_id = :userId ORDER BY title ASC")
    fun getAllContentSortedByTitle(userId: Long): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE user_id = :userId ORDER BY added_date DESC")
    fun getAllContentSortedByDateNewest(userId: Long): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE user_id = :userId ORDER BY added_date ASC")
    fun getAllContentSortedByDateOldest(userId: Long): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE user_id = :userId ORDER BY rating DESC")
    fun getAllContentSortedByRatingHigh(userId: Long): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE user_id = :userId ORDER BY rating ASC")
    fun getAllContentSortedByRatingLow(userId: Long): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE id = :id")
    suspend fun getContentById(id: Long): ContentEntity?

    @Update
    suspend fun updateContent(content: ContentEntity)

    @Delete
    suspend fun deleteContent(content: ContentEntity)

    @Query("DELETE FROM content WHERE user_id = :userId")
    suspend fun deleteAllContentByUser(userId: Long)

    @Query("SELECT * FROM content WHERE user_id = :userId AND title LIKE '%' || :search || '%'")
    fun searchContent(userId: Long, search: String): Flow<List<ContentEntity>>
}