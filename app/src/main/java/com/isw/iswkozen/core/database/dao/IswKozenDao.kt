package com.isw.iswkozen.core.database.dao

import androidx.room.*
import com.isw.iswkozen.core.database.entities.TransactionResultData
import kotlinx.coroutines.flow.Flow

@Dao
interface IswKozenDao {

    @Query("SELECT * FROM transaction_result_table ORDER BY stan ASC")
    fun getAllTransaction(): Flow<List<TransactionResultData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(resultData: TransactionResultData)

    @Update
    suspend fun updateTransaction(resultData: TransactionResultData)

}