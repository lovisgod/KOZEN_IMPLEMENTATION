package com.isw.iswkozen.core.data.dataInteractor

import com.isw.iswkozen.core.data.datasource.IswConfigDataSource
import com.isw.iswkozen.core.data.models.TerminalInfo

class IswConfigSourceInteractor(var iswConfigDataSource: IswConfigDataSource) {

    suspend fun loadTerminal(terminalData: TerminalInfo) = iswConfigDataSource.loadTerminal(terminalData)
    suspend fun loadAid() = iswConfigDataSource.loadAid()
    suspend fun loadCapk () = iswConfigDataSource.loadCapk()
    suspend fun loadVisa() = iswConfigDataSource.loadVisa()
    suspend fun loadExceptionFile() = iswConfigDataSource.loadExceptionFile()
    suspend fun loadRevocationIPK() = iswConfigDataSource.loadRevocationIPK()
    suspend fun loadUnionPay() = iswConfigDataSource.loadUnionPay()
    suspend fun loadMasterCard() = iswConfigDataSource.loadMasterCard()
    suspend fun loadDiscover() = iswConfigDataSource.loadDiscover()
    suspend fun loadAmex() = iswConfigDataSource.loadAmex()
    suspend fun loadMir() = iswConfigDataSource.loadMir()
    suspend fun loadVisaDRL() = iswConfigDataSource.loadVisaDRL()
    suspend fun loadAmexDRL() = iswConfigDataSource.loadAmexDRL()
    suspend fun loadService() = iswConfigDataSource.loadService()
}