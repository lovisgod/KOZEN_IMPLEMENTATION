package com.isw.iswkozen.core.data.dataSourceImpl

import com.google.gson.Gson
import com.isw.iswkozen.core.data.datasource.IswDetailsAndKeyDataSource
import com.isw.iswkozen.core.data.models.IswTerminalModel
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants.TERMINAL_INFO_KEY
import com.isw.iswkozen.core.network.AuthInterface
import com.isw.iswkozen.core.network.models.convertConfigResponseToAllTerminalInfo
import com.isw.iswkozen.core.utilities.HexUtil
import com.pixplicity.easyprefs.library.Prefs
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.security.PedKcvInfo
import com.pos.sdk.security.PedKeyInfo

class IswDetailsAndKeysImpl(val authInterface: AuthInterface): IswDetailsAndKeyDataSource {
    override suspend fun writeDukPtKey(keyIndex: Int, keyData: String, KsnData: String): Int {
        val kcvInfo = PedKcvInfo(0, ByteArray(5))
        return POIHsmManage.getDefault().PedWriteTIK(
            keyIndex,
            0,
            16,
            HexUtil.parseHex(keyData),
            HexUtil.parseHex(KsnData),
            kcvInfo
        )
    }

    override suspend fun writePinKey(keyIndex: Int, keyData: String): Int {
        val pedKeyInfo =
            PedKeyInfo(0, 0, POIHsmManage.PED_TPK, keyIndex, 0, 16, HexUtil.parseHex(keyData))
        return POIHsmManage.getDefault().PedWriteKey(
            pedKeyInfo,
            PedKcvInfo(0, ByteArray(5)))
    }
    override suspend fun downloadTerminalDetails(terminalData: IswTerminalModel) {
       var response = authInterface.getMerchantDetails(terminalData.serialNumber).run()
       if (response.isSuccessful) {
           if (response.body() != null) {
               var configData = response.body()
               configData.let {
                   if (it != null) {
                       convertConfigResponseToAllTerminalInfo(it).let {
                           it.terminalInfo?.let { info ->
                               saveTerminalInfo(info)
                           }
                       }
                   }
               }
           }
       }
    }


    override suspend fun readTerminalInfo(): TerminalInfo {
        val dataString = Prefs.getString(TERMINAL_INFO_KEY)
        return Gson().fromJson(dataString, TerminalInfo::class.java) ?: TerminalInfo()
    }

    override suspend fun eraseKey(): Int {
        return POIHsmManage.getDefault().PedErase()
    }

    override suspend fun saveTerminalInfo(data: TerminalInfo) {
        val dataString = Gson().toJson(data)
        Prefs.putString(TERMINAL_INFO_KEY, dataString)
    }

}