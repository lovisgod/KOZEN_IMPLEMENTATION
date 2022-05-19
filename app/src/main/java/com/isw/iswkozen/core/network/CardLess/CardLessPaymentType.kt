package com.isw.iswkozen.core.network.CardLess

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * This enum class represents the different
 * payment methods the SDK supports
 */
@Parcelize
enum class CardLessPaymentType : Parcelable {
    PayCode,
    Card,
    QR,
    Transfer,
    USSD;

    override fun toString(): String {
        val string =  when {
            this == Card -> "Card Payment"
            this == QR -> "QR Code Payment"
            this == USSD -> "USSD Payment"
            else -> super.toString()
        }

        return string
    }
}