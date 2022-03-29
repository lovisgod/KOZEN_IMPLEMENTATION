package com.isw.iswkozen.views.repo

import android.util.Log
import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.models.TerminalInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IswDataRepo(val iswConfigSourceInteractor: IswConfigSourceInteractor, val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

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