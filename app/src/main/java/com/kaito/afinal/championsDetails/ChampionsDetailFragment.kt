package com.kaito.afinal.championsDetails

import android.content.ContentValues
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.R
import com.kaito.afinal.databinding.FragmentChampionsdetailBinding
import com.kaito.afinal.network.Spell
import kotlinx.android.synthetic.main.fragment_championsdetail.*
import kotlinx.android.synthetic.main.fragment_championsdetail.view.*
import kotlinx.android.synthetic.main.fragment_championsdetail.view.animationView

class ChampionsDetailFragment : Fragment() {
    private lateinit var binding: FragmentChampionsdetailBinding
    private val db = Firebase.firestore
    private val docRef =
        db.collection("lol_database").document("${FirebaseAuth.getInstance().currentUser?.uid}")
    private var isCOntainInList = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChampionsdetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 750
        }
        @Suppress("UNUSED_VARIABLE")
        val application = requireNotNull(activity).application
        val animationView = binding.animationView


        binding.setLifecycleOwner(this)
        val name = ChampionsDetailFragmentArgs.fromBundle(requireArguments()).selectedChampion
        val viewModelFactory = ChampionsDetailViewModelFactory(name, application)
        val viewModel: ChampionsDetailViewModel by viewModels { viewModelFactory }
        viewModel.isFavorite.observe(viewLifecycleOwner, Observer {
            if (it) {
                animationView?.setMinAndMaxFrame(45, 45)
//                animationView?.playAnimation()
            } else {
                animationView?.setMinAndMaxFrame(10, 60)
            }
//            binding.animationView?.setImageResource(if (it){
//                R.drawable.ic_baseline_favorite_24
//            }else{
//                R.drawable.ic_baseline_favorite_border_24
//            })
        })
        val spellAdapter = SpellAdapter()
        binding.rvSpells.adapter = spellAdapter
        viewModel.champion.observe(viewLifecycleOwner, Observer {
            spellAdapter.submitList(it.spell)
        })
        binding.viewModel = viewModel
        animationView?.setOnClickListener {
            toogleFavorite()
        }
//        if (isCOntainInList==true){
//            binding.ivFavourite?.setImageResource(R.drawable.ic_baseline_favorite_24)
//        }else{
//            binding.ivFavourite?.setImageResource(R.drawable.ic_baseline_favorite_border_24)
//        }


        binding.executePendingBindings()
//            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    fun toogleFavorite() {
        docRef.get().addOnSuccessListener { document ->
            val favHeroes = document.data?.get("favoriteList")
            favHeroes as List<String>
            isCOntainInList = favHeroes.contains(binding.champName.text.toString())
            if (isCOntainInList == false) {
                animationView?.setMinAndMaxFrame(0, 45)
                animationView?.speed = 1f
                animationView?.playAnimation()
                docRef.update(
                    "favoriteList",
                    FieldValue.arrayUnion(binding.champName.text.toString())
                )
            } else {
                animationView?.speed = -1f
                animationView?.resumeAnimation()
                animationView?.setMinAndMaxFrame(10, 60)
                docRef.update(
                    "favoriteList",
                    FieldValue.arrayRemove(binding.champName.text.toString())
                )
            }
        }
    }

}