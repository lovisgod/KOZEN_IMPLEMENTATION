package com.isw.iswkozen.core.data.models

data class CardDetail(
    var pan: String = "",
    var expiry: String = "",
    var type: CardType = CardType.VERVE
 )


/**
 * This enum class represents the different cards
 * supported for EMV Card Chip transactions
 *
 * Note: The [code] parameter must match the AID's name tag in the xml config
 */
enum class CardType( val code: String) {
    MASTER("Master"),
    VISA("Visa"),
    VERVE("Verve"),
    AMERICANEXPRESS("AMEX"),
    CHINAUNIONPAY("CUP"),
    None("None"),
}