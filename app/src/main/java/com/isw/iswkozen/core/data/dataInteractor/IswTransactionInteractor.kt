package com.isw.iswkozen.core.data.dataInteractor

import android.content.Context
import com.isw.iswkozen.core.data.datasource.IswTransactionDataSource
import com.isw.iswkozen.core.data.models.TransactionData

class IswTransactionInteractor( val iswTransactionDataSource: IswTransactionDataSource) {

    suspend fun startTransaction(
        hasContactless: Boolean = true,
        hasContact: Boolean = true,
        amount: Long,
        amountOther: Long,
        transType: Int
    ) = iswTransactionDataSource.startTransaction(hasContactless,
        hasContact,
        amount,
        amountOther,
        transType)
    suspend fun getTransactionData(): TransactionData =
        iswTransactionDataSource.getTransactionData()

    suspend fun setEmvContect(context: Context) =
        iswTransactionDataSource.setEmvContect(context)
}