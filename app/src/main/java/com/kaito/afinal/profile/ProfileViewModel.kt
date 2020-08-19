package com.kaito.afinal.profile

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.database.getDatabase
import com.kaito.afinal.repository.ChampionsRepository
import com.kaito.afinal.repository.Profile
import com.kaito.afinal.repository.UserRepository
import java.io.File

class ProfileViewModel(application: Application) :AndroidViewModel(application){

    private val userRepository = UserRepository(application)

    val profile = userRepository.getUserData()

    val isLoggedOut = MutableLiveData<Boolean>(userRepository.isLoggedIn().not())

    fun refreshLoginStatus() {
        isLoggedOut.value = isLoggedIn().not()
    }

    fun uploadImage(file:File) {
        userRepository.uploadImage(file)
    }

    fun login() {
        userRepository.login {
            refreshLoginStatus()
        }
    }

    fun logout() {
        userRepository.logout {
            refreshLoginStatus()
        }
    }

    private fun isLoggedIn() = userRepository.isLoggedIn()
}