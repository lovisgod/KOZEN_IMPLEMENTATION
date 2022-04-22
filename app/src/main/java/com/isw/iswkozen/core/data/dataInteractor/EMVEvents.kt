package com.isw.iswkozen.core.data.dataInteractor

import EmvMessage
import com.isw.iswkozen.core.data.utilsData.RequestIccData

interface EMVEvents {

    fun onInsertCard()
    fun onRemoveCard()
    fun onPinInput()
    fun onEmvProcessing(message: String = "Please wait while we read card")
    fun onEmvProcessed(data: RequestIccData)
}