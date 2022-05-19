package com.isw.iswkozen.core.network.CardLess.response


/**
 * This class captures the possible responses that
 * indicate the result of a pending payment status
 */
sealed class PaymentStatus {

    class Complete(val transaction: Transaction): PaymentStatus()
    class Pending(val transaction: Transaction): PaymentStatus()
    class Error(val transaction: Transaction?, val errorMsg: String?): PaymentStatus()
    object OngoingTimeout: PaymentStatus()
    object Timeout: PaymentStatus()
}

 fun getTransactionStatus(paymentStatus: PaymentStatus): Transaction? {

    return when(paymentStatus) {

        is PaymentStatus.Complete -> {
            paymentStatus.transaction
        }

        is PaymentStatus.Error -> {
            paymentStatus.transaction
        }

        is PaymentStatus.Pending -> {
            paymentStatus.transaction
        }
        else -> {
            null
        }
    }
}