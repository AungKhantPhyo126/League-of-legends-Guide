package com.kaito.afinal.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class UserRepository(
    private val context: Context
) {

    private val db = Firebase.firestore

    fun isLoggedIn() = FirebaseAuth.getInstance().currentUser != null

    fun login(op: () -> Unit) {
        val userRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        val name = FirebaseAuth.getInstance().currentUser?.displayName
        val email = FirebaseAuth.getInstance().currentUser?.email
        val phone = FirebaseAuth.getInstance().currentUser?.phoneNumber
        val photo =
            FirebaseAuth.getInstance().currentUser?.photoUrl?.toString() ?: "https://bit.ly/39GLBSV"
        val userdata =
            hashMapOf(
                "photoUrl" to photo,
                "name" to name,
                "email" to email,
                "phone" to phone,
                "favoriteList" to listOf<String>()
            )
        userRef.set(userdata, SetOptions.merge()).addOnSuccessListener { op() }
    }

    fun logout(op: () -> Unit) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener { op() }
            .addOnFailureListener {
                Log.i("logout", "fail tl")
            }
    }

    fun uploadImage(bitmap: Bitmap) {
        val storageRef = Firebase.storage.reference
        val userRef = storageRef.child("images/profile.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        var uploadTask = userRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            val uri = userRef.downloadUrl.addOnSuccessListener {
                val photo = hashMapOf("photoUrl" to it.toString())
                db.collection("lol_database")
                    .document("${FirebaseAuth.getInstance().currentUser?.uid}")
                    .set(photo, SetOptions.merge())
                    .addOnSuccessListener { Log.i("database", "success") }
                    .addOnFailureListener { Log.i("database", "fail") }
                Log.i("uploadSuccess", "${userRef.downloadUrl}")
            }
                .addOnFailureListener { Log.i("database", "FailP Hayyyy") }
        }
        uploadTask.addOnFailureListener {
            Log.e("uploadError", "Upload Failed")
        }
    }

    fun getUserData(): LiveData<Profile> {
        val profileLive = MutableLiveData<Profile>()
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            Firebase.firestore.collection("lol_database")
                .document(it)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.i(ContentValues.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        profileLive.value = snapshot.toObject<Profile>()
                    } else {
                        Log.i(ContentValues.TAG, "Current data: null")
                    }
                }
        }
        return profileLive
    }
}

data class Profile(
    val photoUrl: String? = null,
    val name: String = "",
    val phone: String? = null,
    val email: String? = null
)