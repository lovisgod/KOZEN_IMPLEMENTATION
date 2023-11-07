package com.isw.iswkozen.core.data.dataInteractor

import android.content.Context
import com.isw.iswkozen.core.data.datasource.IswTransactionDataSource
import com.isw.iswkozen.core.data.models.TransactionData
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.database.dao.IswKozenDao

class IswTransactionInteractor( val iswTransactionDataSource: IswTransactionDataSource) {

    suspend fun startTransaction(
        hasContactless: Boolean = true,
        hasContact: Boolean = true,
        amount: Long,
        amountOther: Long,
        transType: Int,
        emvEvents: EMVEvents
    ) = iswTransactionDataSource.startTransaction(hasContactless,
        hasContact,
        amount,
        amountOther,
        transType,
        emvEvents)

    suspend fun continuePaxTransaction() {
        iswTransactionDataSource.continuePaxTransaction()
    }

    suspend fun setPaxDao(dao: IswKozenDao) {
        iswTransactionDataSource.setDaoForPaxHandler(dao)
    }
    suspend fun getTransactionData(): RequestIccData =
        iswTransactionDataSource.getTransactionData()

    suspend fun setEmvContect(context: Context) =
        iswTransactionDataSource.setEmvContect(context)

    suspend fun setPinMode(pinMode: Int) = iswTransactionDataSource.setEmvPINMODE(pinMode)
}