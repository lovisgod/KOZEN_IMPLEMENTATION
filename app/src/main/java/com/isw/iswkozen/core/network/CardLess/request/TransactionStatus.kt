package com.isw.iswkozen.core.network.CardLess.request


/**
 * This class captures the information for pending
 * transaction and is used to poll for the transaction status
 */
data class TransactionStatus(
        internal val reference: String,
        internal val merchantCode: String
)