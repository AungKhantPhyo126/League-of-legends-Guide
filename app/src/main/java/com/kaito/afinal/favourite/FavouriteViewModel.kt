package com.kaito.afinal.favourite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.database.getDatabase
import com.kaito.afinal.network.LolApi
import com.kaito.afinal.repository.ChampionsRepository
import kotlinx.coroutines.launch

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val apiService = LolApi.getService(application)
    private val championsRepository = ChampionsRepository(apiService, database)
    val favoriteChampionsList = championsRepository.getFavoriteChampions()

}