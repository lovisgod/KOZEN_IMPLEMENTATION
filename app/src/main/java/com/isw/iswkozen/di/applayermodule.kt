package com.isw.iswkozen.di


import com.isw.iswkozen.IswApplication
import com.isw.iswkozen.core.data.models.DeviceType
import com.isw.iswkozen.core.database.IswKozenRoomDb
import com.isw.iswkozen.views.repo.IswDataRepo
import com.isw.iswkozen.views.repo.IswDataRepoPax
import com.isw.iswkozen.views.viewmodels.BluetoothViewModel
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val applayerModule = module {

    single<IswKozenRoomDb> { IswKozenRoomDb.getDatabase(androidContext()) }
    single { get<IswKozenRoomDb>().iswKozenDao() }

    factory {
        if (IswApplication.DEVICE_TYPE == DeviceType.KOZEN) {
            IswDataRepo(get(), get(), get(), get(), get(), get(), get(), get() )
        } else {
            IswDataRepoPax(get(), get(), get(), get(), get(), get(), get(), get() )
        }
    }

    viewModel { IswKozenViewModel(get()) }
    viewModel { BluetoothViewModel() }

}