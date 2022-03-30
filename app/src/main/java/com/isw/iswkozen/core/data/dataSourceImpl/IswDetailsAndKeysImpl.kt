package com.isw.iswkozen.core.data.dataSourceImpl

import com.isw.iswkozen.core.data.datasource.IswDetailsAndKeyDataSource
import com.isw.iswkozen.core.data.models.IswTerminalModel
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.utilities.HexUtil
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.security.PedKcvInfo
import com.pos.sdk.security.PedKeyInfo

class IswDetailsAndKeysImpl: IswDetailsAndKeyDataSource {
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
//    override suspend fun downloadTerminalDetails(terminalData: IswTerminalModel) {
//        TODO("Not yet implemented")
//    }


    override suspend fun readTerminalInfo(): TerminalInfo {
        return TerminalInfo()
    }

    override suspend fun eraseKey(): Int {
        return POIHsmManage.getDefault().PedErase()
    }

}