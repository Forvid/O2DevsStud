package ru.forvid.o2devsstud.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.forvid.o2devsstud.data.repository.RoomOrdersRepository
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // bind Room implementation to interface
    @Binds
    @Singleton
    abstract fun bindOrdersRepository(
        room: RoomOrdersRepository
    ): OrdersRepository
}
