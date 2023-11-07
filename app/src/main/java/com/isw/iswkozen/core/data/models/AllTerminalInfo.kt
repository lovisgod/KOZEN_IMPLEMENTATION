package com.isw.iswkozen.core.data.models

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.interswitchng.smartpos.BuildConfig
import com.isw.iswkozen.core.data.utilsData.Constants
import kotlinx.android.parcel.Parcelize
import org.simpleframework.xml.*

@Parcelize
@Root(name = "allTerminalInfo", strict = false)
data class AllTerminalInfo (
        @field:Element(name = "responseCode", required = false)
        var responseCode: String = "",

        @field:Element(name = "responseMessage", required = false)
        var responseMessage: String = "",

        @field:ElementList(name = "terminalAllowedTxTypes", inline=true, required = false)
//        var terminalAllowedTxTypes: List<TerminalAllowedTxTypes>? = null,
        var terminalAllowedTxTypes: List<String>? = null,

        @field:Element(name = "terminalInfoBySerials", required = false)
        var terminalInfo: TerminalInfo? = null,

        @field:Element(name = "tmsRouteTypeConfig", required = false)
        var tmsRouteTypeConfig: TmsRouteTypeConfig? = null
): Parcelable

@Parcelize
@Root(name = "terminalInfoBySerials", strict = false)
data class TerminalInfo(

        @field:Element(name = "terminalCode", required = false)
        var terminalCode: String = "Terminal",

        @field:Element(name = "cardAcceptorId", required = false)
        var cardAcceptorId: String = "000000000000000",

        @field:Element(name = "merchantId", required = false)
        var merchantId: String = "000000000000000",

        @field:Element(name = "merchantName", required = false)
        var merchantName: String = "Railway Operator",

        @field:Element(name = "merchantAddress1", required = false)
        var merchantAddress1: String = "",

        @field:Element(name = "merchantAddress2", required = false)
        var merchantAddress2: String = "",

        @field:Element(name = "merchantPhoneNumber", required = false)
        var merchantPhoneNumber: String = "08000000000",

        @field:Element(name = "merchantEmail", required = false)
        var merchantEmail: String = "sample@interswitchng.com",

        @field:Element(name = "merchantState", required = false)
        var merchantState: String = "Lagos state",

        @field:Element(name = "tmsRouteType", required = false)
        var tmsRouteType: String = "",

        @field:Element(name = "merchantCity", required = false)
        var merchantCity: String = "Lagos",

        @field:Element(name = "qtbMerchantCode", required = false)
        var qtbMerchantCode: String? = "MX1065",

        @field:Element(name = "qtbMerchantAlias", required = false)
        var qtbMerchantAlias: String? = "002208",

        @field:Element(name = "cardAcceptorNameLocation", required = false)
        var cardAcceptorNameLocation: String = "",

        @field:Element(name = "merchantCategoryCode", required = false)
        var merchantCategoryCode: String = "5411",

        var  terminalCountryCode: String = "0566",
        var  transCurrencyCode: String = "0566",
        var transCurrencyExp: String = "02",
        var terminalType: String = "22",
        var terminalCapabilities: String = "E0F8C8",
        var terminalExtCapabilities: String = "F000F0F001",
        var terminalEntryMode: String = "05",
        var nibbsKey: String = Constants.getCMS(false)
): Parcelable {
        override fun toString(): String {
                """code: ${terminalCode}
                   capailty: ${terminalCapabilities}
                   name: ${merchantName}
                """.trimIndent()
                return super.toString()
        }
}

@Root(name = "terminalAllowedTxTypes", strict = false)
class TerminalAllowedTxTypes(
        @field:Element(name = "applicationDescription", required = false)
        var applicationDescription: String = ""
)

@Parcelize
@Root(name = "tmsRouteTypeConfig", strict = false)
class TmsRouteTypeConfig(

        @field:Element(name = "port", required = false)
        var port: String = "${Constants.ISW_DEFAULT_TERMINAL_PORT}",

        @field:Element(name = "ip", required = false)
        var ip: String = "${Constants.ISW_DEFAULT_TERMINAL_IP}",

        @field:Element(name = "key", required = false)
        var key: String = "${Constants.getCMS(false)}",

        @field:Element(name = "name", required = false)
        var name: String = ""): Parcelable


fun TerminalInfo.toPaxTerminalInfo() : com.interswitchng.smartpos.models.core.TerminalInfo {
        return com.interswitchng.smartpos.models.core.TerminalInfo(
                terminalId = this.terminalCode,
                merchantId = this.merchantId,
                merchantName = this.merchantName,
                merchantAddress1 = this.merchantAddress1,
                merchantAddress2 = this.merchantAddress2,
                countryCode = "566",
                currencyCode = "566",
                merchantNameAndLocation = this.cardAcceptorNameLocation,
                merchantCategoryCode = this.merchantCategoryCode,
                serverUrl = Constants.ISW_KIMONO_BASE_URL,
                serverIp = com.interswitchng.smartpos.shared.Constants.ISW_TERMINAL_IP,
                serverPort = BuildConfig.ISW_TERMINAL_PORT,
                isKimono = this.tmsRouteType == "KIMONO_DEFAULT",
                isKimono3 = this.tmsRouteType == "KIMONO_DEFAULT",
                capabilities = this.terminalCapabilities,
                agentEmail = "",
                agentId = "",
                callHomeTimeInMin = 2,
                serverTimeoutInSec = 6000
        )
}


