package com.isw.iswkozen.views.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.KeysUtils
import com.isw.iswkozen.views.repo.IswDataRepo
import kotlinx.coroutines.*

class IswKozenViewModel(val dataRepo: IswDataRepo): ViewModel() {

    private val job = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)
    protected val ioScope = uiScope.coroutineContext + Dispatchers.IO


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private val _terminalSetupStatus = MutableLiveData<Boolean>()
    val terminalSetupStatus: LiveData<Boolean> = _terminalSetupStatus

    private val _downloadterminalDetailsStatus = MutableLiveData<Boolean>()
    val downloadterminalDetailsStatus: LiveData<Boolean> = _downloadterminalDetailsStatus

    private val _keyMStatus = MutableLiveData<Int>()
    val keyMStatus: LiveData<Int> = _keyMStatus


    fun setupTerminal () {
       viewModelScope.launch {
           withContext(Dispatchers.Main) {
               var loaded = dataRepo.loadTerminal()
               println("terminalDetailsLoaded: ${loaded}")
               _terminalSetupStatus.postValue(loaded)
           }

       }
    }


    fun dowloadDetails () {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var downloaded = dataRepo.downloadTerminalDetails()
                println("terminalDetailsLoaded: ${downloaded}")
                _downloadterminalDetailsStatus.postValue(downloaded)
            }

        }
    }

    fun loadAllConfig () {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var loaded = dataRepo.loadAllConfig()
                println("terminalConfigLoaded: ${loaded}")
                _terminalSetupStatus.postValue(loaded)
            }

        }
    }


     fun writePinKey() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.writePinKey()
                println("key result: ${result}")
                _keyMStatus.postValue(result)
            }

        }
    }


     fun  writeDukptKey()  {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.writeDukptKey()
                println("key result: ${result}")
                _keyMStatus.postValue(result)
            }

        }
    }

     fun readterminalDetails() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.readterminalDetails()
                println("terminalConfigLoaded: ${result?.terminalCode}")
            }
        }
    }

     fun eraseKeys()  {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.eraseKeys()
                println("key result: ${result}")
                _keyMStatus.postValue(result)
            }

        }
    }


    fun nullifyValues() {
        // get payment status
        viewModelScope.launch  {
            withContext(Dispatchers.IO) {
               _terminalSetupStatus.postValue(null)
            }
        }
    }


}