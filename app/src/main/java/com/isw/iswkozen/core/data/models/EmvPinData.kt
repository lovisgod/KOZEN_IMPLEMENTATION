package com.isw.iswkozen.core.data.models

import com.isw.iswkozen.core.data.utilsData.Constants.ISW_DUKPT_KSN

data class EmvPinData (
    var ksn : String = ISW_DUKPT_KSN,
    var CardPinBlock: String = "")