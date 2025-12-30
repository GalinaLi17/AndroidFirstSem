package com.example.fifth.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun getUserByLogin(login: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE login = :login")
    suspend fun checkUserExists(login: String): Int

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET is_deleted = 1, deletion_date = :deletionDate WHERE id = :userId")
    suspend fun softDeleteUser(userId: Long, deletionDate: Date)

    @Query("UPDATE users SET is_deleted = 0, deletion_date = NULL WHERE id = :userId")
    suspend fun restoreUser(userId: Long)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE is_deleted = 1 AND deletion_date < :dateThreshold")
    suspend fun getExpiredDeletedUsers(dateThreshold: Date): List<UserEntity>
}