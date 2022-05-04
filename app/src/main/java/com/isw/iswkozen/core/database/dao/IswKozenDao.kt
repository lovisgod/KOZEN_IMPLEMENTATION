package com.isw.iswkozen.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isw.iswkozen.core.database.entities.TransactionResultData
import kotlinx.coroutines.flow.Flow

@Dao
interface IswKozenDao {

    @Query("SELECT * FROM transaction_result_table ORDER BY id ASC")
    fun getAllTransaction(): Flow<List<TransactionResultData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(resultData: TransactionResultData)

}