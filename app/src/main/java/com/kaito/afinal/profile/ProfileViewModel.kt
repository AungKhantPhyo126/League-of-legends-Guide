package com.kaito.afinal.profile

import android.app.Application
import android.content.ContentValues.TAG
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

class ProfileViewModel(application: Application) :AndroidViewModel(application){
    private val _profilePhoto = MutableLiveData<String>()
    val profilePhoto: LiveData<String>
        get() = _profilePhoto

    private val _displayName = MutableLiveData<String>()
    val displayName: LiveData<String>
        get() = _displayName

    private val _phoneNo = MutableLiveData<String>()
    val phoneNo: LiveData<String>
        get() = _phoneNo

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?>
        get() = _email
    fun getUserData() {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            Firebase.firestore.collection("lol_database").document(it).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.i(TAG, "Current data: ${snapshot.data}")
                    _profilePhoto.value = snapshot.data?.get("photoUrl").toString()
                    _displayName.value = snapshot.data?.get("phone").toString() ?: snapshot.data?.get("UserName").toString()
                    _email.value = snapshot.data?.get("email").toString()
                    _phoneNo.value=snapshot.data?.get("phone").toString()
                } else {
                    Log.i(TAG, "Current data: null")
                }
            }
        }
    }
}