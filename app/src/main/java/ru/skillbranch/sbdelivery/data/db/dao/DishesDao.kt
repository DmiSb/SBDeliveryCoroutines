package ru.skillbranch.sbdelivery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.skillbranch.sbdelivery.data.db.entity.DishPersist

@Dao
interface DishesDao {
    @Query("SELECT * FROM dishes")
    suspend fun findAllDishes(): List<DishPersist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDishes(dishes: List<DishPersist>)

    @Query("SELECT COUNT(*) FROM dishes")
    suspend fun dishesCounts(): Int

    @Query("SELECT * FROM dishes WHERE name LIKE '%' || :searchText || '%' ORDER BY name ASC")
    suspend fun findDishesFrom(searchText: String): List<DishPersist>

    @Query("SELECT * FROM dishes WHERE id=:id")
    suspend fun findDish(id: String): DishPersist
}