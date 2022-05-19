package com.isw.iswkozen.core.network.CardLess


import com.isw.iswkozen.core.data.utilsData.Constants.BANKS_END_POINT
import com.isw.iswkozen.core.data.utilsData.Constants.CODE_END_POINT
import com.isw.iswkozen.core.data.utilsData.Constants.TRANSACTION_STATUS_QR
import com.isw.iswkozen.core.data.utilsData.Constants.TRANSACTION_STATUS_TRANSFER
import com.isw.iswkozen.core.data.utilsData.Constants.TRANSACTION_STATUS_USSD
import com.isw.iswkozen.core.network.CardLess.response.Bank
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.network.CardLess.response.Transaction
import com.isw.iswkozen.core.utilities.simplecalladapter.Simple
import retrofit2.http.*

internal interface IHttpService {

    @POST(CODE_END_POINT)
    fun getQrCode(@Body request: CardLessPaymentRequest): Simple<CodeResponse>

    @POST(CODE_END_POINT)
    fun getUssdCode(@Body request: CardLessPaymentRequest): Simple<CodeResponse>

    @POST(CODE_END_POINT)
    fun getAccountDetails(@Body request: CardLessPaymentRequest): Simple<CodeResponse>

    @GET(TRANSACTION_STATUS_QR)
    fun getQrTransactionStatus(@Query("merchantCode") merchantCode: String,
                             @Query("transactionReference") transactionReference: String): Simple<Transaction?>


    @GET(TRANSACTION_STATUS_USSD)
    fun getUssdTransactionStatus(@Query("merchantCode") merchantCode: String,
                             @Query("transactionReference") transactionReference: String): Simple<Transaction?>


    @GET(TRANSACTION_STATUS_TRANSFER)
    fun getTransferTransactionStatus(@Query("merchantCode") merchantCode: String,
                                     @Query("transactionReference") transactionReference: String): Simple<Transaction?>

    @GET(BANKS_END_POINT)
    fun getBanks(): Simple<List<Bank>?>

}