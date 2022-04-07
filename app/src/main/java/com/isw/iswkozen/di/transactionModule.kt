package com.isw.iswkozen.di

import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswTransactionInteractor
import com.isw.iswkozen.core.data.dataSourceImpl.IswTransactionImpl
import com.isw.iswkozen.core.data.datasource.IswTransactionDataSource
import com.isw.iswkozen.core.utilities.EmvHandler
import org.koin.dsl.module.module

val transactionModule = module {
    single { EmvHandler() }
    single { IswTransactionImpl( get() ) }
    factory<IswTransactionDataSource> { return@factory IswTransactionImpl( get() ) }
    single { IswTransactionInteractor(get()) }
}