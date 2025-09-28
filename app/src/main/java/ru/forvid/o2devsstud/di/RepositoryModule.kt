package ru.forvid.o2devsstud.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.forvid.o2devsstud.data.local.OrdersDatabase
import ru.forvid.o2devsstud.data.repository.RoomOrdersRepository
import ru.forvid.o2devsstud.domain.repository.OrdersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OrdersDatabase {
        return Room.databaseBuilder(context, OrdersDatabase::class.java, "orders.db")
            .fallbackToDestructiveMigration() // для разработки; в проде нужно миграции
            .build()
    }

    @Provides
    @Singleton
    fun provideOrderDao(db: OrdersDatabase) = db.orderDao()

    @Provides
    @Singleton
    fun provideOrdersRepository(dao: ru.forvid.o2devsstud.data.local.OrderDao): OrdersRepository {
        // По умолчанию использую реализацию на Room
        return RoomOrdersRepository(dao)
    }

    // Если нужно временно вместо Room использовать Fake (удалить/закомментировать provideOrdersRepository выше)
    /*
    @Provides
    @Singleton
    fun provideFakeOrdersRepository(): OrdersRepository = FakeOrdersRepository()
    */
}
