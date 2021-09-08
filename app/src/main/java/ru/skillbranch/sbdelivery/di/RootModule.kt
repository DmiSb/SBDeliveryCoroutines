package ru.skillbranch.sbdelivery.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import ru.skillbranch.sbdelivery.screens.root.logic.Command
import ru.skillbranch.sbdelivery.screens.root.logic.Eff

@InstallIn(ViewModelComponent::class)
@Module
object RootModule {
    @ViewModelScoped
    @Provides
    fun provideNotificationChannel() : Channel<Eff.Notification> = Channel()

    @ViewModelScoped
    @Provides
    fun provideCmdChannel() : Channel<Command> = Channel()


    @ViewModelScoped
    @Provides
    fun provideDispatcher() : CoroutineDispatcher = Dispatchers.Default
}