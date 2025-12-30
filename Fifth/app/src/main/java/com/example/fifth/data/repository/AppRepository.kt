package com.example.fifth.data.repository

import com.example.fifth.data.database.AppDatabase
import com.example.fifth.data.database.ContentEntity
import com.example.fifth.data.database.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

class AppRepository(
    private val database: AppDatabase
) {
    private val userDao = database.userDao()
    private val contentDao = database.contentDao()

    suspend fun registerUser(login: String, password: String, name: String): Long {
        val existingUser = userDao.getUserByLogin(login)
        if (existingUser != null) {
            throw IllegalArgumentException("Пользователь с таким логином уже существует")
        }

        val user = UserEntity(
            login = login,
            password = password,
            name = name
        )

        return userDao.insertUser(user)
    }

    suspend fun loginUser(login: String, password: String): UserEntity {
        val user = userDao.getUserByLogin(login)
            ?: throw IllegalArgumentException("Пользователь не найден")

        if (user.password != password) {
            throw IllegalArgumentException("Неверный пароль")
        }

        if (user.isDeleted) {
            val deletionDate = user.deletionDate
            if (deletionDate != null) {
                val sevenDaysAgo = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
                if (deletionDate.before(sevenDaysAgo)) {
                    userDao.deleteUser(user)
                    throw IllegalArgumentException("Аккаунт был окончательно удален")
                }
            }
            throw AccountSoftDeletedException(user)
        }

        return user
    }

    suspend fun getUserById(userId: Long): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun softDeleteUser(userId: Long) {
        userDao.softDeleteUser(userId, Date())
    }

    suspend fun restoreUser(userId: Long) {
        userDao.restoreUser(userId)
    }

    suspend fun permanentlyDeleteUser(user: UserEntity) {
        userDao.deleteUser(user)
    }

    suspend fun cleanupExpiredAccounts() {
        val sevenDaysAgo = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
        val expiredUsers = userDao.getExpiredDeletedUsers(sevenDaysAgo)
        expiredUsers.forEach { userDao.deleteUser(it) }
    }


    suspend fun addContent(
        userId: Long,
        title: String,
        description: String,
        category: String,
        rating: Float,
    ): Long {
        val content = ContentEntity(
            userId = userId,
            title = title,
            description = description,
            category = category,
            rating = rating,
        )

        return contentDao.insertContent(content)
    }

    fun getUserContent(userId: Long): Flow<List<ContentEntity>> {
        return contentDao.getAllContentByUser(userId)
    }

    fun getUserContentSortedByTitle(userId: Long): Flow<List<ContentEntity>> {
        return contentDao.getAllContentSortedByTitle(userId)
    }

    fun getUserContentSortedByDateNewest(userId: Long): Flow<List<ContentEntity>> {
        return contentDao.getAllContentSortedByDateNewest(userId)
    }

    fun getUserContentSortedByDateOldest(userId: Long): Flow<List<ContentEntity>> {
        return contentDao.getAllContentSortedByDateOldest(userId)
    }

    fun getUserContentSortedByRatingHigh(userId: Long): Flow<List<ContentEntity>> {
        return contentDao.getAllContentSortedByRatingHigh(userId)
    }

    fun getUserContentSortedByRatingLow(userId: Long): Flow<List<ContentEntity>> {
        return contentDao.getAllContentSortedByRatingLow(userId)
    }

    suspend fun deleteContent(content: ContentEntity) {
        contentDao.deleteContent(content)
    }

    suspend fun updateContent(content: ContentEntity) {
        contentDao.updateContent(content)
    }

    fun searchContent(userId: Long, query: String): Flow<List<ContentEntity>> {
        return contentDao.searchContent(userId, query)
    }
}

class AccountSoftDeletedException(val user: UserEntity) : Exception("Аккаунт был удален, но может быть восстановлен")