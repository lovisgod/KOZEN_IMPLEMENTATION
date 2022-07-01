package com.isw.iswkozen.core.network.CardLess.response


/**
 * This class represents the response status for a triggered purchase transaction
 */
class Transaction (
        internal val id: Int,
        internal val amount: Int,
        internal val transactionReference: String,
        internal val responseCode: String,
        internal val currencyCode: String?,
        internal val paymentCancelled: Boolean,
        internal val bankCode: String?,
        internal val remittanceAmount: Int,
        val responseDescription: String?,
) {



    fun isCompleted(): Boolean = responseCode == CodeResponse.OK
    fun isPending(): Boolean = responseCode == CodeResponse.PENDING
    fun isError(): Boolean = !isCompleted() && !isPending()

    companion object {
        fun default(): Transaction {
            val int = 0
            val str = ""
            return Transaction(int, int, str, str, str, false, str, 0, str)
        }

        fun getForPayCode(description: String, amount: Int, responseCode: String): Transaction {
            val int = 0
            val str = ""
            return Transaction(int, amount = amount, str, responseCode, str, false, str, 0, description)
        }

        fun getForError(description: String): Transaction {
            val int = 0
            val str = "0X00"
            return Transaction(int, int, str, str, str, false, str, 0, description)
        }

    }
}