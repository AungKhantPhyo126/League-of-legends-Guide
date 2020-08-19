package com.kaito.afinal.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.database.ChampionsDatabase
import com.kaito.afinal.database.asDomainModel
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.network.LolApi
import com.kaito.afinal.network.asDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChampionsRepository(private val database: ChampionsDatabase) {

    private val db = Firebase.firestore

    suspend fun fetchChampions() {
        withContext(Dispatchers.IO) {
            try {
                val champions = LolApi.retrofitService.getProperties().asDatabase()
                database.championsDao.insertAll(champions)
            } catch (e: Throwable) {
                Log.e("lee", e.message)
            }
        }
    }

    fun getChampions(roles: String) = database.championsDao.getFilteredChampions("%$roles%")
        .map { it.asDomainModel() }











    suspend fun refreshDetails(name: String) {
        try {
            val championDetail = LolApi.retrofitService.getSpells(name).asDatabase()
            database.championsDao.insertChampion(championDetail[0])
        } catch (e: Throwable) {
            Log.e("lee", e.message)
        }
    }

    fun getChampion(name: String) = database.championsDao.getChampion(name).map {
        it.asDomainModel()
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
            } else {
            }
        }
        return favoriteList
    }

    fun addfav(name:String){
        val docRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        docRef.update(
                    "favoriteList",
                    FieldValue.arrayUnion(name)
                )
    }

    fun removefav(name: String){
        val docRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        docRef.update(
                    "favoriteList",
                    FieldValue.arrayRemove(name)
                )
    }

    fun isfav(keyName:String):MutableLiveData<Boolean>{
        val _isFavorite = MutableLiveData<Boolean>()
        val docRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        docRef.get().addOnSuccessListener { document ->
            val favHeroes = document.data?.get("favoriteList")
            favHeroes as List<String>
            if (favHeroes.contains(keyName)) {
                _isFavorite.value = true
            } else {
                _isFavorite.value = false
            }

        }
        return _isFavorite
    }
}