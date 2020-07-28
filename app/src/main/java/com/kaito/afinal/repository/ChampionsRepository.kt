package com.kaito.afinal.repository

import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.database.ChampionsDatabase
import com.kaito.afinal.database.DatabaseChampions
import com.kaito.afinal.database.asDomainModel
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.network.LolApi
import com.kaito.afinal.network.asDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChampionsRepository(private val database: ChampionsDatabase) {
    val db = Firebase.firestore
    val champions: LiveData<List<Champions>> =
        Transformations.map(database.championsDao.getChampions()) {
            it.asDomainModel()
        }


    suspend fun refreshDetails(name: String) {
        withContext(Dispatchers.IO) {
            try {
                val championDetail = LolApi.retrofitService.getSpells(name).await().let {
                    it.asDatabase()
                }
                database.championsDao.insertChampion(championDetail[0])
            } catch (e: Throwable) {
                Log.e("lee", e.message)
            }
        }
    }

    fun getChampion(name: String): LiveData<Champions> {
        return database.championsDao.getChampion(name).let {
            Transformations.map(it, {
                it.asDomainModel()
            })
        }
    }

    fun getFavoriteChampions(): MutableLiveData<List<Champions>> {
        val favoriteList = MutableLiveData<List<Champions>>()
        val docRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        docRef.addSnapshotListener { snapshot, e ->

            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                GlobalScope.launch {
                    val list = snapshot.data?.get("favoriteList") as List<String>
                    list.map {
                        database.championsDao.getFavoriteByName(it).let { entity ->
                            entity.asDomainModel()
                        }
                    }.also {
                        favoriteList.postValue(it)
                    }
                }
            }else{}
            }
//        docRef.get().addOnSuccessListener {document->
//            GlobalScope.launch {
//                val list=document.data?.get("favoriteList") as List<String>
//                list.map {
//                    database.championsDao.getFavoriteByName(it).let {entity->
//                        entity.asDomainModel()
//                    }
//                }.also {
//                    favoriteList.postValue(it)
//                }
//            }
////        }
//            .addOnFailureListener {  }
            return favoriteList
        }


        suspend fun refreshChampions() {
            withContext(Dispatchers.IO) {
                try {
                    val championlist = LolApi.retrofitService.getProperties().await().let {
                        it.asDatabase()
                    }
                    database.championsDao.insertAll(championlist)
                } catch (e: Throwable) {
                    Log.e("lee", e.message)
                }
            }
        }

        fun filterChanged(roles: String): LiveData<List<Champions>> {
            return database.championsDao.getFilteredChampions("%$roles%").let {
                Transformations.map(it, {
                    it.asDomainModel()
                })
            }
        }
    }