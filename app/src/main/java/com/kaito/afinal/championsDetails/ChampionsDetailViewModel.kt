package com.kaito.afinal.championsDetails

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.database.getDatabase
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.repository.ChampionsRepository
import kotlinx.coroutines.launch

class ChampionsDetailViewModel(name: String, application: Application) :
    AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val championsRepository = ChampionsRepository(database)
    private val db = Firebase.firestore
    val docRef =
        db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
    val keyName = name


    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    init {
        viewModelScope.launch {
            championsRepository.refreshDetails(name)
            isfav()
        }
    }

    val champion = championsRepository.getChampion(name)
    fun isfav() {
        docRef.get().addOnSuccessListener { document ->
            val favHeroes = document.data?.get("favoriteList")
                favHeroes as List<String>
                if (favHeroes.contains(keyName)) {
                    _isFavorite.value = true
                } else {
                    _isFavorite.value = false
                }

        }
    }

}