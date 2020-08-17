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






}
