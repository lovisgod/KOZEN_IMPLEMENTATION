package com.isw.iswkozen.views.repo

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
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
import com.isw.iswkozen.core.network.kimonoInterface
import com.isw.iswkozen.core.network.models.*
import com.isw.iswkozen.core.utilities.DeviceUtils
import com.isw.iswkozen.core.utilities.DisplayUtils
import com.pixplicity.easyprefs.library.Prefs
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
                  var iswKozenDao: IswKozenDao
                    ) {


    val dispatcher: CoroutineDispatcher = Dispatchers.IO

    // keys wite, download and parameter downloads

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
                nibssIsoServiceImpl.downloadKey(
//                    terminalInfo?.terminalCode.toString(),
                    "205777VZ",
                    ip, port, cms, "9A0000",
                    "9B0000",
                    "9G0000"
                )
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
                    keyData = KeysUtils.productionIPEK(),
                    KsnData = KeysUtils.productionKSN()
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
                if (info != null) {

                    // load the details into the terminal
                    loadTerminal(info)
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
                var info = readterminalDetails()

                println("info =>  ${nibbsInfo.toString()}")
                println("info =>  ${nibbsInfo?.terminalCountryCode}")
                println("info =>  ${info?.terminalCode}")
                if (info != null) {

                    // load the details into the terminal
                    loadTerminal(info)
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
                iswTransactionInteractor.setEmvContect(contextX)
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

    suspend fun makeOnlineRequest(transactionName: String, iccData: RequestIccData, terminalData: TerminalInfo): PurchaseResponse {
        try {
            return withContext(dispatcher) {
               var purchaseRequest = TransactionRequest.createPurchaseRequest(terminalInfoX = terminalData, requestData = iccData)
               return@withContext when (transactionName) {
                   "purchase" -> {
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
                               saveTransactionResult(resultData)
                           }
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
                           purchaseResponse
                       }
                   }

                   else -> {
                       val token  = Prefs.getString("TOKEN", "")
                       var response  = kimonoInterface.makePurchase(
                           request = purchaseRequest as PurchaseRequest
                       ).run()
                       if (response.isSuccessful) {
                           response.body()!!
                       } else {
                           PurchaseResponse(description = "An error occured",
                               responseCode = "${response.code()}", responseMessage = "An error occurred")
                       }
                   }
               }
            }
        } catch (e:Exception) {
            Log.e("get trans data error", e.stackTraceToString())
            return  PurchaseResponse(
                responseCode = "0x000",
                description = "An error occurred"
            )
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
                downloadTerminalDetails()
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
                writePinKey()
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

    val allTransactions: Flow<List<TransactionResultData>> = iswKozenDao.getAllTransaction()
}