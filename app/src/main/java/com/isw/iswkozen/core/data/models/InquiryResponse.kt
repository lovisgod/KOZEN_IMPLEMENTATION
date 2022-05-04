package com.isw.iswkozen.core.data.models


/**
 * This class captures the transaction response from EPMS
 * for a given purchase request
 */
data class InquiryResponse(// response code
        val narration: String?,
        val uuid: String,
        val approvedAmount: String,
        val transactionRef: String,
        val paymentCode: String,
        val customerId: String,
        val customerPhone: String,
        val customerEmail: String?,
        val collectionsAccountType: String,
        val surcharge: String,
        val itemDescription: String? = "",
        val biller: String? = "",
        val customerDescription: String? = "",
        val isAmountFixed: String? = "",
        val collectionsAccountNumber: String = ""
){
    companion object {
    }
}