package com.isw.iswkozen.core.network.CardLess


import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants.getNextStan
import com.isw.iswkozen.core.network.CardLess.request.TransactionInfo
import com.isw.iswkozen.core.utilities.DisplayUtils
import java.util.*

/**
 * This class represents information required for
 * issuing out QR and USSD code purchase request
 */
 data class CardLessPaymentRequest(
        val alias: String,
        val amount: String,
        val bankCode: String?,
        val date: String,
        var stan: String,
        val terminalId: String,
        val transactionType: String,
        val qrFormat: String?,
        val additionalInformation: TransactionInfo
) {

    companion object {
        // transaction types
         const val TRANSACTION_QR = "QR"
         const val TRANSACTION_USSD = "USSD"
         const val TRANSACTION_TRANSFER = "TRANSFER"

        // qr request formats
         const val QR_FORMAT_BITMAP = "BITMAP"
         const val QR_FORMAT_RAW = "RAW"
         const val QR_FORMAT_FULL = "FULL"

         fun from(
                //alias: String,
             terminalInfo: TerminalInfo,
             paymentInfo: CardLessPaymentInfo,
             transactionType: String,
             qrFormat: String? = null,
             bankCode: String? = null
        ) = CardLessPaymentRequest(
                alias = "${terminalInfo.qtbMerchantAlias}",
                amount = "${paymentInfo.amount}",
                bankCode = bankCode,
                date = DisplayUtils.getIsoString(Date()),
                stan = getNextStan(),
                terminalId = terminalInfo.terminalCode,
                transactionType = transactionType,
                qrFormat = qrFormat,
                additionalInformation = TransactionInfo.from(terminalInfo, paymentInfo)
        )
    }
}