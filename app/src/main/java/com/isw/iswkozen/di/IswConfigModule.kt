package com.isw.iswkozen.di

import com.isw.iswkozen.core.data.dataInteractor.IswConfigSourceInteractor
import com.isw.iswkozen.core.data.dataSourceImpl.IswDataConfig
import com.isw.iswkozen.core.data.datasource.IswConfigDataSource
import org.koin.dsl.module.module

val configModule = module {
    single { IswDataConfig() }
    factory<IswConfigDataSource> { return@factory IswDataConfig() }

    single { IswConfigSourceInteractor(get()) }

}