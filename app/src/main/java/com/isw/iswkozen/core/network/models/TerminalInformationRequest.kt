package com.isw.iswkozen.core.network.models

import android.os.Parcelable
import com.isw.iswkozen.core.data.models.TerminalInfo
import kotlinx.android.parcel.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

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

      fun fromTerminalInfo(terminalInfo: TerminalInfo): TerminalInformationRequest {
          return TerminalInformationRequest(terminalId = terminalInfo.terminalCode,
              merchantId = terminalInfo.merchantId)
      }
 }