package ru.forvid.o2devsstud.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.forvid.o2devsstud.data.repository.local.AppDatabase
import ru.forvid.o2devsstud.data.repository.local.OrderDao
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "orders_db"
        ).fallbackToDestructiveMigration() // на этапе разработки удобно
            .build()
    }

    @Provides
    @Singleton
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
}
