package com.isw.iswkozen.core.data.utilsData

/**
 * This enum type identifies the
 * type of transactions issued out
 * to the EPMS through ISO message format
 */
enum class TransactionType {
    PayCode,
    Card,
    Cash
}


/**
 * This enum type identifies the
 * different Bank Account types
 */
enum class AccountType(val value: String) {
    Default("00"),
    Savings("10"),
    Current("20"),
    Credit("30")
}