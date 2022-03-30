package com.isw.iswkozen.views.repo

import android.util.Log
import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswDetailsAndKeySourceInteractor
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants.EXCEPTION_CODE
import com.isw.iswkozen.core.data.utilsData.Constants.KEY_PIN_KEY
import com.isw.iswkozen.core.data.utilsData.KeysUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IswDataRepo(val iswConfigSourceInteractor: IswConfigSourceInteractor,
                  val iswDetailsAndKeySourceInteractor: IswDetailsAndKeySourceInteractor,
                  val dispatcher: CoroutineDispatcher = Dispatchers.IO) {


    // keys wite, download and parameter downloads

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
}