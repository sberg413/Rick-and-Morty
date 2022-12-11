package com.sberg413.rickandmorty.di

import com.sberg413.rickandmorty.BuildConfig
import com.sberg413.rickandmorty.api.ApiService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val AppModule = module(){

    factory {
        Moshi.Builder().build()
    }

    factory {
        OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}