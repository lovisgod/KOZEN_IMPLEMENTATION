
package com.isw.iswkozen.core.network.CardLess


import com.gojuno.koptional.Optional
import com.isw.iswkozen.core.network.CardLess.request.TransactionStatus
import com.isw.iswkozen.core.network.CardLess.response.Bank
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.network.CardLess.response.PaymentStatus



public interface HttpService {

    suspend fun initiateQrPayment(request: CardLessPaymentRequest): Optional<CodeResponse>

    suspend fun initiateUssdPayment(request: CardLessPaymentRequest): Optional<CodeResponse>

    suspend fun getBanks(): Optional<List<Bank>>

    suspend fun initiateTransferPayment(request: CardLessPaymentRequest): Optional<CodeResponse>


    suspend fun checkPayment(type: CardLessPaymentType, status: TransactionStatus): PaymentStatus
}