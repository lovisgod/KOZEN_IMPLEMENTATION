package com.isw.iswkozen.core.network.models

import android.os.Build
import android.os.Parcelable
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.utilities.DateUtils
import com.isw.iswkozen.core.utilities.DeviceUtils
import kotlinx.android.parcel.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.*

@Parcelize
@Root(name = "terminalInformation", strict = false)
data class TerminalInformationRequest (
    @field:Element(name = "batteryInformation", required = false)
    var batteryInformation: String = "",

    @field:Element(name = "cellStationId", required = false)
    var cellStationId: String = "",

    @field:Element(name = "currencyCode", required = false)
    var currencyCode: String = "",

    @field:Element(name = "languageInfo", required = false)
    var languageInfo: String = "",

    @field:Element(name = "merchantId", required = false)
    var merchantId: String = "",

    @field:Element(name = "merhcantLocation", required = false)
    var merhcantLocation: String = "",

    @field:Element(name = "posConditionCode", required = false)
    var posConditionCode: String = "",

    @field:Element(name = "posDataCode", required = false)
    var posDataCode: String = "",

    @field:Element(name = "posEntryMode", required = false)
    var posEntryMode: String = "",

    @field:Element(name = "posGeoCode", required = false)
    var posGeoCode: String = "",

    @field:Element(name = "printerStatus", required = false)
    var printerStatus: String = "",

    @field:Element(name = "terminalId", required = false)
    var terminalId: String = "",

    @field:Element(name = "terminalType", required = false)
    var terminalType: String = "",

    @field:Element(name = "transmissionDate", required = false)
    var transmissionDate: String = "",

    @field:Element(name = "uniqueId", required = false)
    var uniqueId: String = "",
 ): Parcelable {

      fun fromTerminalInfo(
          deviceName: String = "kozen",
          terminalInfo: TerminalInfo,
          haspin: Boolean
      ): TerminalInformationRequest {
          val battery = "-1"
          val date = DateUtils.universalDateFormat.format(Date())

          return TerminalInformationRequest().apply {
              batteryInformation = battery
              currencyCode = "566"
              languageInfo = "EN"
              merchantId = terminalInfo.merchantId
              merhcantLocation = terminalInfo.cardAcceptorNameLocation
              posConditionCode = "00"
              posEntryMode = "051"
              terminalId = terminalInfo.terminalCode
              transmissionDate = date
              uniqueId = DeviceUtils.getDeviceSerialKozen().toString()
              terminalType = deviceName.toUpperCase()
              posDataCode = if (haspin) "510101511344101" else "511101511344101"
              cellStationId = "00"
              posGeoCode = "00234000000000566"
          }
      }



    fun createForrequest(
        deviceName: String = "kozen",
        terminalInfo: TerminalInfo,
        haspin: Boolean
    ): TerminalInformationRequest {
        val battery = "-1"
        val date = DateUtils.universalDateFormat.format(Date())

        return TerminalInformationRequest().apply {
            batteryInformation = battery
            currencyCode = "566"
            languageInfo = "EN"
            merchantId = terminalInfo.merchantId
            merhcantLocation = terminalInfo.cardAcceptorNameLocation
            posConditionCode = "00"
            posEntryMode = Constants.POS_ENTRY_MODE
            terminalId = terminalInfo.terminalCode
            transmissionDate = date
            uniqueId = DeviceUtils.getDeviceSerialKozen().toString()
            terminalType = deviceName.toUpperCase()
            posDataCode = Constants.POS_DATA_CODE
            cellStationId = "00"
            posGeoCode = "00234000000000566"
        }
    }


 }