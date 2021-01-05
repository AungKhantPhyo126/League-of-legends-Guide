package com.kaito.afinal.championsDetails

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kaito.afinal.databinding.FragmentChampionsdetailBinding

class ChampionsDetailFragment : Fragment() {
    private lateinit var binding: FragmentChampionsdetailBinding
    lateinit var name: String
    lateinit var viewModelFactory: ChampionsDetailViewModelFactory
    private val viewModel: ChampionsDetailViewModel by viewModels { viewModelFactory }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        name = ChampionsDetailFragmentArgs.fromBundle(requireArguments()).selectedChampion
        viewModelFactory =
            ChampionsDetailViewModelFactory(name, context.applicationContext as Application)
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

        val spellAdapter = SpellAdapter()
        binding.rvSpells.adapter = spellAdapter

        binding.animationView?.setMinAndMaxFrame(10, 60)

//        binding.animationView?.resumeAnimation()

        viewModel.champion.observe(viewLifecycleOwner, Observer {
            spellAdapter.submitList(it.spell)
        })

        var hasPlayed = false

        viewModel.isFavorite.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.animationView?.speed = 1f
                binding.animationView?.playAnimation()
                hasPlayed = true
            } else {
                if (hasPlayed) {
                    binding.animationView?.speed = -1f
                    binding.animationView?.resumeAnimation()
                    hasPlayed = true
                }
            }
        })


        binding.animationView?.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }
}