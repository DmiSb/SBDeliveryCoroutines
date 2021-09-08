package ru.skillbranch.sbdelivery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class DishPersist(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    val name: String,
    val description: String,
    val image: String,
    val oldPrice: Int?,
    val price: Int,
    val rating: Float,
    val likes: Int,
    val category: String,
    val commentsCount: Int,
    val active: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)