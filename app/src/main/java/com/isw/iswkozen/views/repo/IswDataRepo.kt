package com.isw.iswkozen.views.repo

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.interswitchng.smartpos.shared.utilities.console
import com.isw.iswkozen.core.network.IsoCommunicator.nibss.NibssIsoServiceImpl
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswDetailsAndKeySourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswTransactionInteractor
import com.isw.iswkozen.core.data.models.IswTerminalModel
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.*
import com.isw.iswkozen.core.data.utilsData.Constants.EXCEPTION_CODE
import com.isw.iswkozen.core.database.dao.IswKozenDao
import com.isw.iswkozen.core.database.entities.TransactionResultData
import com.isw.iswkozen.core.database.entities.createTransResultData
import com.isw.iswkozen.core.database.entities.createTransactionResultFromCardLess
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentInfo
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentRequest
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentType
import com.isw.iswkozen.core.network.CardLess.HttpService
import com.isw.iswkozen.core.network.CardLess.request.TransactionStatus
import com.isw.iswkozen.core.network.CardLess.response.Bank
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.network.CardLess.response.PaymentStatus
import com.isw.iswkozen.core.network.CardLess.response.Transaction
import com.isw.iswkozen.core.network.kimonoInterface
import com.isw.iswkozen.core.network.models.*
import com.isw.iswkozen.core.utilities.DeviceUtils
import com.pixplicity.easyprefs.library.Prefs
import com.pos.sdk.security.POIHsmManage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class IswDataRepo(val iswConfigSourceInteractor: IswConfigSourceInteractor,
                  val iswDetailsAndKeySourceInteractor: IswDetailsAndKeySourceInteractor,
                  val iswTransactionInteractor: IswTransactionInteractor,
                  val context: Context,
                  val kimonoInterface: kimonoInterface,
                  val nibssIsoServiceImpl: NibssIsoServiceImpl,
                  val cardlessService: HttpService,
                  var iswKozenDao: IswKozenDao
                    ) {


    val dispatcher: CoroutineDispatcher = Dispatchers.IO


    /**
     * CARD-LESS PAYMENT SECTIONS*/

    suspend fun initiateTransfer(req: CardLessPaymentRequest):
            Optional<CodeResponse> {
        try {
            return withContext(dispatcher) {
                return@withContext cardlessService.initiateTransferPayment(req)
            }
        } catch (e: Exception) {
            Log.e("load virtual error =>", e.stackTraceToString())
            return None
        }
    }


    suspend fun initiateUssd(req: CardLessPaymentRequest):
            Optional<CodeResponse> {
        try {
            return withContext(dispatcher) {
                return@withContext cardlessService.initiateUssdPayment(req)
            }
        } catch (e: Exception) {
            Log.e("ussd error =>", e.stackTraceToString())
            return None
        }
    }

    suspend fun initiateQrcode(req: CardLessPaymentRequest):
            Optional<CodeResponse> {
        try {
            return withContext(dispatcher) {
                return@withContext cardlessService.initiateQrPayment(req)
            }
        } catch (e: Exception) {
            Log.e("qr error =>", e.stackTraceToString())
            return None
        }
    }

    suspend fun checkPaymentStatus(transactionReference: String,
                                   merchantCode: String, paymentType: CardLessPaymentType):
            PaymentStatus? {
        try {
            return withContext(dispatcher) {
                val status = TransactionStatus(
                    reference = transactionReference,
                    merchantCode = merchantCode
                )
                return@withContext cardlessService.checkPayment(type = paymentType,
                status = status)
            }
        } catch (e: Exception) {
            Log.e("qr error =>", e.stackTraceToString())
            return null
        }
    }


    suspend fun loadUssdBanks():
            Optional<List<Bank>> {
        try {
            return withContext(dispatcher) {
                return@withContext cardlessService.getBanks()
            }
        } catch (e: Exception) {
            Log.e("ussd bank error =>", e.stackTraceToString())
            return None
        }
    }





    // keys write, download and parameter downloads

    suspend fun downLoadNibbsKey(): Boolean {
        try {
            return withContext(dispatcher) {
                var terminalInfo = readterminalDetails()

                // check if its EPMS

                val port = Constants.ISW_TERMINAL_PORT.toInt()
                val ip = Constants.ISW_TERMINAL_IP
//                val isEPMS = port == Constants.ISW_TERMINAL_PORT
//                        && ip == Constants.ISW_TERMINAL_IP

                // getResult clear master key
                val cms = Constants.getCMS(false)
                val success = nibssIsoServiceImpl.downloadKey(
                    terminalInfo?.terminalCode.toString(),
//                    "2ISW0001",
                    ip, port, cms, "9A0000",
                    "9B0000",
                    "9G0000"
                )
                if (success) {
                    downloadNibbsTerminalDetails(terminalInfo?.terminalCode.toString())
                }
                return@withContext success
            }
        } catch (e: Exception) {
            Log.e("key error", e.stackTraceToString())
            return false
        }
    }

    suspend fun writePinKey(): Int {
        try {
            return withContext(dispatcher) {
                iswDetailsAndKeySourceInteractor.writePinKey(
                    keyIndex = KeysUtils.PINKEY_INDEX,
                    keyData = KeysUtils.CTMS.productionCMS() // this can be test
                )
            }
        } catch (e: Exception) {
            Log.e("key error", e.stackTraceToString())
            return EXCEPTION_CODE
        }
    }


    suspend fun  writeDukptKey() : Int {
        try {
            return withContext(dispatcher) {
                iswDetailsAndKeySourceInteractor.writeDukPtKey(
                    keyIndex = KeysUtils.DUKPTKEY_INDEX,
                    keyData = KeysUtils.testIPEK(),
                    KsnData = KeysUtils.testKSN()
                )
            }
        } catch (e:Exception) {
            Log.e("key error", e.stackTraceToString())
            return  EXCEPTION_CODE
        }
    }

    suspend fun readterminalDetails() : TerminalInfo? {
        try {
            return withContext(dispatcher) {
              iswDetailsAndKeySourceInteractor.readTerminalInfo()
            }
        } catch (e:Exception) {
            Log.e("terminal details error", e.stackTraceToString())
            return  null
        }
    }

    suspend fun downloadTerminalDetails(): Boolean {
        try {
            return withContext(dispatcher) {
                println("device serial => ${DeviceUtils.getDeviceSerialKozen().toString()}")
                var data = IswTerminalModel("", "${DeviceUtils.getDeviceSerialKozen().toString()}", false)
                iswDetailsAndKeySourceInteractor.downloadTerminalDetails(data)




                // read the data from saved pref
                var info = readterminalDetails()

                println("info =>  ${info.toString()}")
                println("info =>  ${info?.terminalCountryCode}")
                println("info =>  ${info?.terminalCode}")
                println("nibbskey => ${info?.nibbsKey}")
                println("tmsroutetype => ${info?.tmsRouteType}")
                if (info != null) {

                    if (info?.terminalCode == "Terminal") {
                        return@withContext false
                    }

                    // load the details into the terminal
                    if (info.tmsRouteType != "KIMONO_DEFAULT" ) {
                        Prefs.putString("NIBSS_KEY", info.nibbsKey)
                        Prefs.putBoolean("ISNIBSS", true)
                        downLoadNibbsKey()
                    } else {
                        Prefs.putBoolean("ISNIBSS", false)
                        loadTerminal(info)
                        getISWToken(info)
                    }

                }
                true
            }
        } catch (e:Exception) {
            Log.e("term data down error", e.stackTraceToString())
            return  false
        }
    }


    suspend fun downloadNibbsTerminalDetails(terminalId: String): Boolean {
        try {
            return withContext(dispatcher) {
                println("device serial => ${DeviceUtils.getDeviceSerialKozen().toString()}")
                var data = IswTerminalModel("", "${DeviceUtils.getDeviceSerialKozen().toString()}", false)
                val port = Constants.ISW_TERMINAL_PORT.toInt()
                val ip = Constants.ISW_TERMINAL_IP
                val isEPMS = port == Constants.ISW_TERMINAL_PORT
                        && ip == Constants.ISW_TERMINAL_IP

                val nibbsInfo =nibssIsoServiceImpl.downloadTerminalDetails(
                    terminalId = terminalId,
                    ip = ip,
                    port = port,
                    processingCode = "9C0000")

                // read the data
//                var info = readterminalDetails()

                println("info =>  ${nibbsInfo.toString()}")
                println("info =>  ${nibbsInfo?.terminalCountryCode}")
                println("info =>  ${nibbsInfo?.terminalCode}")
                if (nibbsInfo != null) {
                    iswDetailsAndKeySourceInteractor.saveTerminalInfo(nibbsInfo)
                    // load the details into the terminal
                    loadTerminal(nibbsInfo)
                }
                true
            }
        } catch (e:Exception) {
            Log.e("term data down error", e.stackTraceToString())
            return  false
        }
    }


    suspend fun startTransaction( hasContactless: Boolean = true,
                                  hasContact: Boolean = true,
                                  amount: Long,
                                  amountOther: Long,
                                  transType: Int, contextX: Context, emvEvents: EMVEvents): Boolean {
        try {
            return withContext(dispatcher) {
                println("transaction started")
                iswTransactionInteractor.setEmvContect(contextX)
                if (Prefs.getBoolean("ISNIBSS", false)) {
                    iswTransactionInteractor.setPinMode(POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK)
                } else {
                    iswTransactionInteractor.setPinMode(POIHsmManage.PED_PINBLOCK_FETCH_MODE_DUKPT)
                }
                iswTransactionInteractor.startTransaction(
                    hasContactless, hasContact, amount, amountOther, transType, emvEvents)
                   true
            }
        } catch (e:Exception) {
            Log.e("startTransaction error", e.stackTraceToString())
            return  false
        }
    }

    suspend fun getTransactionData(): RequestIccData {
        try {
            return withContext(dispatcher) {
              iswTransactionInteractor.getTransactionData()
            }
        } catch (e:Exception) {
            Log.e("get trans data error", e.stackTraceToString())
            return  RequestIccData()
        }
    }


    suspend fun makePayCodeRequest(
        transactionName: String,
        amount: String,
        code: String,
        terminalData: TerminalInfo
    ): PurchaseResponse {
        val paymentInfo = CardLessPaymentInfo(
            amount = amount.toInt(),
            Constants.getNextStan(),
            surcharge = 0,
            additionalAmounts = 0)
        try {
            return withContext(dispatcher) {

                println("is nibbs =< ${Prefs.getBoolean("ISNIBSS", false)}")
                if (Prefs.getBoolean("ISNIBSS", false)) {
                    val port = Constants.ISW_TERMINAL_PORT.toInt()
                    val ip = Constants.ISW_TERMINAL_IP
                    val responseIso = nibssIsoServiceImpl.getPaycodeRequest(
                        ip, port, code,
                        terminalData,
                       amount = amount.toLong()
                    )
                    var resultData = responseIso.let {
                        var transaction = Transaction.Companion.getForPayCode(
                            description = responseIso.description,
                            amount = paymentInfo.amount,
                            responseCode = responseIso.responseCode
                        )
                        createTransactionResultFromCardLess(
                            paymentStatus = transaction,
                            paymentInfo = paymentInfo,
                            transactionName = transactionName,
                            transactionType = TransactionType.PayCode,
                            terminalData = terminalData
                        )
                    }
                    if (resultData != null) {
                        println("got here for saving")
                        println(resultData)
                        saveTransactionResult(resultData)
                    }
                    responseIso.transTYpe = transactionName
                    responseIso.transactionResultData = resultData
                    responseIso.paymentType = "Card"
                    responseIso

                } else {
                    PurchaseResponse(
                        responseCode = "0x000",
                        description = "An error occurred,\nTransaction not permitted for this route",
                        responseMessage = "An error occurred \nTransaction not permitted for this route",
                        paymentType = "Card"
                    )
                }
            }

        } catch (e:Exception) {
            Log.e("get trans data error", e.stackTraceToString())
            val purchaseResponse = PurchaseResponse(
                responseCode = "0x000",
                description = "An error occurred",
                responseMessage = "An error occurred",
                paymentType = "Card"
            )

            var transaction = Transaction.Companion.getForError("An error occurred")
            var resultData = createTransactionResultFromCardLess(
                paymentStatus = transaction,
                paymentInfo = paymentInfo,
                transactionName = transactionName,
                transactionType = TransactionType.PayCode,
                terminalData = terminalData
            )

            if (resultData != null) {
                println("got here for saving")
                println(resultData)
                saveTransactionResult(resultData)
            }
            purchaseResponse?.transactionResultData = resultData
            purchaseResponse.paymentType = "Card"
            purchaseResponse.transTYpe = transactionName
            return  purchaseResponse


        }
    }

    suspend fun makeOnlineRequest(transactionName: String, iccData: RequestIccData, terminalData: TerminalInfo): PurchaseResponse {
        try {
            return withContext(dispatcher) {
               var purchaseRequest = TransactionRequest.createPurchaseRequest(terminalInfoX = terminalData, requestData = iccData)
               return@withContext when (transactionName) {
                   "purchase" -> {
                       println("is nibbs =< ${Prefs.getBoolean("ISNIBSS", false)}")
                       if (Prefs.getBoolean("ISNIBSS", false)) {
                           val port = Constants.ISW_TERMINAL_PORT.toInt()
                           val ip = Constants.ISW_TERMINAL_IP
                           val responseIso = nibssIsoServiceImpl.get200Request(
                               ip, port,
                               terminalData,
                               iccData,
                               Constants.additionalTransactionInfo.accountType
                           )
                           var resultData = responseIso.let {
                               createTransResultData(
                                   it,
                                   iccData,
                                   transactionName,
                                   TransactionType.Card,
                                   terminalData
                               )
                           }
                               if (resultData != null) {
                                   println("got here for saving")
                                   println(resultData)
                                   saveTransactionResult(resultData)
                               }
                               responseIso?.transTYpe = transactionName
                               responseIso?.transactionResultData = resultData
                               responseIso?.paymentType = "Card"
                               responseIso!!

                       } else {
                           val token = Prefs.getString("TOKEN", "")
                           var response = kimonoInterface.makePurchase(
                               request = purchaseRequest as PurchaseRequest
                           ).run()
                           if (response.isSuccessful) {
                               val purchaseResponse = response.body()
                               var resultData = purchaseResponse?.let {
                                   createTransResultData(
                                       it,
                                       iccData,
                                       transactionName,
                                       TransactionType.Card,
                                       terminalData
                                   )
                               }
                               if (resultData != null) {
                                   println("got here for saving")
                                   println(resultData)
                                   saveTransactionResult(resultData)
                               }
                               purchaseResponse?.transTYpe = transactionName
                               purchaseResponse?.transactionResultData = resultData
                               purchaseResponse?.paymentType = "Card"
                               purchaseResponse!!
                           } else {
                               val purchaseResponse =  PurchaseResponse(description = "An error occured",
                                   responseCode = "${response.code()}", responseMessage = "An error occurred")
                               val resultData = purchaseResponse?.let {
                                   createTransResultData(
                                       it,
                                       iccData,
                                       transactionName,
                                       TransactionType.Card,
                                       terminalData
                                   )
                               }
                               saveTransactionResult(resultData)
                               purchaseResponse?.transTYpe = transactionName
                               purchaseResponse?.transactionResultData = resultData
                               purchaseResponse.paymentType = "Card"
                               purchaseResponse
                           }
                       }
                   }

                   "cashout" -> {
                       var cashoutRequest = TransactionRequest.createCashoutRequest(terminalInfoX = terminalData, requestData = iccData)
                       val token = Prefs.getString("TOKEN", "")
                       var response = kimonoInterface.makeCashout(
                           request = cashoutRequest as TransferRequest,
                           token = token
                       ).run()
                       if (response.isSuccessful) {
                           val purchaseResponse = response.body()
                           var resultData = purchaseResponse?.let {
                               createTransResultData(
                                   it,
                                   iccData,
                                   transactionName,
                                   TransactionType.Card,
                                   terminalData
                               )
                           }
                           if (resultData != null) {
                               println("got here for saving")
                               saveTransactionResult(resultData)
                           }
                           purchaseResponse?.transTYpe = transactionName
                           purchaseResponse?.transactionResultData = resultData
                           purchaseResponse?.paymentType = "Card"
                           purchaseResponse!!
                       } else {
                           val purchaseResponse =  PurchaseResponse(description = "An error occured",
                               responseCode = "${response.code()}", responseMessage = "An error occurred")
                           val resultData = purchaseResponse?.let {
                               createTransResultData(
                                   it,
                                   iccData,
                                   transactionName,
                                   TransactionType.Card,
                                   terminalData
                               )
                           }
                           saveTransactionResult(resultData)
                           purchaseResponse?.transTYpe = transactionName
                           purchaseResponse?.transactionResultData = resultData
                           purchaseResponse.paymentType = "Card"
                           purchaseResponse
                       }
                   }

                   else -> {
                       println("got here instead")
                       val token  = Prefs.getString("TOKEN", "")
                       var response  = kimonoInterface.makePurchase(
                           request = purchaseRequest as PurchaseRequest
                       ).run()
                       if (response.isSuccessful) {
                           response.body()!!
                       } else {
                           PurchaseResponse(description = "An error occured",
                               responseCode = "${response.code()}",
                               responseMessage = "An error occurred",
                               paymentType = "Card")
                       }
                   }
               }
            }
        } catch (e:Exception) {
            Log.e("get trans data error", e.stackTraceToString())
            val purchaseResponse = PurchaseResponse(
                responseCode = "0x000",
                description = "An error occurred",
                responseMessage = "An error occurred",
                paymentType = "Card"
            )
            var resultData = purchaseResponse.let {
                createTransResultData(
                    it,
                    iccData,
                    transactionName,
                    TransactionType.Card,
                    terminalData
                )
            }

            if (resultData != null) {
                println("got here for saving")
                println(resultData)
                saveTransactionResult(resultData)
            }
            purchaseResponse?.transactionResultData = resultData
            purchaseResponse.paymentType = "Card"
            purchaseResponse.transTYpe = transactionName
            return  purchaseResponse


        }
    }




    suspend fun eraseKeys() : Int {
        try {
            return withContext(dispatcher) {
               iswDetailsAndKeySourceInteractor.eraseKey()
            }
        } catch (e:Exception) {
            Log.e("key error", e.stackTraceToString())
            return  EXCEPTION_CODE
        }
    }

    suspend fun loadTerminal(terminalData: TerminalInfo = TerminalInfo()): Boolean {

        try {
            return withContext(dispatcher) {

                iswConfigSourceInteractor.loadTerminal(terminalData)
                return@withContext true
            }
        } catch (e:Exception) {
            Log.e("loadTerminalError", e.stackTraceToString())
            return false
        }
    }


    suspend fun getISWToken(terminalData: TerminalInfo = TerminalInfo()): Boolean {

        try {
            return withContext(dispatcher) {
                var terminalInformationRequest =
                    TerminalInformationRequest().fromTerminalInfo("", terminalData, false)
                var tokenRequestModel = TokenRequestModel()
                tokenRequestModel.terminalInformation = terminalInformationRequest
                iswDetailsAndKeySourceInteractor.getISWToken(tokenRequestModel)
                return@withContext true
            }
        } catch (e:Exception) {
            Log.e("getIswToken", e.stackTraceToString())
            return false
        }
    }

    suspend fun loadAllConfig(): Boolean {
        console.log("load all config", "Loading config")
       try {
           return withContext(dispatcher) {

               iswConfigSourceInteractor.loadAid()
               iswConfigSourceInteractor.loadCapk()
               iswConfigSourceInteractor.loadVisa()
               iswConfigSourceInteractor.loadExceptionFile()
               iswConfigSourceInteractor.loadRevocationIPK()
               iswConfigSourceInteractor.loadUnionPay()
               iswConfigSourceInteractor.loadMasterCard()
               iswConfigSourceInteractor.loadDiscover()
               iswConfigSourceInteractor.loadAmex()
               iswConfigSourceInteractor.loadMir()
               iswConfigSourceInteractor.loadVisaDRL()
               iswConfigSourceInteractor.loadAmexDRL()
               iswConfigSourceInteractor.loadService()
               return@withContext true
           }
       } catch (e: Exception) {
           Log.e("loadConfigError", e.stackTraceToString())
           return false
       }
    }

    suspend fun resetConfig(): Boolean {
        try {
            return withContext(dispatcher) {
                // first download terminal parameters
                // the download parameter function has the loadparameter function implemented in it
                val downloadedGood = downloadTerminalDetails()
                if (!downloadedGood) {
                    return@withContext false
                }
                loadAllConfig()
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e("loadConfigError", e.stackTraceToString())
            return false
        }
    }

    suspend fun loadAllkeys(): Int {
        try {
            return withContext(dispatcher) {
               // run all the keys related functions
                eraseKeys()
//                writePinKey()
                writeDukptKey()
                return@withContext 0
            }
        } catch (e: Exception) {
            Log.e("loadallkeyError", e.stackTraceToString())
            return 99
        }
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun saveTransactionResult(result: TransactionResultData) {
        iswKozenDao.insert(resultData = result)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTransactionResult(result: TransactionResultData) {
        iswKozenDao.updateTransaction(resultData = result)
    }

    val allTransactions: Flow<List<TransactionResultData>> = iswKozenDao.getAllTransaction()
}