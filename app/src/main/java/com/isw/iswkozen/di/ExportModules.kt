package com.isw.iswkozen.di

object ExportModules {

    val ConfigModule = configModule
    val applayermodule = applayerModule
    val networkmodule = networkModule
    val transactionmodule = transactionModule

    val modules = listOf(networkmodule, ConfigModule, applayermodule, transactionmodule)

}