package com.isw.iswkozen.core.network.models

import android.os.Parcelable
import androidx.room.Entity
import com.interswitchng.smartpos.models.core.TerminalInfo
import com.interswitchng.smartpos.models.transaction.CardReadTransactionResponse
import com.isw.iswkozen.core.database.entities.TransactionResultData
import com.isw.iswkozen.core.database.entities.createpaxTransactionData
import kotlinx.android.parcel.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Root


@Root(name = "purchaseResponse", strict = false)
@NamespaceList(
    Namespace( prefix = "ns2", reference = "http://interswitchng.com"),
    Namespace( prefix = "ns3", reference = "http://tempuri.org/ns.xsd")
)
@Entity(tableName = "transaction_table")
@Parcelize
data class PurchaseResponse(

    @field:Element(name = "description", required = false)
    var description: String = "",

    @field:Element(name = "field39", required = false)
    var responseCode: String = "",

    @field:Element(name = "authId", required = false)
    var authCode: String = "",

    @field:Element(name = "referenceNumber", required = false)
    var referenceNumber: String = "",

    @field:Element(name = "stan", required = false)
    var stan: String = "",

    @field:Element(name = "transactionChannelName", required = false)
    var transactionChannelName: String = "",

    @field:Element(name = "wasReceive", required = false)
    var wasReceive: Boolean = false,

    @field:Element(name = "wasSend", required = false)
    var wasSend: Boolean = false,

    @field:Element(name = "responseMessage", required = false)
    var responseMessage: String = "",

    @field:Element(name = "transactionRef", required = false)
    var transactionRef: String = "",

    @field: Element(name = "date", required = false)
    var date: Long = 0L,

    @field: Element(name = "scripts", required = false)
    var scripts: String = "",

    @field: Element(name = "responseDescription", required = false)
    var responseDescription: String? = null,

    @field: Element(name = "transactionId", required = false)
    var transactionId: String? = null,

    @field: Element(name = "transtype", required = false)
    var transTYpe: String? = null,

    @field: Element(name = "paymentType", required = false)
    var paymentType: String? = null,

    var transactionResultData: TransactionResultData? = null,

//    @field: Element(name = "data", required = false)
//    val inquiryResponse: InquiryResponse? = null,

    @field:Element(name = "remoteResponseCode", required = false)
    var remoteResponseCode: String = ""):Parcelable



        fun fromResponseData(transactionResultData: TransactionResultData?): PurchaseResponse {
             return transactionResultData.let { response ->
                return@let PurchaseResponse(
                    description = response!!.responseMessage,
                    responseCode = response.responseCode,
                    referenceNumber = response.ref.toString(),
                    stan = response.stan,
                    date = response.txnDate,
                    responseDescription = response.responseMessage,
                    transTYpe = response.paymentType,
                    paymentType = response.type.name,
                    transactionResultData = transactionResultData
                )
            }
        }

fun fromPaxResponse(transactionResultData: CardReadTransactionResponse, terminalInfo: TerminalInfo): PurchaseResponse {
    return transactionResultData.let { responsex ->
        val response = responsex.transactionResult!!
        return@let PurchaseResponse(
            description = response.responseMessage.toString(),
            responseCode = response.responseCode.toString(),
            referenceNumber = response.ref.toString(),
            stan = response.stan.toString(),
            date = response.time,
            responseDescription = response.responseMessage,
            transTYpe = response.type.name,
            paymentType = response.paymentType.name,
            authCode = response.authorizationCode,
            transactionResultData = createpaxTransactionData(transactionResultData, terminalInfo)
        )
    }
}

