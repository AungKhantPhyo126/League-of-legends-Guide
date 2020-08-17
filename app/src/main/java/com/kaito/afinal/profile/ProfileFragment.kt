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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
    private val viewModel by viewModels<ProfileViewModel>()

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        binding.tvLogin.setOnClickListener {
            signingProgress()
        }
        binding.ivLogout.setOnClickListener {
            logout()
        }
        binding.tvLogout.setOnClickListener {
            logout()
        }
        binding.tvChangeProfilePic.setOnClickListener {
            selectImage(requireContext())
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        switchLogInView()

        viewModel.getUserData()

        viewModel.profilePhoto.observe(viewLifecycleOwner, Observer {
            bindProfilePicture(binding.ivProfilePic, it)
        })

        viewModel.displayName.observe(viewLifecycleOwner, Observer {
            binding.tvUserName.text = it.toString()
        })
        viewModel.phoneNo.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.tvPhone.isVisible = false
                binding.tvPhoneTitle.isVisible = false
                binding.ivPhone.isVisible=false
            } else {
                binding.tvPhone.isVisible = true
                binding.tvPhone.setText(it.toString())
                binding.tvPhoneTitle.isVisible = true
                binding.ivPhone.isVisible=true
            }
            binding.tvPhone.text = it.toString()
        })

        viewModel.email.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.tvEmailTitle.isVisible = false
                binding.tvEmail.isVisible = false
                binding.ivEmail.isVisible=false
            } else {
                binding.tvEmailTitle.isVisible = true
                binding.tvEmail.isVisible = true
                binding.ivEmail.isVisible=true
                binding.tvEmail.text=it.toString()
            }
            binding.tvEmail.text = it.toString()
        })
    }

    fun logout() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnSuccessListener {
                switchLogInView()
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
    fun signingProgress() {
// Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), 101
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_CANCELED) {
            when (requestCode) {
                101 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    saveUserData()
//                    FirebaseAuth.getInstance().currentUser?.uid?.let {
//                        docRef1.document(it).get().addOnCompleteListener { document ->
//                            if (!document.result.exists()) {
//                                // Successfully signed in
//                                bindUserData()
//                            }
//                        }
//                    }
//        val photo=FirebaseAuth.getInstance().currentUser?.photoUrl
//                    val userdata = hashMapOf(
//                        "UserName" to name,
//                        "email" to email,
//                        "favoriteList" to listOf<String>()
//                    )
//                    docRef.set(userdata, SetOptions.merge())
                }
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    val storageRef = Firebase.storage.reference
                    val userRef = storageRef.child("images/profile.jpg")

                    val iv_profilePic = binding.ivProfilePic
                    iv_profilePic.setImageBitmap(selectedImage)
                    iv_profilePic.isDrawingCacheEnabled = true
                    iv_profilePic.buildDrawingCache()
                    val bitmap = (iv_profilePic.drawable as BitmapDrawable).bitmap
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
            }
        }
    }

    fun switchLogInView(){
        if(FirebaseAuth.getInstance().currentUser==null){
            binding.ivPhone.isVisible=false
            binding.tvPhoneTitle.isVisible=false
            binding.tvPhone.isVisible=false
            binding.tvEmailTitle.isVisible=false
            binding.tvEmail.isVisible=false
            binding.ivEmail.isVisible=false
            binding.tvUserName.isVisible=false
            binding.ivProfilePic.isVisible=false
            binding.ivChangeProfilePic.isVisible=false
            binding.ivLogout.isVisible=false
            binding.tvLogout.isVisible=false
            binding.tvChangeProfilePic.isVisible=false
        }else{
            binding.tvLogin.isVisible=false

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
    fun saveUserData() {
        val userRef =
            db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
        val name = FirebaseAuth.getInstance().currentUser?.displayName
        val email = FirebaseAuth.getInstance().currentUser?.email
        val phone=FirebaseAuth.getInstance().currentUser?.phoneNumber
        val photo=FirebaseAuth.getInstance().currentUser?.photoUrl?.toString() ?: "https://bit.ly/39GLBSV"
        val userdata =
            hashMapOf("photoUrl" to photo,"UserName" to name, "email" to email,"phone" to phone, "favoriteList" to listOf<String>())
        userRef.set(userdata, SetOptions.merge())
    }
}