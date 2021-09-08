package ru.skillbranch.sbdelivery.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.skillbranch.sbdelivery.BuildConfig
import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemDbView
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.db.entity.DishPersist

@Database(entities = [DishPersist::class, CartItemPersist::class], views = [CartItemDbView::class], version = AppDb.DATABASE_VERSION, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }
    abstract fun dishesDao(): DishesDao
    abstract fun cartDao(): CartDao
}