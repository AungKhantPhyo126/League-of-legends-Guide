package com.kaito.afinal.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.database.ChampionsDatabase
import com.kaito.afinal.database.DatabaseChampions
import com.kaito.afinal.database.asDomainModel
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.network.LolApi
import com.kaito.afinal.network.LolApiService
import com.kaito.afinal.network.asDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChampionsRepository(
    private val retrofitService: LolApiService,
    private val database: ChampionsDatabase
) {

    private val db = Firebase.firestore
    private val rootRef = db.collection("lol_database")
    private val userRef = rootRef.document("${FirebaseAuth.getInstance().currentUser?.uid}")

    suspend fun fetchChampions() {
        withContext(Dispatchers.IO) {
            try {
                val champions = retrofitService.getProperties().asDatabase()
                database.championsDao.insertAll(champions)
            } catch (e: Throwable) {
                Log.e("fetchFail", e.message.orEmpty())
            }
        }
    }

    fun getChampions(roles: String) = database.championsDao.getChampions("%$roles%")
        .map { it.asDomainModel() }


    suspend fun refreshDetails(name: String) {
        withContext(Dispatchers.IO) {
            try {
                val new = retrofitService.getSpells(name).asDatabase()[0]
                val old = database.championsDao.getChampion(name)
                val merged = new.copy(favorite = old.favorite)
                database.championsDao.insert(merged)
            } catch (e: Throwable) {
                Log.e("lee", e.message.orEmpty())
            }
        }

    }

    fun getChampion(name: String) = database.championsDao.getChampionLive(name).map {
        it.asDomainModel()
    }

    fun getFavoriteChampions(): LiveData<List<Champions>> {
        return database.championsDao.getFavoriteChampions()
            .map { it.asDomainModel() }
    }

    suspend fun toggleFav(champions: Champions) {
        if (champions.favorite) {
            removefav(champions.championsName)
        } else {
            addfav(champions.championsName)
        }
    }

    private suspend fun addfav(name: String) {
        userRef.update("favoriteList", FieldValue.arrayUnion(name)).await()
        database.championsDao.toggleFavorite(name, true)
    }

    private suspend fun removefav(name: String) {
        userRef.update("favoriteList", FieldValue.arrayRemove(name)).await()
        database.championsDao.toggleFavorite(name, false)
    }

    suspend fun <T> Task<T>.await() = suspendCoroutine<T> { t ->
        addOnSuccessListener {
            t.resume(it)
        }
        addOnFailureListener {
            t.resumeWithException(it)
        }
    }


}