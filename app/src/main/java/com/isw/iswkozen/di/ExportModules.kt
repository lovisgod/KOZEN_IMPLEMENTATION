package com.isw.iswkozen.di

object ExportModules {

    val ConfigModule = configModule
    val applayermodule = applayerModule

    val modules = listOf(ConfigModule, applayermodule)

}