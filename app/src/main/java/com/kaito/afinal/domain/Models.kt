package com.kaito.afinal.domain

import android.os.Parcelable
import com.kaito.afinal.network.Images
import com.kaito.afinal.network.Spell
import kotlinx.android.parcel.Parcelize


data class Champions (
    val championsName:String,
    val title:String,
    val image: String,
    val roles:String,
    val blurb:String,
    val hp:String,
    val mp:String,
    val spell:List<Spell>
)