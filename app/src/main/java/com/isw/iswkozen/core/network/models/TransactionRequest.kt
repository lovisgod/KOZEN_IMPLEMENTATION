package com.isw.iswkozen.core.network.models

import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.AccountType
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.TransactionType
import com.isw.iswkozen.core.utilities.DateUtils
import org.simpleframework.xml.Element
import java.util.*

sealed class TransactionRequest {
    @field:Element(name = "terminalInformation", required = false)
    var terminalInformation: TerminalInformationRequest? = null

    @field:Element(name = "cardData", required = false)
    var cardData: CardData? = null

    @field:Element(name = "fromAccount", required = false)
    var fromAccount: String = AccountType.Default.name

    @field:Element(name = "receivingInstitutionId", required = false)
    var receivingInstitutionId: String = ""

    @field:Element(name = "destinationAccountNumber", required = false)
    var destinationAccountNumber: String = ""

    @field:Element(name = "extendedTransactionType", required = false)
    var extendedTransactionType: String = "6101"

    @field:Element(name = "originalTransmissionDateTime", required = false)
    var originalTransmissionDateTime: String = DateUtils.universalDateFormat.format(Date())

    @field:Element(name = "stan", required = false)
    var stan: String = ""

    @field:Element(name = "minorAmount", required = false)
    var minorAmount: String = ""

    @field:Element(name = "pinData", required = false)
    var pinData: PinData? = null

    @field:Element(name = "keyLabel", required = false)
    var keyLabel: String = ""

    @field:Element(name = "originalAuthRef", required = false)
    var originalAuthId: String = ""

    @field:Element(name = "originalStan", required = false)
    var originalStan: String = ""

    @field:Element(name = "originalDateTime", required = false)
    var originalDateTime: String = ""

    @field:Element(name = "purchaseType", required = false)
    var purchaseType: String = ""

    @field:Element(name = "transactionId", required = false)
    var transactionId: String = ""

    @field:Element(name = "app", required = false)
    var app: String = ""

    @field:Element(name = "retrievalReferenceNumber", required = false)
    var retrievalReferenceNumber: String = ""

    @field:Element(name = "bankCbnCode", required = false)
    var bankCbnCode: String = ""

    @field:Element(name = "cardPan", required = false)
    var cardPan: String = ""

    @field:Element(name = "customerEmail", required = false)
    var customerEmail: String = ""

    @field:Element(name = "customerId", required = false)
    var customerId: String = ""

    @field:Element(name = "customerMobile", required = false)
    var customerMobile: String = ""

    @field:Element(name = "paymentCode", required = false)
    var paymentCode: String = ""

    @field:Element(name = "terminalId", required = false)
    var terminalId: String = ""

    @field:Element(name = "customerName", required = false)
    var customerName: String = ""

    @field:Element(name = "requestType", required = false)
    var requestType: String = ""

    @field:Element(name = "transactionRef", required = false)
    var transactionRef: String = ""

    @field:Element(name = "uuid", required = false)
    var uuid: String = ""

    @field:Element(name = "additionalAmounts", required = false)
    var additionalAmounts: String = ""

    @field:Element(name = "surcharge", required = false)
    var surcharge: String = ""

    companion object {

        fun createPurchaseRequest(terminalInfoX: TerminalInfo, requestData: RequestIccData = RequestIccData()): TransactionRequest {
            val request = PurchaseRequest()
            var cardDataX = CardData.create(requestData)

            return request.apply {
                terminalInformation =
                    TerminalInformationRequest().createForrequest(
                        terminalInfo = terminalInfoX, haspin = requestData.haspin!!)
                cardData = cardDataX
                minorAmount = requestData.TRANSACTION_AMOUNT
                stan = Constants.getNextStan()
                pinData = if (requestData.haspin!!) PinData.create(Constants.memoryPinData) else null
                keyLabel = if (Constants.checkEnv()) "000006" else "000002"
                this.purchaseType = TransactionType.Card.name
                transactionId = System.currentTimeMillis().toString()
            }

        }

        fun createCashoutRequest(terminalInfoX: TerminalInfo, requestData: RequestIccData = RequestIccData()): TransactionRequest {
            val request = TransferRequest()
            var cardDataX = CardData.create(requestData)

            return request.apply {
                terminalInformation =
                    TerminalInformationRequest().createForrequest(
                        terminalInfo = terminalInfoX, haspin = requestData.haspin!!)
                cardData = cardDataX
                minorAmount = requestData.TRANSACTION_AMOUNT
                stan = Constants.getNextStan()
                pinData = if (requestData.haspin!!) PinData.create(Constants.memoryPinData) else null
                keyLabel = if (Constants.checkEnv()) "000006" else "000002"
                this.purchaseType = TransactionType.Card.name
                transactionId = System.currentTimeMillis().toString()
                extendedTransactionType = Constants.additionalTransactionInfo.extendedTransactionType
                surcharge = Constants.additionalTransactionInfo.surcharge
                receivingInstitutionId = Constants.additionalTransactionInfo.receivingInstitutionId
                destinationAccountNumber = Constants.additionalTransactionInfo.destinationAccountNumber
                fromAccount = Constants.additionalTransactionInfo.fromAccount

            }

        }
    }
}