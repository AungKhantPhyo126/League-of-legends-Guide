package com.kaito.afinal.favourite

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaito.afinal.championsDetails.ChampionsDetailViewModel

class FavouriteViewModelFactory(private val application: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            return FavouriteViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

