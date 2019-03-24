package io.philippeboisney.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.philippeboisney.local.converter.Converters
import io.philippeboisney.local.dao.UserDao
import io.philippeboisney.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArchAppDatabase: RoomDatabase() {

    // DAO
    abstract fun userDao(): UserDao

    companion object {

        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, ArchAppDatabase::class.java, "ArchApp.db")
                .build()
    }
}