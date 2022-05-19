package com.isw.iswkozen.core.network.CardLess.request

import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.network.CardLess.PaymentInfo
import com.isw.iswkozen.core.utilities.DisplayUtils
import java.util.*


/**
 * This class represents information required for
 * issuing out QR and USSD code purchase request
 */
internal data class CodeRequest(
        val alias: String,
        val amount: String,
        val bankCode: String?,
        val date: String,
        val stan: String,
        val terminalId: String,
        val transactionType: String,
        val qrFormat: String?,
        val additionalInformation: TransactionInfo
) {

    companion object {
        // transaction types
        internal const val TRANSACTION_QR = "QR"
        internal const val TRANSACTION_USSD = "USSD"

        // qr request formats
        internal const val QR_FORMAT_BITMAP = "BITMAP"
        internal const val QR_FORMAT_RAW = "RAW"
        internal const val QR_FORMAT_FULL = "FULL"

        internal fun from(alias: String, terminalInfo: TerminalInfo, paymentInfo: PaymentInfo, transactionType: String, qrFormat: String? = null) = CodeRequest (
                alias = alias,
                amount = "${paymentInfo.amount}",
                bankCode = paymentInfo.bankCode,
                date = DisplayUtils.getIsoString(Date()),
                stan = paymentInfo.getStan(),
                terminalId = terminalInfo.terminalCode,
                transactionType = transactionType,
                qrFormat = qrFormat,
                additionalInformation = TransactionInfo.fromOld(terminalInfo, paymentInfo)
        )
    }
}