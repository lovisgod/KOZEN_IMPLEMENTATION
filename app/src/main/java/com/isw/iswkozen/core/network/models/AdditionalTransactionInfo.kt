package com.isw.iswkozen.core.network.models

import android.os.Parcelable
import com.isw.iswkozen.core.data.models.CardType
import com.isw.iswkozen.core.data.utilsData.AccountType
import com.isw.iswkozen.core.data.utilsData.TransactionType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdditionalTransactionInfo (
    var amount: String  = "",
    var surcharge: String = "",
    var extendedTransactionType: String = "6101",
    var receivingInstitutionId: String = "",
    var destinationAccountNumber: String = "",
    var fromAccount: String = "",
    var accountType: AccountType = AccountType.Default,
    var cardType: com.interswitchng.smartpos.models.transaction.cardpaycode.CardType = com.interswitchng.smartpos.models.transaction.cardpaycode.CardType.None

 ): Parcelable