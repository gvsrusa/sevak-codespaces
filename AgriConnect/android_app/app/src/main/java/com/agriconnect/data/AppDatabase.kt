package com.agriconnect.data

import androidx.room.Database
import androidx.room.RoomDatabase
// Import DAOs and Entities here once they are created
// Example: import com.agriconnect.data.models.UserEntity
// Example: import com.agriconnect.data.dao.UserDao

@Database(
    entities = [
        // Add your Room entities here, e.g., UserEntity::class
    ],
    version = 1,
    exportSchema = false // Set to true if you want to export schema to a folder
)
abstract class AppDatabase : RoomDatabase() {
    // Define abstract methods for your DAOs here
    // abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        const val DATABASE_NAME = "agriconnect_db"

        // fun getInstance(context: android.content.Context): AppDatabase {
        //     return INSTANCE ?: synchronized(this) {
        //         val instance = androidx.room.Room.databaseBuilder(
        //             context.applicationContext,
        //             AppDatabase::class.java,
        //             DATABASE_NAME
        //         )
        //         // Add migrations if needed
        //         // .addMigrations(MIGRATION_1_2)
        //         .build()
        //         INSTANCE = instance
        //         instance
        //     }
        // }
    }
}