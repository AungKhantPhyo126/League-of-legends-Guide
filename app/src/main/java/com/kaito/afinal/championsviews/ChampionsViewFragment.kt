package com.kaito.afinal.championsviews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaito.afinal.DarkModeHelper
import com.kaito.afinal.R
import com.kaito.afinal.databinding.FragmentChampionsBinding
import kotlinx.android.synthetic.main.fragment_champions.*


class ChampionsViewFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    val viewModel: ChampionViewModel by viewModels()
    var currentFilter = "All"
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("filter", currentFilter)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter =
            ChampionsAdapter(ChampionsAdapter.OnClickListener { champion, clickedImageView ->
                val extras2 = FragmentNavigator.Extras.Builder().addSharedElements(
                    mapOf(
                        clickedImageView!! to getString(R.string.transition_name)
                    )

                ).build()
                val extras = FragmentNavigatorExtras(
                    clickedImageView!! to getString(R.string.transition_name)
                )
                this.findNavController().navigate(
                    ChampionsViewFragmentDirections.actionChampionsViewFragmentToChampionsDetailFragment(
                        champion.championsName
                    ),

                    extras2
                )
//            viewModel.displayChampionDetail(champion)
            })
        val binding = FragmentChampionsBinding.inflate(inflater, container, false)
        binding.championsList.adapter = adapter
        viewModel.navigateToSelectedChampion.observe(viewLifecycleOwner, Observer {
            if (null != it) {

                viewModel.displayChampionDetailComplete()
            }
        })
        currentFilter = savedInstanceState?.getString("filter") ?: "All"
        viewModel.filter(currentFilter)
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.Fighter -> "Fighter"
                R.id.Tank -> "Tank"
                R.id.Mage -> "Mage"
                R.id.Support -> "Support"
                R.id.Assassin -> "Assassin"
                R.id.Marksman->"Marksman"
                else -> "All"
            }.also {
                viewModel.filter(it)
                currentFilter = it
            }
        }
        viewModel.champions.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        return binding.root
    }
}