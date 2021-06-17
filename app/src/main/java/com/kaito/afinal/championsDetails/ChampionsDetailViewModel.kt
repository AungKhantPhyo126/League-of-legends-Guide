package com.kaito.afinal.championsDetails

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.database.getDatabase
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.network.LolApi
import com.kaito.afinal.repository.ChampionsRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
class ChampionsDetailViewModel(
    private val name: String,
    private val app: Application
) : AndroidViewModel(app) {

    private val database = getDatabase(app)
    private val apiService = LolApi.getService(app)
    @OptIn(InternalCoroutinesApi::class)
    private val championsRepository = ChampionsRepository(apiService, database)

    @OptIn(InternalCoroutinesApi::class)
    fun toggleFavorite() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(
                app, "Please log in to give favorite!", Toast.LENGTH_LONG
            ).show()
        } else {
            viewModelScope.launch {
                championsRepository.toggleFav(champion.value!!)
            }
        }
    }

    init {
        viewModelScope.launch {
            championsRepository.refreshDetails(name)
        }
    }

    val champion = championsRepository.getChampion(name).distinctUntilChanged()

    val isFavorite = champion.map { it.favorite }.distinctUntilChanged()
}