package ru.forvid.o2devsstud.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.forvid.o2devsstud.domain.util.OrderCompletionNotifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun provideOrderCompletionNotifier(): OrderCompletionNotifier {
        return OrderCompletionNotifier()
    }
}
