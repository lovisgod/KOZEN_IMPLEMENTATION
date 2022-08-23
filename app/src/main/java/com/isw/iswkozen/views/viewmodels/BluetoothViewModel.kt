package com.isw.iswkozen.views.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isw_smart_bluetooth.interfaces.TillCommand
import com.isw_smart_bluetooth.models.response.TillTransactionResponse


class BluetoothViewModel: ViewModel() {

    private val _bluetoothCommand: MutableLiveData<TillCommand> = MutableLiveData()
    val bluetoothCommand: LiveData< TillCommand> get() =  _bluetoothCommand

    private val _bluetoothTransactionResponse: MutableLiveData<TillTransactionResponse> = MutableLiveData()
    val bluetoothTransactionResponse: LiveData<TillTransactionResponse> get() =  _bluetoothTransactionResponse

    fun pushCommand(command: TillCommand) {
        _bluetoothCommand.postValue(command)
    }

    fun resetCommand() {
        _bluetoothCommand.postValue(null)
    }

    fun sendTransactionResponse(response: TillTransactionResponse) {
        println("response sent ===> ${response.Amount}")
        _bluetoothTransactionResponse.postValue(response)
    }

}