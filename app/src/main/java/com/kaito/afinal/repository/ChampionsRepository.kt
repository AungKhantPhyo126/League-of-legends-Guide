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

    private val db = Firebase.firestore
    private val rootRef = db.collection("lol_database")
    private val userRef = rootRef.document("${FirebaseAuth.getInstance().currentUser?.uid}")


    private var favoriteNames = mutableListOf<String>()
    private var championlist = mutableListOf<DatabaseChampions>()


    suspend fun fetchChampions() {
        withContext(Dispatchers.IO) {
            try {

                val champions = LolApi.retrofitService.getProperties().asDatabase()
                userRef.get().addOnSuccessListener { snapshot ->

                    if (snapshot != null && snapshot.exists()) {
                        snapshot.data?.get("favoriteList")?.let {
                            it as? List<String>
                        }?.also { favoriteNames ->
                            GlobalScope.launch(Dispatchers.IO) {
                                champions.map {
                                    it.copy(favorite = favoriteNames.contains(it.championsName))
                                }.also {
                                    database.championsDao.insertAll(it)
                                }
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.e("fetchFail", e.message)
            }
        }
    }

    fun getChampions(roles: String) = database.championsDao.getFilteredChampions("%$roles%")
        .map { it.asDomainModel() }


    suspend fun refreshDetails(name: String) {
        withContext(Dispatchers.IO) {
            try {
                val championDetail = LolApi.retrofitService.getSpells(name).asDatabase()[0]
                userRef.get().addOnSuccessListener { snapshot ->

                    if (snapshot != null && snapshot.exists()) {
                        snapshot.data?.get("favoriteList")?.let {
                            it as? List<String>
                        }?.also { favoriteNames ->
                            GlobalScope.launch(Dispatchers.IO) {
                                championDetail.copy(favorite = favoriteNames.contains(championDetail.championsName))
                                    .also {
                                        database.championsDao.insertChampion(it)
                                    }
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.e("lee", e.message)
            }
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

    fun addfav(name: String) {
        val docRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        docRef.update(
            "favoriteList",
            FieldValue.arrayUnion(name)
        )
    }

    fun removefav(name: String) {
        val docRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        docRef.update(
            "favoriteList",
            FieldValue.arrayRemove(name)
        )
    }

}