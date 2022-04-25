package com.isw.iswkozen.core.network.IsoCommunicator.nibss

import android.content.Context
import com.isw.iswkozen.core.data.datasource.IswDetailsAndKeyDataSource
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.Constants.KEY_PIN_KEY
import com.isw.iswkozen.core.data.utilsData.Constants.KEY_SESSION_KEY
import com.isw.iswkozen.core.network.IsoCommunicator.IsoSocket
import com.isw.iswkozen.core.network.IsoCommunicator.nibss.builders.IsoTransactionBuilder
import com.pixplicity.easyprefs.library.Prefs

/**
 *
 * @author ayooluwa.olosunde
 */

class NibssIsoServiceImpl(
    private val context: Context,
    private val socket: IsoSocket,
    val iswDetailsAndKeyDataSource: IswDetailsAndKeyDataSource,
    val isoTransactionBuilder: IsoTransactionBuilder
){

    suspend fun downloadKey(terminalId: String,
                            ip: String,
                            port: Int,
                            cms: String,
                            TMK: String,
                            TSK: String,
                            TPK: String
                            ): Boolean {

        // getResult master key & save
        val isDownloaded = isoTransactionBuilder.KeyTransactionBuilder(terminalId, ip, port, TMK, cms)?.let { masterKey ->
            Prefs.putString(Constants.KEY_MASTER_KEY, masterKey)
            // load master key into pos
            iswDetailsAndKeyDataSource.loadMasterKey(masterKey)

            // getResult pin key & save
            val isSessionSaved =
                isoTransactionBuilder.KeyTransactionBuilder(terminalId, ip, port, TSK, masterKey)?.let { sessionKey ->
                    Prefs.putString(KEY_SESSION_KEY, sessionKey)
                    true
                }

            // getResult pin key & save
            val isPinSaved = isoTransactionBuilder.KeyTransactionBuilder(terminalId, ip, port, TPK, masterKey)?.let { pinKey ->
                Prefs.putString(KEY_PIN_KEY, pinKey)

                // load pin key into pos device
                iswDetailsAndKeyDataSource.writePinKey(1, pinKey)
                true
            }

            isPinSaved == true && isSessionSaved == true
        }

        return isDownloaded == true
    }

    suspend fun downloadTerminalDetails(
        terminalId: String,
        ip: String,
        port: Int,
        processingCode: String,
    ): TerminalInfo? {
        val terminalData = isoTransactionBuilder.parameterTransactionBuilder(
            terminalId,
            ip,
            port,
            processingCode)

        return terminalData
    }

}