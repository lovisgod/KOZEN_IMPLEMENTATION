package com.isw.iswkozen.core.data.datasource

import com.isw.iswkozen.core.data.models.IswTerminalModel
import com.isw.iswkozen.core.data.models.TerminalInfo


interface IswDetailsAndKeyDataSource {
    suspend fun downloadTerminalDetails(terminalData: IswTerminalModel)
    suspend fun writeDukPtKey(keyIndex: Int, keyData: String, KsnData: String): Int
    suspend fun writePinKey(keyIndex: Int, keyData: String): Int
    suspend fun readTerminalInfo():TerminalInfo
    suspend fun eraseKey(): Int
    suspend fun saveTerminalInfo(data: TerminalInfo)

}