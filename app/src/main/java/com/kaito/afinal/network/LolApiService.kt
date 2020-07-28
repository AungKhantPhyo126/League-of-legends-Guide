package com.kaito.afinal.network

/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kaito.afinal.domain.Champions
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private const val BASE_URL = "https://ddragon.leagueoflegends.com/cdn/10.12.1/"
// TODO (02) Use Retrofit Builder with ScalarsConverterFactory and BASE_URL
private val retrofit=Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

// TODO (03) Implement the MarsApiService interface with @GET getProperties returning a String
interface LolApiService{
    @GET("data/en_US/champion.json")
    fun getProperties():
            Deferred<OverViewDto>
    @GET("data/en_US/champion/{championName}.json")
    fun getSpells(@Path("championName")name:String):
            Deferred<OverViewDto>
}

// TODO (04) Create the MarsApi object using Retrofit to implement the MarsApiService
object LolApi{
    val retrofitService:LolApiService by lazy {
        retrofit.create(LolApiService::class.java)
    }
}



