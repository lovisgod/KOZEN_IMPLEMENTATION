package com.isw.iswkozen.core.data.utilsData

import android.annotation.SuppressLint
import java.nio.charset.Charset


object KeysUtils {

    private val ASCII = Charset.forName("US-ASCII")

    fun productionKSN() = "0000000002DDDDE00001" //FFFF000002DDDDE0
    fun testKSN() = "0000000006DDDDE00000"
    fun productionIPEK() = "3F2216D8297BCE9C"
    fun testIPEK() = "9F8011E7E71E483B"

    fun productionCMS(isEpms: Boolean) =
        if (isEpms) EPMS.productionCMS()
        else CTMS.productionCMS()

    fun testCMS(isEpms: Boolean) =
        if (isEpms) EPMS.testCMS()
        else CTMS.testCMS()


     object EPMS {
        fun productionCMS() = "A050F63AFF366A4B0588D818D23C6C77"
        fun testCMS() = "DBEECACCB4210977ACE73A1D873CA59F"
    }

     object CTMS {
        fun productionCMS() = "3CDDE1CC6FDD225C9A8BC3EB065509A6"//"A050F63AFF366A4B0588D818D23C6C77"
        fun testCMS() = "DBEECACCB4210977ACE73A1D873CA59F" //EPMS=DBEECACCB4210977ACE73A1D873CA59F
    }

    const val PINKEY_INDEX = 2
    const val DUKPTKEY_INDEX = 1



    fun ByteArray.getTextValue(): String? {
        return getTextValue(ASCII)
    }

    @SuppressLint("NewApi")
    fun ByteArray.getTextValue(aCharset: Charset?): String? {
        return String(this, aCharset!!)
    }


}
