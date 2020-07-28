package com.kaito.afinal.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {
    @Query("select * from databasechampions order by championsName asc")
    fun getChampions(): LiveData<List<DatabaseChampions>>
    @Query("select * from databasechampions where roles like :roles order by championsName asc")
    fun getFilteredChampions(roles:String): LiveData<List<DatabaseChampions>>
    @Query("select * from databasechampions where championsName = :name")
    fun getChampion(name:String):LiveData<DatabaseChampions>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChampion(champion:DatabaseChampions)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(champions: List<DatabaseChampions>)
    @Query("SELECT * FROM databasechampions WHERE championsName = :keyName")
    fun getFavoriteByName(keyName:String):DatabaseChampions

}
@Database(entities = [DatabaseChampions::class], version = 1)
abstract class ChampionsDatabase : RoomDatabase() {
    abstract val championsDao: VideoDao
}
private lateinit var INSTANCE: ChampionsDatabase

fun getDatabase(context: Context): ChampionsDatabase {
    synchronized(ChampionsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                ChampionsDatabase::class.java,
                "champions").build()
        }
    }
    return INSTANCE
}
