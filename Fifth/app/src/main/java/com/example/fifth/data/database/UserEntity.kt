package com.example.fifth.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "login")
    val login: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "is_deleted")
    var isDeleted: Boolean = false,

    @ColumnInfo(name = "deletion_date")
    var deletionDate: Date? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date()
)