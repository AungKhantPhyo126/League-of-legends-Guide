package com.kaito.afinal

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kaito.afinal.network.Images

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val url="http://ddragon.leagueoflegends.com/cdn/10.12.1/img/champion/${it}"
//        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context).load(url)
            .into(imgView)
    }
}
@BindingAdapter("detailImageUrl")
fun bindDetailImage(imgView: ImageView,imgUrl: String?){
    imgUrl?.let {
        val newurl=it.replace(".png","_0.jpg")
        val url="http://ddragon.leagueoflegends.com/cdn/img/champion/splash/${newurl}"
        Glide.with(imgView.context).load(url)
            .into(imgView)
    }
}

@BindingAdapter("spellWImageUrl")
fun bindSpellWImage(imgView: ImageView,imgUrl: String?){
    imgUrl?.let {
        val newurl=it.replace(".","W.")
        val url="http://ddragon.leagueoflegends.com/cdn/10.12.1/img/spell/${newurl}"
        Glide.with(imgView.context).load(url)
            .into(imgView)
    }
}
@BindingAdapter("spellEImageUrl")
fun bindSpellEImage(imgView: ImageView,imgUrl: String?){
    imgUrl?.let {
        val newurl=it.replace(".","E.")
        val url="http://ddragon.leagueoflegends.com/cdn/10.12.1/img/spell/${newurl}"
        Glide.with(imgView.context).load(url)
            .into(imgView)
    }
}
@BindingAdapter("spellRImageUrl")
fun bindSpellRImage(imgView: ImageView,imgUrl: String?){
    imgUrl?.let {
        val newurl=it.replace(".","R.")
        val url="http://ddragon.leagueoflegends.com/cdn/10.12.1/img/spell/${newurl}"
        Glide.with(imgView.context).load(url)
            .into(imgView)
    }
}

@BindingAdapter("lolAPiStatus")
fun bindStatus(statusImageView: ImageView, status: LolAPIStatus?) {
    when (status) {
        LolAPIStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        LolAPIStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        LolAPIStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
enum class LolAPIStatus{LOADING,ERROR,DONE}