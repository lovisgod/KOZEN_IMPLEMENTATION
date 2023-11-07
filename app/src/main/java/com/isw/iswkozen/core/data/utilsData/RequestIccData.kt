package com.isw.iswkozen.core.data.utilsData

import android.os.Parcel
import android.os.Parcelable
import com.interswitchng.smartpos.models.transaction.CardReadTransactionResponse
import com.isw.iswkozen.core.data.models.EmvCard
import com.isw.iswkozen.core.data.models.EmvPinData
import org.simpleframework.xml.Element

class RequestIccData(
        @field:Element(name = "AmountAuthorized")
        var TRANSACTION_AMOUNT: String = "",
        @field:Element(name = "AmountOther")
        var ANOTHER_AMOUNT: String = "000000000000",
        @field:Element(name = "ApplicationInterchangeProfile")
        var APPLICATION_INTERCHANGE_PROFILE: String = "",
        @field:Element(name = "atc")
        var APPLICATION_TRANSACTION_COUNTER: String = "",
        @field:Element(name = "Cryptogram")
        var AUTHORIZATION_REQUEST: String = "",
        @field:Element(name = "CryptogramInformationData")
        var CRYPTOGRAM_INFO_DATA: String = "",
        @field:Element(name = "CvmResults")
        var CARD_HOLDER_VERIFICATION_RESULT: String = "",
        @field:Element(name = "iad")
        var ISSUER_APP_DATA: String = "",
        @field:Element(name = "TransactionCurrencyCode")
        var TRANSACTION_CURRENCY_CODE: String = "",
        @field:Element(name = "TerminalVerificationResult")
        var TERMINAL_VERIFICATION_RESULT: String = "",
        @field:Element(name = "TerminalCountryCode")
        var TERMINAL_COUNTRY_CODE: String = "",
        @field:Element(name = "TerminalType")
        var TERMINAL_TYPE: String = "",
        @field:Element(name = "TerminalCapabilities")
        var TERMINAL_CAPABILITIES: String = "",
        @field:Element(name = "TransactionDate")
        var TRANSACTION_DATE: String = "",
        @field:Element(name = "TransactionType")
        var TRANSACTION_TYPE: String = "",
        @field:Element(name = "UnpredictableNumber")
        var UNPREDICTABLE_NUMBER: String = "",
        @field:Element(name = "DedicatedFileName")
        var DEDICATED_FILE_NAME: String = ""): Parcelable {


        var INTERFACE_DEVICE_SERIAL_NUMBER: String = ""
        var APP_VERSION_NUMBER: String = ""
        var APP_PAN_SEQUENCE_NUMBER: String = ""
        var CARD_HOLDER_NAME: String = ""

        var iccAsString: String = ""

        var EMV_CARD_PIN_DATA: EmvPinData = EmvPinData()

        var EMC_CARD_ : EmvCard ? = null

        var haspin: Boolean? = true

        var TRACK_2_DATA: String = ""

        constructor(parcel: Parcel) : this(
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString()
        ) {
                INTERFACE_DEVICE_SERIAL_NUMBER = parcel.readString().toString()
                APP_VERSION_NUMBER = parcel.readString().toString()
                APP_PAN_SEQUENCE_NUMBER = parcel.readString().toString()
                CARD_HOLDER_NAME = parcel.readString().toString()
                iccAsString = parcel.readString().toString()
                haspin = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
                TRACK_2_DATA = parcel.readString().toString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(TRANSACTION_AMOUNT)
                parcel.writeString(ANOTHER_AMOUNT)
                parcel.writeString(APPLICATION_INTERCHANGE_PROFILE)
                parcel.writeString(APPLICATION_TRANSACTION_COUNTER)
                parcel.writeString(AUTHORIZATION_REQUEST)
                parcel.writeString(CRYPTOGRAM_INFO_DATA)
                parcel.writeString(CARD_HOLDER_VERIFICATION_RESULT)
                parcel.writeString(ISSUER_APP_DATA)
                parcel.writeString(TRANSACTION_CURRENCY_CODE)
                parcel.writeString(TERMINAL_VERIFICATION_RESULT)
                parcel.writeString(TERMINAL_COUNTRY_CODE)
                parcel.writeString(TERMINAL_TYPE)
                parcel.writeString(TERMINAL_CAPABILITIES)
                parcel.writeString(TRANSACTION_DATE)
                parcel.writeString(TRANSACTION_TYPE)
                parcel.writeString(UNPREDICTABLE_NUMBER)
                parcel.writeString(DEDICATED_FILE_NAME)
                parcel.writeString(INTERFACE_DEVICE_SERIAL_NUMBER)
                parcel.writeString(APP_VERSION_NUMBER)
                parcel.writeString(APP_PAN_SEQUENCE_NUMBER)
                parcel.writeString(CARD_HOLDER_NAME)
                parcel.writeString(iccAsString)
                parcel.writeValue(haspin)
                parcel.writeString(TRACK_2_DATA)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<RequestIccData> {
                override fun createFromParcel(parcel: Parcel): RequestIccData {
                        return RequestIccData(parcel)
                }

                override fun newArray(size: Int): Array<RequestIccData?> {
                        return arrayOfNulls(size)
                }
        }

}

fun paxDataToRequestIccData(data: CardReadTransactionResponse): RequestIccData {
        return RequestIccData(
                TRANSACTION_AMOUNT = data.emvData?.icc?.TRANSACTION_AMOUNT.toString(),
                ANOTHER_AMOUNT = data.emvData?.icc?.ANOTHER_AMOUNT.toString(),
                APPLICATION_INTERCHANGE_PROFILE = data.emvData?.icc?.APPLICATION_INTERCHANGE_PROFILE.toString(),
                APPLICATION_TRANSACTION_COUNTER = data.emvData?.icc?.APPLICATION_TRANSACTION_COUNTER.toString(),
               AUTHORIZATION_REQUEST = data.emvData?.icc?.AUTHORIZATION_REQUEST.toString(),
                CRYPTOGRAM_INFO_DATA = data.emvData?.icc?.CRYPTOGRAM_INFO_DATA.toString(),
               CARD_HOLDER_VERIFICATION_RESULT = data.emvData?.icc?.CARD_HOLDER_VERIFICATION_RESULT.toString(),
               ISSUER_APP_DATA = data.emvData?.icc?.ISSUER_APP_DATA.toString(),
               TRANSACTION_CURRENCY_CODE = data.emvData?.icc?.TRANSACTION_CURRENCY_CODE.toString(),
                TERMINAL_VERIFICATION_RESULT = data.emvData?.icc?.TERMINAL_VERIFICATION_RESULT.toString(),
               TERMINAL_COUNTRY_CODE = data.emvData?.icc?.TERMINAL_COUNTRY_CODE.toString(),
               TERMINAL_TYPE = data.emvData?.icc?.TERMINAL_TYPE.toString(),
               TERMINAL_CAPABILITIES = data.emvData?.icc?.TERMINAL_CAPABILITIES.toString(),
                TRANSACTION_DATE = data.emvData?.icc?.TRANSACTION_DATE.toString(),
                TRANSACTION_TYPE = data.emvData?.icc?.TRANSACTION_TYPE.toString(),
                UNPREDICTABLE_NUMBER = data.emvData?.icc?.UNPREDICTABLE_NUMBER.toString(),
               DEDICATED_FILE_NAME = data.emvData?.icc?.DEDICATED_FILE_NAME.toString()
        ).apply {
                iccAsString = data.emvData?.icc?.iccAsString.toString()
                INTERFACE_DEVICE_SERIAL_NUMBER = data.emvData?.icc?.INTERFACE_DEVICE_SERIAL_NUMBER.toString()
                APP_VERSION_NUMBER = data.emvData?.icc?.APP_VERSION_NUMBER.toString()
                APP_PAN_SEQUENCE_NUMBER = data.emvData?.icc?.APP_PAN_SEQUENCE_NUMBER.toString()
                CARD_HOLDER_NAME = data.emvData?.icc?.CARD_HOLDER_NAME.toString()

        }
}

