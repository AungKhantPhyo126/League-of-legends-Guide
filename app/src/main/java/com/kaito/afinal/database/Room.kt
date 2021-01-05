package com.kaito.afinal.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {

    @Query("select * from databasechampions where favorite = :favorite order by championsName asc")
    fun getFavoriteChampions(favorite: Boolean = true): LiveData<List<DatabaseChampions>>

    @Query("select * from databasechampions order by championsName asc")
    fun getChampions(): LiveData<List<DatabaseChampions>>

    @Query("select * from databasechampions where roles like :roles order by championsName asc")
    fun getChampions(roles: String): LiveData<List<DatabaseChampions>>

    @Query("select * from databasechampions where championsName = :name")
    fun getChampionLive(name: String): LiveData<DatabaseChampions>

    @Query("select * from databasechampions where championsName = :name")
    fun getChampion(name: String): DatabaseChampions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(champion: DatabaseChampions)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(champions: List<DatabaseChampions>)

    @Query("update databasechampions set favorite = :favorite where  championsName = :name")
    fun toggleFavorite(name: String, favorite: Boolean)
}

@Database(entities = [DatabaseChampions::class], version = 1)
abstract class ChampionsDatabase : RoomDatabase() {
    abstract val championsDao: VideoDao
}

private lateinit var INSTANCE: ChampionsDatabase

fun getDatabase(context: Context): ChampionsDatabase {
    synchronized(ChampionsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ChampionsDatabase::class.java,
                "champions"
            ).allowMainThreadQueries().build()
        }
    }
    return INSTANCE
}
