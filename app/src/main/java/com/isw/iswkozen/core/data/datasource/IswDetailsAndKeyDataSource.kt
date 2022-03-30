package com.isw.iswkozen.core.data.datasource

import com.isw.iswkozen.core.data.models.TerminalInfo


interface IswConfigDataSource {
    suspend fun loadTerminal(terminalData: TerminalInfo)
    suspend fun loadAid()
    suspend fun loadCapk ()
    suspend fun loadVisa()
    suspend fun loadExceptionFile()
    suspend fun loadRevocationIPK()
    suspend fun loadUnionPay()
    suspend fun loadMasterCard()
    suspend fun loadDiscover()
    suspend fun loadAmex()
    suspend fun loadMir()
    suspend fun loadVisaDRL()
    suspend fun loadAmexDRL()
    suspend fun loadService()

}