package com.kaito.afinal.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.kaito.afinal.R
import com.kaito.afinal.championsDetails.ChampionsDetailViewModel
import com.kaito.afinal.championsDetails.ChampionsDetailViewModelFactory
import com.kaito.afinal.championsviews.ChampionsAdapter
import com.kaito.afinal.championsviews.ChampionsViewFragmentDirections
import com.kaito.afinal.databinding.FragmentChampionsdetailBinding
import com.kaito.afinal.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class FavouriteFragment :Fragment(){
    private lateinit var binding:FragmentFavoriteBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater,container,false)

        val application = requireNotNull(activity).application

        val viewModelFactory = FavouriteViewModelFactory(application)
        val viewModel: FavouriteViewModel by viewModels { viewModelFactory }

        val adapter=ChampionsAdapter(ChampionsAdapter.OnClickListener{champion, clickedImageView ->
            val extras2 = FragmentNavigator.Extras.Builder().addSharedElements(
                mapOf(
                    clickedImageView!! to getString(R.string.transition_name)
                )

            ).build()
            val extras = FragmentNavigatorExtras(
                clickedImageView!! to getString(R.string.transition_name)
            )
            this.findNavController().navigate(
                FavouriteFragmentDirections.actionFavouriteFragmentToChampionsDetailFragment(
                    champion.championsName
                ),
                extras2
            )
        })

        binding.favouriteChampionsList.adapter=adapter
        viewModel.favoriteChampionsList.observe(viewLifecycleOwner, Observer {
            if(it.isEmpty() || it==null){
                binding.noFavorite.setText("You Have No favorite")
            }
            if(it.isNotEmpty()){
                binding.noFavorite.isVisible=false
            }
            binding.viewModel=viewModel
            adapter.submitList(it)
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}