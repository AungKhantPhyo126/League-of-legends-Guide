package com.kaito.afinal.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI

import com.kaito.afinal.databinding.FragmentProfileBinding
import com.kaito.afinal.repository.Profile

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProfileBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogin.setOnClickListener {
            startSignInProcess()
        }

        binding.btnLogin.setOnClickListener {
            startSignInProcess()
        }

        binding.ivLogout.setOnClickListener {
            viewModel.logout()
        }
        binding.tvLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.tvChangeProfilePic.setOnClickListener {
            selectImage(requireContext())
        }

        viewModel.isLoggedOut.observe(viewLifecycleOwner) {
            binding.gpLogout.isVisible = it
            binding.gpLogin.isVisible = it.not()
        }

        viewModel.profile.observe(viewLifecycleOwner) {
            bindUserInfo(it)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshLoginStatus()
    }

    private fun bindUserInfo(profile: Profile) {
        Glide.with(requireContext())
            .load(profile.photoUrl)
            .circleCrop()
            .into(binding.ivProfilePic)

        binding.tvUserName.text = profile.name
        binding.tvPhone.text = profile.phone
        binding.tvEmail.text = profile.email
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_CANCELED) {
            when (requestCode) {
                101 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    viewModel.login()
                }
                200, 201 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras?.get("data") as Bitmap?
                    selectedImage?.also {
                        with(binding.ivProfilePic) {
                            setImageBitmap(it)
                            isDrawingCacheEnabled = true
                            buildDrawingCache()
                            (drawable as? BitmapDrawable)?.bitmap?.let {
                                viewModel.uploadImage(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startSignInProcess() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), 101
        )
    }

    private fun selectImage(context: Context) {
        val options =
            arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePicture =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 200)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 201)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }
}