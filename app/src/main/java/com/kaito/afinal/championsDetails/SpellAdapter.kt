package com.kaito.afinal.championsDetails

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaito.afinal.databinding.ItemSpellBinding
import com.kaito.afinal.network.Spell

object SpellDiffCallback:DiffUtil.ItemCallback<Spell>(){
    override fun areItemsTheSame(oldItem: Spell, newItem: Spell): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Spell, newItem: Spell): Boolean {
        return oldItem == newItem
    }
}

class SpellAdapter :ListAdapter<Spell,SpellAdapter.SpellViewHolder>(SpellDiffCallback){
    class SpellViewHolder(private val binding:ItemSpellBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(spell:Spell){
            val url="http://ddragon.leagueoflegends.com/cdn/10.12.1/img/spell/${spell.id}.png"
            Glide.with(itemView.context).load(url)
                .into(binding.spellOne)
            binding.tvTitle.text=spell.name
            binding.tvDesc.text= Html.fromHtml(spell.description, HtmlCompat.FROM_HTML_MODE_LEGACY);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellViewHolder {
       return ItemSpellBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ).let {
             SpellViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: SpellViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}