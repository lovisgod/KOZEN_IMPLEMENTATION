package com.isw.iswkozen.views.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    fun setupTerminal () {
       viewModelScope.launch {
           withContext(Dispatchers.Main) {
               var loaded = dataRepo.loadTerminal()
               println("terminalDetailsLoaded: ${loaded}")
               _terminalSetupStatus.postValue(loaded)
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


    fun nullifyValues() {
        // get payment status
        viewModelScope.launch  {
            withContext(Dispatchers.IO) {
               _terminalSetupStatus.postValue(null)
            }
        }
    }


}