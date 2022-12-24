package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.*
import com.udacity.asteroidradar.utils.getTodayString

/**
 * Dao interface for local database
 */
@Dao
interface AsteroidDao {
    /**
     * insert new record to db
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidRecord)

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate =:today ORDER BY closeApproachDate ASC")
    fun getTodayAsteroids(today: String = getTodayString()): List<AsteroidRecord>

    @Query("SELECT * FROM asteroid_table")
    fun getAll(): List<AsteroidRecord>

    /**
     * get all asteroid saved on database
     */
    @Query("SELECT * FROM asteroid_table ORDER BY closeApproachDate ASC")
    fun getSavedAsteroids(): List<AsteroidRecord>

    /**
     * get all asteroids from today
     */
    @Query("SELECT * FROM asteroid_table WHERE (Date(closeApproachDate) >=Date(:today)) ORDER BY closeApproachDate ASC")
    fun getWeekAsteroids(today: String = getTodayString()): List<AsteroidRecord>

    /**
     * get the newest record
     */
    @Query("SELECT * FROM asteroid_table ORDER BY closeApproachDate DESC LIMIT 1")
    fun getLatestAsteroid(): AsteroidRecord?

    /**
     * delete all records older than today
     */
    @Query("DELETE FROM asteroid_table WHERE (Date(closeApproachDate) < Date(:today))")
    fun deleteOldAsteroids(today: String = getTodayString())

    /**
     * delete all records
     */
    @Query("DELETE FROM asteroid_table")
    fun clearDatabase()
}

/**
 * Database
 */
@Database(entities = [AsteroidRecord::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao

    companion object {
        @Volatile
        private var INSTANCE: AsteroidDatabase? = null
        fun getDatabase(context: Context): AsteroidDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (null == instance) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroid_cache_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}