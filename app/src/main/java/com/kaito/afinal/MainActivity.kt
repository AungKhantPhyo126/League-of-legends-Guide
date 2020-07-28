package com.kaito.afinal

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.ktx.storage
import com.kaito.afinal.championsviews.ChampionsViewFragment
import com.kaito.afinal.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.fragment_championsdetail.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private val docRef =
        db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")

    override fun onStart() {
        super.onStart()
        signingProgress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment)


        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

//        fun selectImage(context: Context) {
//            val options =
//                arrayOf("Take Photo", "Choose from Gallery", "Cancel")
//            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
//            builder.setTitle("Choose your profile picture")
//            builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
//                if (options[item] == "Take Photo") {
//                    val takePicture =
//                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(takePicture, 0)
//                } else if (options[item] == "Choose from Gallery") {
//                    val pickPhoto = Intent(
//                        Intent.ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                    )
//                    startActivityForResult(pickPhoto, 1)
//                } else if (options[item] == "Cancel") {
//                    dialog.dismiss()
//                }
//            })
//            builder.show()
//        }




        val switch=binding.switchOne
        switch.isChecked = DarkModeHelper.getInstance(applicationContext).isDark()
        switch.setOnClickListener {
            DarkModeHelper.getInstance(applicationContext).toggleDark()
            recreate()
        }

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {

            minimumFetchIntervalInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(mapOf("darkmode" to false, "categoryChange" to false))

        remoteConfig.fetchAndActivate()
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    switch.isVisible = remoteConfig.getBoolean("darkmode")

                    Toast.makeText(
                        this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("tag1", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                task.result?.apply {
                    Log.i("MainActivity", "token:${this.token}")
                }

                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d("tag2", msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }

    fun signingProgress() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

// Create and launch sign-in intent
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), 101
            )
        } else {
//            bindUserData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                101 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val user = FirebaseAuth.getInstance().currentUser
//                    val leeUser = hashMapOf("photo" to (user?.photoUrl ?: ""))
                    bindUserData()
                }
//                0 -> if (resultCode == RESULT_OK && data != null) {
//                    val selectedImage = data.extras!!["data"] as Bitmap?
//                    val storageRef = Firebase.storage.reference
//                    val userRef = storageRef.child("images/profile.jpg")
//                    val navView = binding.navView
//                    val headerView = navView.getHeaderView(0)
//                    val iv_profilePic=headerView.profile_pic
//                    iv_profilePic.setImageBitmap(selectedImage)
//                    iv_profilePic.isDrawingCacheEnabled = true
//                    iv_profilePic.buildDrawingCache()
//                    val bitmap = (iv_profilePic.drawable as BitmapDrawable).bitmap
//                    val baos = ByteArrayOutputStream()
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                    val data = baos.toByteArray()
//                    var uploadTask = userRef.putBytes(data)
//                    uploadTask.addOnSuccessListener {
//                        val uri=userRef.downloadUrl.addOnSuccessListener {
//                            val photo= hashMapOf("photoUrl" to it.toString())
//                            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
//                                .set(photo, SetOptions.merge())
//                                .addOnSuccessListener { Log.i("database","success") }
//                                .addOnFailureListener {  Log.i("database","fail")  }
//                            Log.i("uploadSuccess", "${userRef.downloadUrl}")
//                        }
//                            .addOnFailureListener {  Log.i("database","FailP Hayyyy")  }
//                    }
//                    uploadTask.addOnFailureListener {
//                        Log.e("uploadError", "Upload Failed")
//                    }
//                }
//                1 -> if (resultCode == RESULT_OK && data != null) {
//                    val selectedImage: Uri? = data.data
//                    val filePathColumn =
//                        arrayOf(MediaStore.Images.Media.DATA)
//                    if (selectedImage != null) {
//                        val cursor: Cursor? = getContentResolver().query(
//                            selectedImage,
//                            filePathColumn, null, null, null
//                        )
//                        if (cursor != null) {
//                            cursor.moveToFirst()
//                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
//                            val picturePath: String = cursor.getString(columnIndex)
//                            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
//                            cursor.close()
//                        }
//                    }
//                }
            }
        }
    }

    fun bindUserData() {
        val name = FirebaseAuth.getInstance().currentUser?.displayName
        val email = FirebaseAuth.getInstance().currentUser?.email
//        val photo=FirebaseAuth.getInstance().currentUser?.photoUrl
        val userdata =
            hashMapOf("UserName" to name, "email" to email, "favoriteList" to listOf<String>())
        docRef.set(userdata, SetOptions.merge())
    }
}
