package com.isw.iswkozen.di


import com.isw.iswkozen.views.repo.IswDataRepo
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val applayerModule = module {


    single { IswDataRepo(get(), get()) }

    viewModel { IswKozenViewModel(get()) }

}