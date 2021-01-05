package com.kaito.afinal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaito.afinal.domain.Champions
import com.kaito.afinal.network.NetworkChampions
import com.kaito.afinal.network.Images
import com.kaito.afinal.network.Spell
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity
data class DatabaseChampions constructor(
    @PrimaryKey
    val championsName:String,
    val title:String,
    val image: String,
    val blurb:String,
    val roles:String,
    val hp:String,
    val mp:String,
    val spells:String,
    val favorite:Boolean
)

fun List<DatabaseChampions>.asDomainModel(): List<Champions> {
    return map {
        it.asDomainModel()
    }
}
fun DatabaseChampions.asDomainModel():Champions{
    return  Champions(
        championsName = championsName,
        title = title,
        image = image,
        roles = roles,
        hp = hp,
        mp = mp,
        blurb = blurb,
        spell = stringToList(spells),
        favorite = favorite
    )
}
fun stringToList(spells: String):List<Spell>{
     val moshi = Moshi.Builder().build()
     val type = Types.newParameterizedType(List::class.java, Spell::class.java)
    val adapter = moshi.adapter<List<Spell>>(type)
    return adapter.fromJson(spells)?: emptyList()
}