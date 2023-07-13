package com.isw.iswkozen

import android.content.Context
import android.util.Log
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswDetailsAndKeySourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswTransactionInteractor
import com.isw.iswkozen.core.data.models.IswTerminalModel
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.utilities.DeviceUtils
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ApplicationHandler: KoinComponent {

    private object Container: KoinComponent

    val iswConfigSourceInteractor: IswConfigSourceInteractor by inject()
    val iswDetailsAndKeySourceInteractor: IswDetailsAndKeySourceInteractor by inject()
    val iswTransactionInteractor: IswTransactionInteractor by inject()
//    val context: Context,


    // set up terminal config
    suspend fun readterminalDetails() : TerminalInfo? {
        try {
            return iswDetailsAndKeySourceInteractor.readTerminalInfo()
        } catch (e:Exception) {
            Log.e("terminal details error", e.stackTraceToString())
            return  null
        }
    }

    // THIS IS PECULIAR TO ISW CUSTOMERS THAT DOWNLOADS THEIR TERMINAL IDS FROM ISW

    suspend fun downloadTerminalDetails(): Boolean {
        return try {
            println("device serial => ${DeviceUtils.getDeviceSerialKozen().toString()}")
            val data = IswTerminalModel("", DeviceUtils.getDeviceSerialKozen().toString(), false)
            iswDetailsAndKeySourceInteractor.downloadTerminalDetails(data)
            true
        } catch (e:Exception) {
            Log.e("term data down error", e.stackTraceToString())
            false
        }
    }

    suspend fun saveTerminalInfo(terminalInfo: TerminalInfo) =
        iswDetailsAndKeySourceInteractor.saveTerminalInfo(terminalInfo)


    // Load all configs
    suspend fun loadAllConfig(): Boolean {
        return try {
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
                 true
        } catch (e: Exception) {
            Log.e("loadConfigError", e.stackTraceToString())
             false
        }
    }

    // load terminal paramters
    suspend fun loadTerminal(terminalData: TerminalInfo = TerminalInfo()): Boolean {
        return try {
            iswConfigSourceInteractor.loadTerminal(terminalData)
            true
        } catch (e:Exception) {
            Log.e("loadTerminalError", e.stackTraceToString())
              false
        }
    }

    suspend fun downloadAndLoadTerminalDetails() {
        try {
            val downloaded = downloadTerminalDetails()
            if (downloaded) {
                var terminalInfo = readterminalDetails()
                if (terminalInfo != null) {
                    var loaded  =loadTerminal(terminalInfo)
                    if (loaded) { loadAllConfig() }
                }
            }
        } catch (e: Exception) {
            Log.e("terminal details error:", e.stackTraceToString())
        }
    }


    // write keys

    suspend fun writePinKey(keyIndex:Int, keyData: String): Int{
        return iswDetailsAndKeySourceInteractor.writePinKey(keyIndex, keyData)
    }

    suspend fun writeDukptKey(keyIndex: Int, keyData: String, ksnData: String): Int{
        return iswDetailsAndKeySourceInteractor.writeDukPtKey(keyIndex, keyData, ksnData)
    }

    suspend fun loadMasterKey(masterKey: String): Int {
        return try {
            iswDetailsAndKeySourceInteractor.loadMasterKey(masterKey)
            0
        } catch (e: Exception) {
            Log.e("load master key error", e.stackTraceToString())
            99
        }
    }

    suspend fun eraseKey() = iswDetailsAndKeySourceInteractor.eraseKey()


    // start transaction
    suspend fun startTransaction( hasContactless: Boolean = true,
                                  hasContact: Boolean = true,
                                  amount: Long,
                                  amountOther: Long,
                                  transType: Int, context: Context, emvEvents: EMVEvents
    ): Boolean {
        return try {

                iswTransactionInteractor.setEmvContect(context)
                iswTransactionInteractor.startTransaction(
                    hasContactless, hasContact, amount, amountOther, transType, emvEvents)
                true

        } catch (e:Exception) {
            Log.e("startTransaction error", e.stackTraceToString())
            return  false
        }
    }


    // get transaction data

    suspend fun getTransactionData(): RequestIccData {
        return try {
         iswTransactionInteractor.getTransactionData()
        } catch (e:Exception) {
            Log.e("get trans data error", e.stackTraceToString())
            RequestIccData()
        }
    }
}