package ru.forvid.o2devsstud.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.forvid.o2devsstud.BuildConfig
import ru.forvid.o2devsstud.data.repository.repository.ApiService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): Interceptor = Interceptor { chain ->
        val apiKey = BuildConfig.SERVER_API_KEY.ifBlank {
            // Фоллбек только для локальной разработки (НЕ пушить секреты в репозиторий)
            "c1378193-bc0e-42c8-a502-b8d66d189617"
        }
        val request = chain.request().newBuilder()
            .addHeader("X-API-KEY", apiKey)
            .build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        // На реальном устройстве с adb reverse -> используем localhost 127.0.0.1:8080
        // На эмуляторе используется 10.0.2.2:8080
        val localForDevice = "http://127.0.0.1:8080/"
        val emulator = "http://10.0.2.2:8080/"

        val baseUrl = when {
            BuildConfig.DEBUG -> localForDevice
            else -> "http://94.228.125.136:8080/"
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
