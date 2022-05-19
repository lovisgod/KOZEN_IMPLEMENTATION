package com.isw.iswkozen.core.network.CardLess.request

import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentInfo
import com.isw.iswkozen.core.network.CardLess.PaymentInfo


/**
 * This class represents transaction information required
 * to issue out uSSD and QR code Purchase Transactions
 */ data class TransactionInfo(
        val currencyCode: String,
        val merchantId: String,
        val merchantLocation: String,
        val posGeoCode: String,
        val terminalType: String,
        val uniqueId: String
) {

    companion object {

        internal fun from(terminalInfo: TerminalInfo, paymentInfo: CardLessPaymentInfo) = TransactionInfo (
                currencyCode = terminalInfo.transCurrencyCode,
                merchantId = terminalInfo.merchantId,
                merchantLocation = terminalInfo.merchantName,
                posGeoCode = terminalInfo.terminalCountryCode,
                terminalType = "Android",
                uniqueId =  paymentInfo.currentStan
        )

        internal fun fromOld(terminalInfo: TerminalInfo, paymentInfo: PaymentInfo) = TransactionInfo (
                currencyCode = terminalInfo.transCurrencyCode,
                merchantId = terminalInfo.merchantId,
                merchantLocation = terminalInfo.merchantName,
                posGeoCode = terminalInfo.terminalCountryCode,
                terminalType = "Android",
                uniqueId =  paymentInfo.currentStan
        )
    }
}