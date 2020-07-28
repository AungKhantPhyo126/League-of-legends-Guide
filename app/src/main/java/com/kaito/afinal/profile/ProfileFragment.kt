package com.kaito.afinal.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaito.afinal.MainActivity
import com.kaito.afinal.databinding.FragmentProfileBinding
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val db = Firebase.firestore
    private val docRef =
        db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        val user = FirebaseAuth.getInstance().currentUser

        if (user?.displayName != null) {
            binding.tvUserName.setText(user.displayName)
        }
        if(user?.displayName==null && user?.phoneNumber!=null){
            binding.tvUserName.setText(user.phoneNumber)
        }
        if (user?.email != null) {
            binding.tvEmail.setText(user.email)
        }
        if (user?.phoneNumber!=null){
            binding.tvPhone.setText(user.phoneNumber)
        }
        if (user?.photoUrl==null) {
            bindProfilePicture(
                binding.ivProfilePic,
                "https://www.thepeakid.com/wp-content/uploads/2016/03/default-profile-picture.jpg"
            )
        }else{}
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                bindProfilePicture(binding.ivProfilePic, snapshot.data?.get("photoUrl").toString())

            } else {

            }
        }
        binding.tvLogout.setOnClickListener {
            logout()
        }
        binding.ivLogout.setOnClickListener {
            logout()
        }
        binding.ivChangeProfilePic.setOnClickListener {
            selectImage(requireContext())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun logout() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnSuccessListener {
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
                this.findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToChampionsViewFragment()
                )
            }
            .addOnFailureListener {
                Log.i("logout", "fail tl")
            }
    }

    fun bindProfilePicture(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            Glide.with(imgView.context).load(imgUrl)
                .circleCrop()
                .into(imgView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_CANCELED) {
            when (requestCode) {
                101 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val name=FirebaseAuth.getInstance().currentUser?.displayName
                    val email=FirebaseAuth.getInstance().currentUser?.email
//        val photo=FirebaseAuth.getInstance().currentUser?.photoUrl
                    val userdata= hashMapOf("UserName" to name,"email" to email,"favoriteList" to listOf<String>())
                    docRef.set(userdata, SetOptions.merge())
//                    val leeUser = hashMapOf("photo" to (user?.photoUrl ?: ""))
                }
                0-> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    val storageRef = Firebase.storage.reference
                    val userRef = storageRef.child("images/profile.jpg")

                    val iv_profilePic=binding.ivProfilePic
                    iv_profilePic.setImageBitmap(selectedImage)
                    iv_profilePic.isDrawingCacheEnabled = true
                    iv_profilePic.buildDrawingCache()
                    val bitmap = (iv_profilePic.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    var uploadTask = userRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        val uri=userRef.downloadUrl.addOnSuccessListener {
                            val photo= hashMapOf("photoUrl" to it.toString())
                            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
                                .set(photo, SetOptions.merge())
                                .addOnSuccessListener { Log.i("database","success") }
                                .addOnFailureListener {  Log.i("database","fail")  }
                            Log.i("uploadSuccess", "${userRef.downloadUrl}")
                        }
                            .addOnFailureListener {  Log.i("database","FailP Hayyyy")  }
                    }
                    uploadTask.addOnFailureListener {
                        Log.e("uploadError", "Upload Failed")
                    }
                }
            }
        }
    }

    fun selectImage(context: Context) {
        val options =
            arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePicture =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }
//    fun setName(){
//        docRef.get().addOnSuccessListener {
//            val name=it.data?.get("UserName")
//            if (name==null){
//                binding.tvUserName.setText(user)
//            }
//        }
//    }
}