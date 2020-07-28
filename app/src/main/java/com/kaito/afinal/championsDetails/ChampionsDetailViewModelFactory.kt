package com.kaito.afinal.championsDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaito.afinal.domain.Champions

class ChampionsDetailViewModelFactory(
    private val name: String,
    private val application: Application

) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChampionsDetailViewModel::class.java)) {
            return ChampionsDetailViewModel(name,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}