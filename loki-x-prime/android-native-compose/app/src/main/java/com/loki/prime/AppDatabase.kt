package com.loki.prime

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Define the Entity (Maps to your Dexie Schema)
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

// 2. Define the DAO (Data Access Object)
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun clearAll()
}

// 3. Create the Database
@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class LokiDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: LokiDatabase? = null

        fun getDatabase(context: android.content.Context): LokiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LokiDatabase::class.java,
                    "loki_x_prime_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
