package com.isw.iswkozen.core.network.CardLess


import android.os.Parcel
import android.os.Parcelable
import java.util.*


/**
 * This class represents the purchase request
 * triggered by external source that depends the SDK
 */
 data class CardLessPaymentInfo(
    val amount: Int,
    val currentStan: String,
    val surcharge: Int = 0,
    val additionalAmounts: Int = 0,
    var reference:String = ""
) : Parcelable {


    val amountString: String get() = String.format(Locale.getDefault(), "%,.2f",
        amount.toDouble() / 100.toDouble())

    val additionalAmountsString: String get() = String.format(Locale.getDefault(), "%,.2f",
            additionalAmounts.toDouble() / 100.toDouble())

    val surchargeString: String get() = String.format(Locale.getDefault(), "%,.2f",
            surcharge.toDouble() / 100.toDouble())

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(amount)
        parcel.writeString(currentStan)
        parcel.writeInt(surcharge)
        parcel.writeInt(additionalAmounts)
        parcel.writeString(reference)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardLessPaymentInfo> {
        override fun createFromParcel(parcel: Parcel): CardLessPaymentInfo {
            return CardLessPaymentInfo(parcel)
        }

        override fun newArray(size: Int): Array<CardLessPaymentInfo?> {
            return arrayOfNulls(size)
        }
    }
}