package com.isw.iswkozen

import android.app.Application
import com.isw.iswkozen.di.ExportModules
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext

class IswApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        loadModules()
    }

    fun appContext(app: Application) = module(override = true) {
        single { app.applicationContext }

    }

    fun loadModules(){
// set up koin
        var modules = arrayListOf<Module>()
        modules.add(appContext(this))
        modules.addAll(ExportModules.modules)
        StandAloneContext.loadKoinModules(modules)
    }
}