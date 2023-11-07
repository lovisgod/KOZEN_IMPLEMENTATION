package com.isw.iswkozen.core.data.dataInteractor

import EmvMessage
import com.interswitchng.smartpos.models.transaction.CardReadTransactionResponse
import com.isw.iswkozen.core.data.models.CardType
import com.isw.iswkozen.core.data.models.EmvCardType
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.network.models.PurchaseResponse

interface EMVEvents {

    fun onInsertCard()
    fun onRemoveCard()
    fun onPinInput()

    fun onPinText(text: String){
        println("pin text entered")
    }
    fun onEmvProcessing(message: String = "Please wait while we read card")
    fun onEmvProcessed(data: Any)

    fun onCardDetected(cardType: com.interswitchng.smartpos.models.transaction.cardpaycode.CardType) {
        println("card detected")
    }

    fun onTransactionError(message: String? = "") {
        println("card error")
    }

    fun onPaxTransactionDone(data: PurchaseResponse, iccData: RequestIccData) {
        println("pax card transaction done")
    }
}

interface SaveTransactionEvent {
    fun saveTransaction(purchaseResponse: PurchaseResponse)
}