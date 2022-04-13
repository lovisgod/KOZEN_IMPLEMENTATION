package com.isw.iswkozen.core.network.models

import com.isw.iswkozen.core.data.models.EmvCard
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.TransactionType
import org.simpleframework.xml.Element

sealed class TransactionRequest {
    @field:Element(name = "terminalInformation", required = false)
    var terminalInformation: TerminalInformationRequest? = null

    @field:Element(name = "cardData", required = false)
    var cardData: CardData? = null

    @field:Element(name = "fromAccount", required = false)
    var fromAccount: String = ""

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
//        fun create(
//            type: TransactionType,
//            deviceName: String,
//            terminalInfo: TerminalInfo,
//            transactionInfo: TransactionInfo,
//            preAuthStan: String = "",
//            preAuthDateTime: String = "",
//            preAuthAuthId: String = ""
//        ): TransactionRequest {
//            val hasPin = transactionInfo.cardPIN.isNotEmpty()
//            val iswConfig = IswPos.getInstance().config
//
//            val request = when (type) {
//                TransactionType.Purchase, TransactionType.CashBack -> PurchaseRequest()
//                TransactionType.PreAuth -> PreAuthRequest()
//                TransactionType.Completion -> CompletionRequest()
//                TransactionType.Refund -> RefundRequest()
//                TransactionType.IFIS -> CashOutRequest()
//                else -> throw Exception("Invalid transaction type selected")
//            }
//
//            return request.apply {
//                terminalInformation =
//                    TerminalInformation.create(deviceName, terminalInfo, transactionInfo)
//                cardData = CardData.create(transactionInfo)
//                fromAccount = transactionInfo.accountType.name
//                minorAmount = transactionInfo.amount.toString()
//                stan = transactionInfo.stan
//                pinData = if (hasPin) PinData.create(transactionInfo) else null
//                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
//                this.purchaseType = transactionInfo.purchaseType.name
//                transactionId = System.currentTimeMillis().toString()
//
//                // completion fields
//                originalStan = preAuthStan
//                originalDateTime = preAuthDateTime
//                originalAuthId = preAuthAuthId
//            }
//        }
//
//        fun createCashOut(
//            type: TransactionType,
//            deviceName: String,
//            terminalInfo: TerminalInfo,
//            transactionInfo: TransactionInfo,
//            allTerminalInfo: AllTerminalInfo
//        ): TransactionRequest {
//            val hasPin = transactionInfo.cardPIN.isNotEmpty()
//            val iswConfig = IswPos.getInstance().config
//
//            val request =  CashOutRequest()
//
//            val agentDetails = allTerminalInfo.terminalInfoBySerials
//
//            return request.apply {
//                terminalInformation =
//                    TerminalInformation.create(deviceName, terminalInfo, transactionInfo)
//                cardData = CardData.create(transactionInfo)
//                fromAccount = transactionInfo.accountType.name
//                minorAmount = transactionInfo.amount.toString()
//                stan = transactionInfo.stan
//                pinData = if (hasPin) PinData.create(transactionInfo) else null
//                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
//                this.purchaseType = transactionInfo.purchaseType.name
//                transactionId = System.currentTimeMillis().toString()
//
//                customerName = agentDetails!!.merchantName
//                customerMobile = agentDetails.merchantPhoneNumber
//                customerId = agentDetails.merchantPhoneNumber
//                customerEmail = agentDetails.merchantEmail
//                retrievalReferenceNumber = transactionInfo.stan
//                requestType = "InquiryPayment"
//                paymentCode = Constants.PAYMENT_CODE
//                cardPan = transactionInfo.cardPAN
//                bankCbnCode = terminalInfo.terminalId.substring(1,4)
//                terminalId = terminalInfo.terminalId
//                app = "IFISBillPaymentCashoutRequest"
//            }
//        }
//
//        fun buildInquiry(
//            deviceName: String,
//            terminalInfo: TerminalInfo,
//            billPaymentInfo: IswBillPaymentInfo,
//            pan: String,
//            paymentInfo: IswPaymentInfo
//        ): TransactionRequest {
//
//            val iswConfig = IswPos.getInstance().config
//
//            val request =  BillPaymentRequest()
//
//            val iccData = IccData()
//            val transactionInfo = TransactionInfo("","", pan, pan,"",iccData,"","", paymentInfo.amount, paymentInfo.currentStan, PurchaseType.Card, AccountType.Savings, "")
//            return request.apply {
//                terminalInformation =
//                    TerminalInformation.create(deviceName, terminalInfo, transactionInfo)
//                cardData = CardData.create(transactionInfo)
//                //fromAccount = transactionInfo.accountType.name
//                minorAmount = transactionInfo.amount.toString()
//                stan = transactionInfo.stan
//                pinData =  null
//                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
//
//                customerMobile = billPaymentInfo.customerPhone
//                customerId = billPaymentInfo.customerId
//                customerEmail = billPaymentInfo.customerEmail ?: ""
//                requestType = "Inquiry"
//                paymentCode = billPaymentInfo.paymentCode
//                cardPan = pan
//                bankCbnCode = terminalInfo.terminalId.substring(1,4)
//                terminalId = terminalInfo.terminalId
//            }
//        }
//
//        fun buildCompletion(
//            deviceName: String,
//            terminalInfo: TerminalInfo,
//            transactionInfo: TransactionInfo,
//            inquiryResponse: InquiryResponse
//        ): TransactionRequest {
//
//            val hasPin = transactionInfo.cardPIN.isNotEmpty()
//            val iswConfig = IswPos.getInstance().config
//
//            val request =  BillPaymentRequest()
//            val amount: Int = if(inquiryResponse.isAmountFixed == "1") inquiryResponse.approvedAmount.toInt() else transactionInfo.amount
//            return request.apply {
//                terminalInformation =
//                    TerminalInformation.create(deviceName, terminalInfo, transactionInfo)
//                cardData = CardData.create(transactionInfo)
//                fromAccount = transactionInfo.accountType.name
//                minorAmount = amount.toString()
//                stan = transactionInfo.stan
//                pinData =  if (hasPin) PinData.create(transactionInfo) else null
//                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
//                customerMobile = inquiryResponse.customerPhone
//                customerId = inquiryResponse.customerId
//                customerEmail = inquiryResponse.customerEmail ?: ""
//                requestType = "Payment"
//                paymentCode = inquiryResponse.paymentCode
//                cardPan = transactionInfo.cardPAN
//                bankCbnCode = terminalInfo.terminalId.substring(1,4)
//                terminalId = terminalInfo.terminalId
//                uuid = inquiryResponse.uuid
//                transactionRef = inquiryResponse.transactionRef
//            }
//        }
//
//        fun buildAdvice(
//            deviceName: String,
//            terminalInfo: TerminalInfo,
//            transactionInfo: TransactionInfo,
//            inquiryResponse: InquiryResponse
//        ): TransactionRequest {
//
//            val hasPin = transactionInfo.cardPIN.isNotEmpty()
//            val iswConfig = IswPos.getInstance().config
//
//            val request =  BillPaymentRequest()
//            val amount: Int = if(inquiryResponse?.isAmountFixed == "1") inquiryResponse.approvedAmount.toInt() else transactionInfo.amount
//            return request.apply {
//                terminalInformation =
//                    TerminalInformation.create(deviceName, terminalInfo, transactionInfo)
//                cardData = CardData.create(transactionInfo)
//                fromAccount = transactionInfo.accountType.name
//                minorAmount = amount.toString()
//                stan = transactionInfo.stan
//                pinData =  if (hasPin) PinData.create(transactionInfo) else null
//                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
//                customerMobile = inquiryResponse.customerPhone
//                customerId = inquiryResponse.customerId
//                customerEmail = inquiryResponse.customerEmail ?: ""
//                requestType = "Advice"
//                paymentCode = inquiryResponse.paymentCode
//                cardPan = transactionInfo.cardPAN
//                bankCbnCode = terminalInfo.terminalId.substring(1,4)
//                terminalId = terminalInfo.terminalId
//                uuid = inquiryResponse.uuid
//                transactionRef = inquiryResponse.transactionRef
//            }
//        }

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
                keyLabel = if (!Constants.checkEmv()) "000006" else "000002"
                this.purchaseType = TransactionType.Card.name
                transactionId = System.currentTimeMillis().toString()
            }

        }
    }
}