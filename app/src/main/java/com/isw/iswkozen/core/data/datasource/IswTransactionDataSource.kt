package com.isw.iswkozen.core.data.datasource

import android.content.Context
import com.isw.iswkozen.core.data.models.TransactionData
import com.isw.iswkozen.core.data.utilsData.RequestIccData

interface IswTransactionDataSource {

    suspend fun startTransaction(
        hasContactless: Boolean = true,
        hasContact: Boolean = true,
        amount: Long,
        amountOther: Long,
        transType: Int
    )
    suspend fun getTransactionData(): RequestIccData

    suspend fun setEmvContect(context: Context)
}