package com.isw.iswkozen.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.isw.iswkozen.core.database.dao.IswKozenDao
import com.isw.iswkozen.core.database.entities.TransactionResultData

// Annotates class to be a Room Database with a table (entity) of the TransactionResultData class
@Database(entities = arrayOf(TransactionResultData::class), version = 1, exportSchema = false)
public abstract class IswKozenRoomDb : RoomDatabase() {

    abstract fun iswKozenDao(): IswKozenDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: IswKozenRoomDb? = null

        fun getDatabase(context: Context): IswKozenRoomDb {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IswKozenRoomDb::class.java,
                    "iswkozen_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}