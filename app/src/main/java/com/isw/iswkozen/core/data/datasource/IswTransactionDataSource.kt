package com.isw.iswkozen.core.data.datasource

import android.content.Context
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.models.TransactionData
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.database.dao.IswKozenDao

interface IswTransactionDataSource {

    suspend fun startTransaction(
        hasContactless: Boolean = true,
        hasContact: Boolean = true,
        amount: Long,
        amountOther: Long,
        transType: Int,
        emvEvents: EMVEvents
    )
    suspend fun getTransactionData(): RequestIccData

    suspend fun setEmvContect(context: Context)

    suspend fun setEmvPINMODE(pinMode: Int)

    suspend fun continuePaxTransaction() {
        println("continue pax transaction here")
    }

    suspend fun setDaoForPaxHandler(dao: IswKozenDao){
        println("setting dao for pax handler")
    }
}