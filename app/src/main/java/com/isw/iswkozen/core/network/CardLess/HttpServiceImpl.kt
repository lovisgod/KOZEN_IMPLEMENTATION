package com.isw.iswkozen.core.network.CardLess

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.isw.iswkozen.core.network.CardLess.request.TransactionStatus
import com.isw.iswkozen.core.network.CardLess.response.Bank
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.network.CardLess.response.PaymentStatus
import com.isw.iswkozen.core.network.CardLess.response.Transaction
import com.isw.iswkozen.core.utilities.Logger
import com.isw.iswkozen.core.utilities.simplecalladapter.Simple
import com.pixplicity.easyprefs.library.Prefs
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class HttpServiceImpl(private val httpService: IHttpService) : HttpService {

    val logger by lazy { Logger.with(this.javaClass.name) }


    override suspend fun getBanks(): Optional<List<Bank>> {
        val banks = httpService.getBanks().await()
        val banksResponse = banks.first
        return when (banksResponse) {
            null -> None
            else ->  Some(banksResponse)
        }
    }


    override suspend fun initiateQrPayment(request: CardLessPaymentRequest): Optional<CodeResponse> {
        val code = httpService.getQrCode(request).await()
        val codeResponse = code.first
        return when (codeResponse) {
            null -> None
            else -> Some(codeResponse)
        }
    }

    override suspend fun initiateTransferPayment(request: CardLessPaymentRequest): Optional<CodeResponse> {
        var token = "Bearer ${Prefs.getString("token", "")}"
        val response = httpService.getAccountDetails(request).await()
        val codeResponse = response.first
        return when (codeResponse) {
            null -> None
            else -> Some(codeResponse)
        }
    }

    override suspend fun initiateUssdPayment(request: CardLessPaymentRequest): Optional<CodeResponse> {
        val response = httpService.getUssdCode(request).await()
        val codeResponse = response.first
        return when (codeResponse) {
            null -> None
            else -> Some(codeResponse)
        }
    }

    override suspend fun checkPayment(type: CardLessPaymentType, status: TransactionStatus): PaymentStatus {

        // check status based on the transaction type
        val transactionResponse = when (type) {
            CardLessPaymentType.USSD -> httpService.getUssdTransactionStatus(status.merchantCode, status.reference).await()
            CardLessPaymentType.Transfer -> httpService.getTransferTransactionStatus(status.merchantCode, status.reference).await()
            else -> httpService.getQrTransactionStatus(status.merchantCode, status.reference).await()
        }

        val transaction = transactionResponse.first
        println("second available => ${transactionResponse.second}")
        println("transaction xxx : ${transaction}")
        return when {
            transaction == null || transaction.isError() -> PaymentStatus.Error(
                Transaction.getForError(transactionResponse.second.toString()), transactionResponse.second)
            transaction.isCompleted() -> PaymentStatus.Complete(transaction)
            transaction.isPending() -> PaymentStatus.Pending(transaction)
            else -> {
                // some other error occurred
                // terminate loop and return
                PaymentStatus.Error(Transaction.getForError(transactionResponse.second.toString()), transactionResponse.second)
            }
        }
    }


     suspend fun callHome(request: CardLessPaymentRequest): Optional<CodeResponse> {
       //send XML and Receive XML

        val response = httpService.getUssdCode(request).await()
        val codeResponse = response.first
        return when (codeResponse) {
            null -> None
            else -> Some(codeResponse)
        }
    }



    private suspend fun <T> Simple<T>.await(): Pair<T?, String?> {
        return suspendCoroutine { continuation ->
            process { response, t ->
                val message =  t?.message ?: t?.localizedMessage

                // log errors
                if (message != null) logger.log(message)
                // pair result and error
                val result = Pair(response, message)
                // return response
                continuation.resume(result)
            }
        }
    }
}