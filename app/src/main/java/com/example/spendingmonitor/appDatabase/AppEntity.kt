package com.example.spendingmonitor.appDatabase

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val keyword: List<String>,
)
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList()
        else value.split(",").map { it.trim() }
}

@Dao
interface AppDao {
    @Query("SELECT * FROM apps ORDER BY packageName DESC")
    fun getAll(): Flow<List<AppEntity>>

    @Upsert
    suspend fun upsert(appEntity: AppEntity)

    @Delete
    suspend fun delete(appEntity: AppEntity)

    @Query("DELETE FROM apps")
    suspend fun deleteAll()

    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("SELECT DISTINCT packageName FROM apps")
    fun getAllPackageName(): Flow<List<String>>
}

@Database(entities = [AppEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}