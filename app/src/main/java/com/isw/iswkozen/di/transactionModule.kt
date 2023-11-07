package com.isw.iswkozen.di

import com.isw.iswkozen.IswApplication
import com.isw.iswkozen.core.network.IsoCommunicator.nibss.NibssIsoServiceImpl
import com.isw.iswkozen.core.data.dataInteractor.IswTransactionInteractor
import com.isw.iswkozen.core.data.dataSourceImpl.IswTransactionHandlerPax
import com.isw.iswkozen.core.data.dataSourceImpl.IswTransactionImpl
import com.isw.iswkozen.core.data.datasource.IswTransactionDataSource
import com.isw.iswkozen.core.data.models.DeviceType
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.network.IsoCommunicator.IsoSocket
import com.isw.iswkozen.core.network.IsoCommunicator.nibss.builders.IsoTransactionBuilder
import com.isw.iswkozen.core.network.IsoCommunicator.nibss.tcp.IsoSocketImpl
import com.isw.iswkozen.core.utilities.EmvHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val transactionModule = module {
    single { EmvHandler() }
    single { IswTransactionImpl( get() ) }
    factory<IswTransactionDataSource> {
        if (IswApplication.DEVICE_TYPE == DeviceType.KOZEN) {
            return@factory IswTransactionImpl( get() )
        }   else {
            return@factory IswTransactionHandlerPax()
        }
    }
    single { IswTransactionInteractor(get()) }

    factory<IsoSocket> {
        val resource = androidContext().resources
        var readTimeout = 60000
        val serverIp =  Constants.ISW_TERMINAL_IP
        val port = Constants.ISW_TERMINAL_PORT
        return@factory IsoSocketImpl(serverIp, port, readTimeout)
    }

    single { IsoTransactionBuilder( get(), get() ) }

    single { NibssIsoServiceImpl( get(), get(), get(), get() ) }

}