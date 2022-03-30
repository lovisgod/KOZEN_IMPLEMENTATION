package com.isw.iswkozen.core.data.dataInteractor

import com.isw.iswkozen.core.data.datasource.IswConfigDataSource
import com.isw.iswkozen.core.data.datasource.IswDetailsAndKeyDataSource
import com.isw.iswkozen.core.data.models.TerminalInfo

class IswDetailsAndKeySourceInteractor(var iswDetailsAndKeyDataSource: IswDetailsAndKeyDataSource) {
    suspend fun writeDukPtKey(keyIndex: Int, keyData: String, KsnData: String) =
        iswDetailsAndKeyDataSource.writeDukPtKey(keyIndex, keyData, KsnData)
    suspend fun writePinKey(keyIndex: Int, keyData: String) =
        iswDetailsAndKeyDataSource.writePinKey(keyIndex, keyData)
    suspend fun readTerminalInfo() =
        iswDetailsAndKeyDataSource.readTerminalInfo()
    suspend fun eraseKey() =
        iswDetailsAndKeyDataSource.eraseKey()
}