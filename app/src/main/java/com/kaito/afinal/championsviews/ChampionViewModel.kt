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

    private val _navigateToSelectedChampion=MutableLiveData<Champions>()
    val navigateToSelectedChampion:LiveData<Champions>
    get() = _navigateToSelectedChampion

    fun displayChampionDetail(champion: Champions) {
        _navigateToSelectedChampion.value = champion
    }
    fun displayChampionDetailComplete() {
        _navigateToSelectedChampion.value = null
    }

    private val roleLive= MutableLiveData<String>()
    val champions= roleLive.distinctUntilChanged()
        .switchMap { championsRepository.filterChanged(it) }
    init {
        viewModelScope.launch {
            championsRepository.refreshChampions()
        }
    }

    fun filter(role: String) {
        roleLive.value=role
    }
}

