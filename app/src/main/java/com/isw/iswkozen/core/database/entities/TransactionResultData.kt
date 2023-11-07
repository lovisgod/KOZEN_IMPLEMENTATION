package com.isw.iswkozen.core.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.interswitchng.smartpos.models.PaymentModel
import com.interswitchng.smartpos.models.transaction.CardReadTransactionResponse
import com.interswitchng.smartpos.shared.services.utils.IsoUtils
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.CardLessTransactionType
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.TransactionType
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentInfo
import com.isw.iswkozen.core.network.CardLess.response.PaymentStatus
import com.isw.iswkozen.core.network.CardLess.response.Transaction
import com.isw.iswkozen.core.network.models.PurchaseResponse
import com.isw.iswkozen.core.utilities.DisplayUtils
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate.now
import java.util.*


@Parcelize
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
):Parcelable

fun createTransResultData(purchaseResponse: PurchaseResponse,
                          iccData: RequestIccData,
                          transactionName: String,
                          transactionType: TransactionType,
                          terminalData: TerminalInfo): TransactionResultData {
    return purchaseResponse.let {
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


fun convertPaxTransType(transactionType: com.interswitchng.smartpos.models.transaction.PaymentType): TransactionType {
    return when(transactionType) {
      com.interswitchng.smartpos.models.transaction.PaymentType.Card -> TransactionType.Card
       com.interswitchng.smartpos.models.transaction.PaymentType.PayCode -> TransactionType.PayCode
       com.interswitchng.smartpos.models.transaction.PaymentType.QR -> TransactionType.QR
       com.interswitchng.smartpos.models.transaction.PaymentType.USSD -> TransactionType.Ussd
       else -> TransactionType.Transfer
    }
}
fun createpaxTransactionData(
                          data: CardReadTransactionResponse,
                          terminalData: com.interswitchng.smartpos.models.core.TerminalInfo): TransactionResultData {
        return TransactionResultData(
            AID = data.emvData?.AID.toString(),
            stan = data.transactionResult?.stan.toString(),
            dateTime = data.transactionResult?.dateTime.toString(),
            cardExpiry = "",
            cardPan = data.emvData?.cardPAN?.let { it1 -> DisplayUtils.maskPan(it1) }
                .toString(),
            paymentType = data.transactionResult?.paymentType?.name.toString(),
            cardHolderName = data.emvData?.icc?.CARD_HOLDER_NAME.toString(),
            type = convertPaxTransType(transactionType = data.transactionResult?.paymentType!!),
            amount = data.transactionResult?.amount.toString(),
            tid = terminalData.terminalId,
            merchantId = terminalData.merchantId,
            merchantName = terminalData.merchantNameAndLocation.toString(),
            merchantLocation = terminalData.merchantAddress1 + terminalData.merchantAddress2,
            responseMessage = data.transactionResult?.responseMessage.toString(),
            responseCode = data.transactionResult?.responseCode.toString(),
            txnDate = Date().time
        )



}

fun createTransactionResultFromCardLess(
                          paymentStatus: Transaction,
                          paymentInfo: CardLessPaymentInfo,
                          transactionName: String,
                          transactionType: TransactionType,
                          terminalData: TerminalInfo): TransactionResultData {
    return paymentStatus.let {
        val responseMsg = IsoUtils.getIsoResult(paymentStatus.responseCode)?.second
            ?: paymentStatus.responseDescription
            ?: "Error"
        val now = Date()
        return@let TransactionResultData(
            AID = "",
            stan = paymentInfo.currentStan,
            dateTime = DisplayUtils.getIsoString(now),
            cardExpiry = "",
            cardPan = "",
            paymentType = transactionName,
            cardHolderName = "",
            type = transactionType,
            amount = ((paymentInfo.amount.toString().toInt()) *100) .toString(),
            tid = terminalData.terminalCode,
            merchantId = terminalData.merchantId,
            merchantName = terminalData.merchantName,
            merchantLocation = terminalData.merchantAddress1 + terminalData.merchantAddress2,
            responseMessage = responseMsg,
            responseCode = paymentStatus.responseCode,
            txnDate = Date().time,
            ref = paymentInfo.reference
        )
    }


}



