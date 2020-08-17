package com.kaito.afinal.championsviews

import android.app.Application
import androidx.lifecycle.*
import com.kaito.afinal.database.getDatabase
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.network.NetworkChampions
import com.kaito.afinal.network.LolApi
import com.kaito.afinal.repository.ChampionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChampionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val championsRepository = ChampionsRepository(database)

    private val roleLive = MutableLiveData<String>("All")

    val champions = roleLive.distinctUntilChanged()
        .switchMap { championsRepository.getChampions(it) }

    init {
        viewModelScope.launch {
            championsRepository.fetchChampions()
        }
    }

    fun filter(role: String) {
        roleLive.value = role
    }
}

