package com.isw.iswkozen.di

import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.dataInteractor.IswDetailsAndKeySourceInteractor
import com.isw.iswkozen.core.data.dataSourceImpl.IswDataConfig
import com.isw.iswkozen.core.data.dataSourceImpl.IswDetailsAndKeysImpl
import com.isw.iswkozen.core.data.datasource.IswConfigDataSource
import com.isw.iswkozen.core.data.datasource.IswDetailsAndKeyDataSource
import org.koin.dsl.module.module

val configModule = module {
    single { IswDataConfig() }
    single { IswDetailsAndKeysImpl( get(), get() ) }
    factory<IswConfigDataSource> { return@factory IswDataConfig() }
    factory<IswDetailsAndKeyDataSource> {
        return@factory IswDetailsAndKeysImpl(
            get(),
            get()
        ) }

    single { IswConfigSourceInteractor(get()) }
    single { IswDetailsAndKeySourceInteractor( get() ) }

}