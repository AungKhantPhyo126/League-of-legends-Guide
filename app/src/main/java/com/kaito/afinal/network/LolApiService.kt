package com.kaito.afinal.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private const val BASE_URL = "https://ddragon.leagueoflegends.com/cdn/10.12.1/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface LolApiService {

    @GET("data/en_US/champion.json")
    suspend fun getProperties(): OverViewDto

    @GET("data/en_US/champion/{championName}.json")
    suspend fun getSpells(@Path("championName") name: String): OverViewDto
}

object LolApi {
    val retrofitService: LolApiService by lazy {
        retrofit.create(LolApiService::class.java)
    }
}



