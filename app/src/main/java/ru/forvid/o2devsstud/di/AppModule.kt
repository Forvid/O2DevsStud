package ru.forvid.o2devsstud.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Пустой модуль-«держатель».
 * Ранее там был @Binds для FakeOrdersRepository — он закомментирован, чтобы
 * не было дублирующих биндингов (conflict "bound multiple times").
 *
 * Если нужно заменить provides -> binds, тогда:
 *  - разморозить реализацию с @Binds и уадлить RepositoryModule.
 *
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    // intentionally empty — kept for future Binds-based wiring if needed
}
