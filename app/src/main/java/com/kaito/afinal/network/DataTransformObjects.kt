package com.kaito.afinal.network

import android.provider.MediaStore
import androidx.recyclerview.widget.RecyclerView
import androidx.versionedparcelable.ParcelField
import androidx.versionedparcelable.VersionedParcelize
import com.kaito.afinal.database.DatabaseChampions
import com.kaito.afinal.domain.Champions
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.*
import kotlin.collections.HashMap
data class OverViewDto(
    val data: Map<String, NetworkChampions>

)

@JsonClass(generateAdapter = true)
data class NetworkChampions(
    @Json(name = "name") val championsName: String,
    val title: String,
    val image: Images,
    val blurb:String,
    val tags:List<String>,
    val stats:Stats,
    val spells:List<Spell>?
)
data class Spell(
    val id:String,
    val name:String,
    val description:String
)

@JsonClass(generateAdapter = true)
data class Stats(
    val hp:Double,
    val mp:Double
)

@JsonClass(generateAdapter = true)
data class Images(
    @Json(name = "full") val imgSrcUrl: String
)
fun OverViewDto.asDatabase():List<DatabaseChampions>{
    return data.values.map {
        DatabaseChampions(
            championsName = it.championsName,
            title = it.title,
            image = it.image.imgSrcUrl,
            roles = it.tags.toString().replace("[","").replace("]","")+",All",
            hp = it.stats.hp.toString(),
            mp = it.stats.mp.toString(),
            blurb = it.blurb,
            spells = listToString(it.spells)
        )
    }
}
fun listToString(spells: List<Spell>?):String{
    val moshi = Moshi.Builder().build()
    val type = Types.newParameterizedType(List::class.java, Spell::class.java)
    val adapter = moshi.adapter<List<Spell>>(type)
    return adapter.toJson(spells)
}


