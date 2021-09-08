package ru.skillbranch.sbdelivery.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.skillbranch.sbdelivery.data.db.AppDb
import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {
    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context) : AppDb = Room.databaseBuilder(
        context,
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideArticlesDao(db: AppDb): DishesDao = db.dishesDao()

    @Provides
    @Singleton
    fun provideCartDao(db: AppDb): CartDao = db.cartDao()
}