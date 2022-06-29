package com.isw.iswkozen.core.data.dataSourceImpl

import android.util.Log
import com.google.gson.Gson
import com.isw.iswkozen.core.data.datasource.IswDetailsAndKeyDataSource
import com.isw.iswkozen.core.data.models.IswTerminalModel
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants.ISW_TOKEN_URL
import com.isw.iswkozen.core.data.utilsData.Constants.TERMINAL_INFO_KEY
import com.isw.iswkozen.core.data.utilsData.Constants.TOKEN
import com.isw.iswkozen.core.network.AuthInterface
import com.isw.iswkozen.core.network.kimonoInterface
import com.isw.iswkozen.core.network.models.TokenRequestModel
import com.isw.iswkozen.core.network.models.convertConfigResponseToAllTerminalInfo
import com.isw.iswkozen.core.utilities.HexUtil
import com.isw.iswkozen.core.utilities.ISWGeneralException
import com.pixplicity.easyprefs.library.Prefs
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.security.PedKcvInfo
import com.pos.sdk.security.PedKeyInfo
import kotlin.jvm.Throws
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.FlexibleTypeDeserializer

class IswDetailsAndKeysImpl(val authInterface: AuthInterface, val kimonoInterface: kimonoInterface): IswDetailsAndKeyDataSource {
    override suspend fun writeDukPtKey(keyIndex: Int, keyData: String, KsnData: String): Int {
        Log.d("KSN", "KSN $KsnData")
        val kcvInfo = PedKcvInfo(0, ByteArray(5))
//        Prefs.putString("IPEK", keyData)
//        Prefs.putString("KSN", KsnData.dropLast(1))
        return POIHsmManage.getDefault().PedWriteTIK(
            keyIndex,
            0,
            8,
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

    override suspend fun loadMasterKey(masterkey: String) {
        val pedKeyInfo =
            PedKeyInfo(0, 0, POIHsmManage.PED_TMK, 1, 0, 16, HexUtil.parseHex(masterkey))
         POIHsmManage.getDefault().PedWriteKey(
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
                               info.nibbsKey = it.tmsRouteTypeConfig?.key.toString()
                               saveTerminalInfo(info)
                           }
                       }
                   }
               }
           }
       }
    }

    @Throws (ISWGeneralException::class)
    override suspend fun getISWToken(tokenRequestModel: TokenRequestModel) {
        var response = kimonoInterface.getISWToken(tokenRequestModel).run()
        if (response.isSuccessful) {
            if (response.body() != null) {
                var tokenData = response.body()
                tokenData?.token.let {
                    if (!it.isNullOrEmpty()) {
                        println("token gotten: => ${it}")
                        Prefs.putString(TOKEN, it)
                    }
                }
            }
        } else {
         throw ISWGeneralException()
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