package com.isw.iswkozen

import android.content.Context
import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswDetailsAndKeySourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswTransactionInteractor
import com.isw.iswkozen.core.database.dao.IswKozenDao
import com.isw.iswkozen.core.network.CardLess.HttpService
import com.isw.iswkozen.core.network.IsoCommunicator.nibss.NibssIsoServiceImpl
import com.isw.iswkozen.core.network.kimonoInterface
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class ApplicationHandler: KoinComponent {

    private object Container: KoinComponent

    val iswConfigSourceInteractor: IswConfigSourceInteractor by inject()
    val iswDetailsAndKeySourceInteractor: IswDetailsAndKeySourceInteractor by inject()
    val iswTransactionInteractor: IswTransactionInteractor by inject()
//    val context: Context,


    // set up terminal config

    // write keys

    suspend fun writePinKey(keyIndex:Int, keyData: String): Int{
        return iswDetailsAndKeySourceInteractor.writePinKey(keyIndex, keyData)
    }

    suspend fun writeDukptKey(keyIndex: Int, keyData: String, ksnData: String): Int{
        return iswDetailsAndKeySourceInteractor.writeDukPtKey(keyIndex, keyData, ksnData)
    }

    // download nibs keys
    // set up transaction
    // start transaction and get
}