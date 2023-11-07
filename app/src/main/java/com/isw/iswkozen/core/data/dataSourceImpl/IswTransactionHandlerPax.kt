package com.isw.iswkozen.core.data.dataSourceImpl

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.interswitchng.smartpos.IswTxnHandler
import com.interswitchng.smartpos.emv.pax.services.POSDeviceImpl
import com.interswitchng.smartpos.models.PaymentModel
import com.interswitchng.smartpos.models.core.TerminalInfo
import com.interswitchng.smartpos.models.transaction.PaymentType
import com.interswitchng.smartpos.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.datasource.IswTransactionDataSource
import com.isw.iswkozen.core.data.models.TransactionData
import com.isw.iswkozen.core.data.models.toPaxTerminalInfo
import com.isw.iswkozen.core.data.utilsData.AccountType
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.paxDataToRequestIccData
import com.isw.iswkozen.core.database.dao.IswKozenDao
import com.isw.iswkozen.core.database.entities.createpaxTransactionData
import com.isw.iswkozen.core.network.models.fromPaxResponse
import com.isw.iswkozen.core.utilities.EmvHandler
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date

class IswTransactionHandlerPax(): IswTransactionDataSource {
    val channel = Channel<EmvMessage>()
    var emvEvents: EMVEvents ? = null
    var context: Context? = null

    var posDevice: POSDevice? = null
    var paxMainHandler: IswTxnHandler? = null
    var terminalInfo: TerminalInfo? = null
    var paxDao: IswKozenDao? = null


    private val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    val ioScope = uiScope.coroutineContext + Dispatchers.IO

    override suspend fun startTransaction(
        hasContactless: Boolean,
        hasContact: Boolean,
        amount: Long,
        amountOther: Long,
        transType: Int,
        emvEvents: EMVEvents
    ) {
       println("got here for transaction")
       this.emvEvents = emvEvents
        with(uiScope) {
           launch(ioScope) {
               for (message in channel) {
                   processMessage(message)
               }
           }

          launch(ioScope) {
              println(paxMainHandler == null)
              terminalInfo = readTerminalInfo().toPaxTerminalInfo()
              println("terminal info :::: ${terminalInfo}")
              terminalInfo?.let {
                  paxMainHandler?.setupTransaction(
                      amount = amount.toInt(),
                      terminalInfo = it,
                      scope = this,
                      channel = channel
                  )
              }
          }
        }

    }

    override suspend fun continuePaxTransaction() {
        super.continuePaxTransaction()

        println("got here for continue transaction")

        val result  = paxMainHandler?.startTransaction()
        withContext(Dispatchers.Main) {
            when (result) {
                EmvResult.CANCELLED -> {
                    val reason = "Error processing card transaction"
                   channel.send(EmvMessage.TransactionCancelled(-1, reason))
                }
                EmvResult.OFFLINE_APPROVED -> {
                   channel.send(EmvMessage.ProcessingTransaction)
                }
                EmvResult.OFFLINE_DENIED -> {
                    println("Transaction canceled")
                }
                EmvResult.ONLINE_REQUIRED -> {
                    channel.send(EmvMessage.ProcessingTransaction)
                }
            }
        }

    }

    private fun processMessage(emvMessage: EmvMessage) {
        println(emvMessage.javaClass.canonicalName)
        when (emvMessage) {

            // when card is detected
            is EmvMessage.CardDetected -> {
                println("me: CardDetected")
//                this.emvEvents?.onCardDetected()
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {
                this.emvEvents?.onInsertCard()
            }

            is EmvMessage.ChooseApplication -> {
                val candidateList = emvMessage.values
                var options = Array<String>(candidateList.size){"$it"}
                candidateList.forEachIndexed { index, candidateAID ->
                    options[index] = String(candidateAID?.appName!!)
                }
            }
            // when error is trown from clss

            is EmvMessage.ClssTransError -> {
                this.emvEvents?.onTransactionError(emvMessage.error)
            }

            // when card has been read
            is EmvMessage.CardRead -> {
                this.emvEvents?.onCardDetected(emvMessage.cardType)
            }

            is EmvMessage.ClssCardDetected -> {
                this.emvEvents?.onCardDetected(CardType.None)
            }

            // card details is for telpo terminals
            is EmvMessage.CardDetails -> {

            }

            // when card gets removed
            is EmvMessage.CardRemoved -> {
                this.emvEvents?.onRemoveCard()
            }
            // when user should enter pin
            is EmvMessage.EnterPin -> {
                println(emvMessage)
                this.emvEvents?.onPinInput()
            }

            // when user types in pin
            is EmvMessage.PinText -> {
                this.emvEvents?.onPinText(emvMessage.text)
            }

            // when pin has been validated
            is EmvMessage.PinOk -> {
                println("Called PIN OKAY")

            }

            // when the user enters an incomplete pin
            is EmvMessage.IncompletePin -> {
                println("Error incomplete")
                this.emvEvents?.onTransactionError("incomplete pin")
            }

            // when the user enters an incomplete pin
            is EmvMessage.EmptyPin -> {
               println("Error empty pin")
              this.emvEvents?.onTransactionError("Empty pin")
            }

            // when pin is incorrect
            is EmvMessage.PinError -> {
               println("Error pin Errir")
                this.emvEvents?.onTransactionError("in correct pin remain count :::${emvMessage.remainCount}")
            }

            // when user cancels transaction
            is EmvMessage.TransactionCancelled -> {
                println("canceled")
                this.emvEvents?.onRemoveCard()
                this.emvEvents?.onTransactionError(message = emvMessage.reason)

            }

            // when transaction is processing
            is EmvMessage.ProcessingTransaction -> {
               this.emvEvents?.onEmvProcessing("Card reading finished, Sending request to remote server")
               val accounttype = when(Constants.additionalTransactionInfo.accountType) {
                   AccountType.Credit -> com.interswitchng.smartpos.models.transaction.cardpaycode.request.AccountType.Credit
                   AccountType.Current -> com.interswitchng.smartpos.models.transaction.cardpaycode.request.AccountType.Current
                   AccountType.Default -> com.interswitchng.smartpos.models.transaction.cardpaycode.request.AccountType.Default
                   AccountType.Savings -> com.interswitchng.smartpos.models.transaction.cardpaycode.request.AccountType.Savings
               }
                val now = Date()
                val date = DateUtils.dateFormatter.format(now)
                val paymentModel = PaymentModel(
                    cardType = Constants.additionalTransactionInfo.cardType,
                    amount = Constants.additionalTransactionInfo.amount.toInt(),
                    paymentType = PaymentType.Card,
                    withReversal = false,
                    type = PaymentModel.TransactionType.CARD_PURCHASE,
                    originalDateAndTime = date
                )
                this.terminalInfo?.let {
                    val response = this.paxMainHandler?.processCardTransaction(
                        paymentModel = paymentModel,
                        accountType = accounttype,
                        terminalInfo = it,
                        purchaseType = PurchaseType.Card
                        )
                    val purchaseResponse = response?.let { it1 -> fromPaxResponse(transactionResultData = it1, terminalInfo = it) }
                    if (purchaseResponse != null) {
                        val iccData = paxDataToRequestIccData(response)
                        this.emvEvents?.onPaxTransactionDone(purchaseResponse, iccData)
                        val transactionData = createpaxTransactionData(response, it)
                       runBlocking {
                           paxDao?.insert(transactionData)
//                           job.cancel()
                       }
                    }

                }
            }
            else -> {

            }
        }
    }

    override suspend fun getTransactionData(): RequestIccData {
       return RequestIccData()
    }

    override suspend fun setEmvContect(context: Context) {
        this.context = context
        posDevice = POSDeviceImpl.create(context)
        val info = readTerminalInfo()
        if (info.tmsRouteType == "KIMONO_DEFAULT") {
            paxMainHandler = IswTxnHandler(posDevice, true)
        } else {
            paxMainHandler = IswTxnHandler(posDevice, false)
        }
    }

    override suspend fun setEmvPINMODE(pinMode: Int) {
    }

    override suspend fun setDaoForPaxHandler(dao: IswKozenDao) {
        super.setDaoForPaxHandler(dao)
        paxDao = dao
    }

    fun readTerminalInfo(): com.isw.iswkozen.core.data.models.TerminalInfo {
        val dataString = Prefs.getString(Constants.TERMINAL_INFO_KEY)
        return Gson().fromJson(dataString, com.isw.iswkozen.core.data.models.TerminalInfo::class.java) ?: com.isw.iswkozen.core.data.models.TerminalInfo()
    }
}