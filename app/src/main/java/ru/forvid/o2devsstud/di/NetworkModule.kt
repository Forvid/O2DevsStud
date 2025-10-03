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
import ru.forvid.o2devsstud.data.remote.ApiService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Интерцептор: добавляет заголовок с серверным ключом
    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): Interceptor = Interceptor { chain ->
        val apiKey = BuildConfig.SERVER_API_KEY.ifBlank {
            // fallback только для разработки — не хранить реальные ключи в коде
            "c1378193-bc0e-42c8-a502-b8d66d189617"
        }

        val original = chain.request()
        val request = original.newBuilder()
            .addHeader("X-API-KEY", apiKey)
            .build()
        chain.proceed(request)
    }

    // OkHttp client с логированием
    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // В debug смотрим BODY, в релизе — минимально
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)   // добавляем ключ в заголовок
            .addInterceptor(logging)             // логируем (после добавления заголовка)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://94.228.125.136:8080/") // сервер для диплома
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
