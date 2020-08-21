package com.kaito.afinal.championsDetails

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionInflater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.R
import com.kaito.afinal.databinding.FragmentChampionsdetailBinding
import com.kaito.afinal.network.Spell
import com.kaito.afinal.profile.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_championsdetail.*
import kotlinx.android.synthetic.main.fragment_championsdetail.view.*
import kotlinx.android.synthetic.main.fragment_championsdetail.view.animationView

class ChampionsDetailFragment : Fragment() {
    private lateinit var binding: FragmentChampionsdetailBinding
    lateinit var name:String
    lateinit var viewModelFactory :ChampionsDetailViewModelFactory
    private val viewModel: ChampionsDetailViewModel by viewModels { viewModelFactory }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        name  = ChampionsDetailFragmentArgs.fromBundle(requireArguments()).selectedChampion
        viewModelFactory= ChampionsDetailViewModelFactory(name, context.applicationContext as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChampionsdetailBinding.inflate(inflater)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNUSED_VARIABLE")
        val animationView = binding.animationView

        val spellAdapter = SpellAdapter()
        binding.rvSpells.adapter = spellAdapter
        animationView?.setMinAndMaxFrame(10, 60)

        viewModel.champion.observe(viewLifecycleOwner, Observer {
            if (it.favorite==true){
                animationView?.speed = 1f
                animationView?.playAnimation()
            }else if (it.favorite==false){
                animationView?.speed = -1f
                animationView?.resumeAnimation()
            }
//            if (it.favorite) {
//                animationView?.setMinAndMaxFrame(45, 45)
////                animationView?.playAnimation()
//            } else {
//                animationView?.setMinAndMaxFrame(10, 60)
//            }
            spellAdapter.submitList(it.spell)
        })


        animationView?.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }
}