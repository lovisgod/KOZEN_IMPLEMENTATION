package com.isw.iswkozen.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.TransactionType
import com.isw.iswkozen.core.network.models.PurchaseResponse
import com.isw.iswkozen.core.utilities.DisplayUtils
import java.time.LocalDate.now
import java.util.*


@Entity(tableName = "transaction_result_table")
data class TransactionResultData(
    val paymentType: String = "",
    @PrimaryKey val stan: String = "",
    val dateTime: String = "",
    val amount: String = "",
    val type: TransactionType,
    val cardPan: String ="",
    val cardType: Int = 0,
    val cardExpiry: String = "",
    val authorizationCode: String = "",
    var tid: String = "",
    var merchantId: String = "",
    var merchantName: String = "",
    var merchantLocation: String = "",
    val responseMessage: String = "",
    val responseCode: String = "",
    val AID: String = "",
    val code: String = "",
    val telephone: String = "",
    val txnDate: Long = 0L,
    val transactionId: String = "",
    val cardHolderName: String,
    val remoteResponseCode: String = "",
    val biller: String? = "",
    val customerDescription: String? = "",
    val surcharge: String? = "",
    val additionalAmounts: String? = "",
    var customerName: String? = "",
    var ref : String? = "",
    var accountNumber: String? = "",
    var transactionCurrencyCode: String = "566"
    //val additionalInfo: String? = ""
)

fun createTransResultData(purchaseResponse: PurchaseResponse,
                          iccData: RequestIccData,
                          transactionName: String,
                          transactionType: TransactionType,
                          terminalData: TerminalInfo): TransactionResultData {
    return purchaseResponse?.let {
        return@let TransactionResultData(
            AID = iccData.DEDICATED_FILE_NAME,
            stan = it?.stan,
            dateTime = iccData.TRANSACTION_DATE,
            cardExpiry = "",
            cardPan = iccData.EMC_CARD_?.cardNumber?.let { it1 -> DisplayUtils.maskPan(it1) }
                .toString(),
            paymentType = transactionName,
            cardHolderName = iccData.CARD_HOLDER_NAME,
            type = transactionType,
            amount = iccData.TRANSACTION_AMOUNT,
            tid = terminalData.terminalCode,
            merchantId = terminalData.merchantId,
            merchantName = terminalData.merchantName,
            merchantLocation = terminalData.merchantAddress1 + terminalData.merchantAddress2,
            responseMessage = purchaseResponse.description,
            responseCode = purchaseResponse.responseCode,
            txnDate = Date().time
        )
    }
}

