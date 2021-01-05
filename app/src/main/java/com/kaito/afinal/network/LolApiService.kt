package com.kaito.afinal.network

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private const val BASE_URL = "https://ddragon.leagueoflegends.com/cdn/10.12.1/"

private fun getRetrofit(context: Context) = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(OkHttpClient.Builder().apply {
        addInterceptor(ChuckInterceptor(context))
    }.build())
    .build()

interface LolApiService {

    @GET("data/en_US/champion.json")
    suspend fun getProperties(): OverViewDto

    @GET("data/en_US/champion/{championName}.json")
    suspend fun getSpells(@Path("championName") name: String): OverViewDto
}

object LolApi {

    fun getService(context: Context) = getRetrofit(context).create(LolApiService::class.java)
}



