package com.isw.iswkozen.core.data.dataSourceImpl

import android.content.Context
import com.isw.iswkozen.core.data.datasource.IswTransactionDataSource
import com.isw.iswkozen.core.data.models.TransactionData
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.utilities.EmvHandler

class IswTransactionImpl(val emvHandler: EmvHandler): IswTransactionDataSource {
    override suspend fun startTransaction(
        hasContactless: Boolean,
        hasContact: Boolean,
        amount: Long,
        amountOther: Long,
        transType: Int
    ) {
        emvHandler.startTransaction(
            hasContactless,
            hasContact,
            amount,
            amountOther,
            transType)
    }

    override suspend fun getTransactionData(): RequestIccData {
       return emvHandler.iccData!!
    }

    override suspend fun setEmvContect(context: Context) {
        emvHandler.setEmvContext(context)
    }
}