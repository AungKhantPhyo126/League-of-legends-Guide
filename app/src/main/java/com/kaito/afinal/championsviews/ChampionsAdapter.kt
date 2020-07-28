package com.kaito.afinal.championsviews

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaito.afinal.databinding.ListItemsChampionsBinding
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.network.NetworkChampions
import com.kaito.afinal.network.Spell
object ChampionDiffCallback: DiffUtil.ItemCallback<Champions>(){
    override fun areItemsTheSame(oldItem: Champions, newItem: Champions): Boolean {
        return oldItem.championsName==newItem.championsName

    }

    override fun areContentsTheSame(oldItem: Champions, newItem: Champions): Boolean {
        return oldItem == newItem
    }

}

class ChampionsAdapter(val onClickListener: OnClickListener) : ListAdapter<Champions,ChampionsAdapter.ItemViewHolder>(ChampionDiffCallback){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
       val layoutInflater= LayoutInflater.from(parent.context)
        val binding=ListItemsChampionsBinding.inflate(layoutInflater,parent,false)
        return ItemViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item=getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item,holder.binding.imageView2)
        }
        holder.bind(item)


    }


    class ItemViewHolder( val binding: ListItemsChampionsBinding): RecyclerView.ViewHolder(binding.root) {
       fun bind(champions:Champions){
            binding.champions=champions
           binding.executePendingBindings()
       }
    }
    class OnClickListener(val clickListener: (champion: Champions,imageView:ImageView) -> Unit) {
        fun onClick(champion: Champions,imageView:ImageView) = clickListener(champion,imageView)
    }
}
