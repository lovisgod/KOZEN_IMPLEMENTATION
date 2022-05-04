package com.isw.iswkozen.views.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.KeysUtils
import com.isw.iswkozen.core.database.entities.TransactionResultData
import com.isw.iswkozen.core.network.models.PurchaseResponse
import com.isw.iswkozen.views.repo.IswDataRepo
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

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

    private val _nibbsKeyMStatus = MutableLiveData<Boolean>()
    val nibbsKeyMStatus: LiveData<Boolean> = _nibbsKeyMStatus

    private val _transactionResponse = MutableLiveData<PurchaseResponse>()
    val transactionResponse: LiveData<PurchaseResponse> = _transactionResponse

    private val _transactionDataList = MutableLiveData<List<TransactionResultData>>()
    val transactionDataList: LiveData<List<TransactionResultData>> = _transactionDataList



    private val _terminalInfo = MutableLiveData<TerminalInfo>()
    val terminalInfo: LiveData<TerminalInfo> = _terminalInfo

    fun setupTerminal () {
       viewModelScope.launch {
           withContext(Dispatchers.Main) {
               var loaded = dataRepo.loadTerminal()
               println("terminalDetailsLoaded: ${loaded}")
               _terminalSetupStatus.postValue(loaded)
           }

       }
    }


    fun getISWToken () {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var teminalInfo = dataRepo.readterminalDetails()
                var loaded = teminalInfo?.let { dataRepo.getISWToken(it) }
                println("getToken: ${loaded}")
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

    fun startTransaction( amount: Long,
                          amountOther: Long,
                          transType: Int, context: Context, emvEvents: EMVEvents) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var status = dataRepo.startTransaction(
                    amount = amount, amountOther = amountOther, transType = transType,
                    contextX = context, emvEvents = emvEvents
                )
                println("startTransactionStatus: ${status}")

                // start transaction here

//                makeOnlineRequest()
            }
        }

    }

    fun getTransactionData( ) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var transactionData = dataRepo.getTransactionData()
                println("transactionData: ${transactionData.CARD_HOLDER_VERIFICATION_RESULT}")
            }
        }

    }

    fun getAllTransactionHistory( ) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                dataRepo.allTransactions.collect {
                    println("this is the list => ${it[0]}")
                    _transactionDataList.postValue(it)
                }
            }
        }

    }

    fun makeOnlineRequest( ) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var transactionData = dataRepo.getTransactionData()
                var terminalInfo = dataRepo.readterminalDetails()
                var response = terminalInfo?.let {
                    dataRepo.makeOnlineRequest(transactionName = "purchase",
                        iccData = transactionData, terminalData = it
                    )
                }
                println("Online request response: ${response?.responseCode}," +
                        " description: ${response?.description}," +
                        " responseMesssage: ${response?.responseMessage}")

                _transactionResponse.postValue(response)
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

    fun loadAllKeys() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.loadAllkeys()
                println("key result: ${result}")
                _keyMStatus.postValue(result)
            }

        }
    }


    fun downloadNibbsKey() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.downLoadNibbsKey()
                println(" nibbs key result: ${result}")
                Prefs.putBoolean("NIBSSKEYSUCCESS", result)
                _nibbsKeyMStatus.postValue(result)
                if (result) {
                    downloadNibbsParams()
                }
            }

        }
    }

    fun checkKey(){
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var terminalInfo = dataRepo.readterminalDetails()
                if (terminalInfo == null) {
                    dataRepo.downloadTerminalDetails()
//                    dataRepo.downLoadNibbsKey()
                } else {
                    setupTerminal()
                    getISWToken()
                }
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

    // reset terminal downloads the terminal details and run the load terminal
    // function that set the EMV parameters for the terminal

    fun resetTerminalConfig()  {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.resetConfig()
                println("reset terminal: ${result}")
                _terminalSetupStatus.postValue(result)
            }

        }
    }

    fun downloadNibbsParams(){
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.downloadNibbsTerminalDetails("205777VZ")
                println("terminal Details => $result")
            }
        }
    }

     fun readterminalDetails() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                var result = dataRepo.readterminalDetails()
                println("terminalConfigLoaded: ${result?.terminalCode}")
                if (result != null) {
                    _terminalInfo.postValue(result)
                }
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