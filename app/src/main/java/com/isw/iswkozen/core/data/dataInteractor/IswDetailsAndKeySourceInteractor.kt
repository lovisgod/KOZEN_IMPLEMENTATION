package com.isw.iswkozen.core.data.dataInteractor

import com.isw.iswkozen.core.data.datasource.IswConfigDataSource
import com.isw.iswkozen.core.data.datasource.IswDetailsAndKeyDataSource
import com.isw.iswkozen.core.data.models.IswTerminalModel
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.network.AuthInterface
import com.isw.iswkozen.core.network.models.TokenRequestModel

class IswDetailsAndKeySourceInteractor(
    var iswDetailsAndKeyDataSource: IswDetailsAndKeyDataSource,
    ) {
    suspend fun writeDukPtKey(keyIndex: Int, keyData: String, KsnData: String) =
        iswDetailsAndKeyDataSource.writeDukPtKey(keyIndex, keyData, KsnData)
    suspend fun writePinKey(keyIndex: Int, keyData: String) =
        iswDetailsAndKeyDataSource.writePinKey(keyIndex, keyData)
    suspend fun readTerminalInfo() =
        iswDetailsAndKeyDataSource.readTerminalInfo()
    suspend fun eraseKey() =
        iswDetailsAndKeyDataSource.eraseKey()

    suspend fun loadMasterKey(masterKey: String) = iswDetailsAndKeyDataSource.loadMasterKey(masterKey)

    suspend fun saveTerminalInfo(data: TerminalInfo) =
        iswDetailsAndKeyDataSource.saveTerminalInfo(data)

    suspend fun downloadTerminalDetails(data: IswTerminalModel) =
        iswDetailsAndKeyDataSource.downloadTerminalDetails(data)

    suspend fun getISWToken(tokenRequestModel: TokenRequestModel) =
        iswDetailsAndKeyDataSource.getISWToken(tokenRequestModel)
}