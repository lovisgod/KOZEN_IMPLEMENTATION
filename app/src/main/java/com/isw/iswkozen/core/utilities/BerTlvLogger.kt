package com.isw.iswkozen.core.utilities

import com.isw.iswkozen.core.utilities.BerTlv

object BerTlvLogger {
    fun log(
        aPadding: String?,
        aTlv: com.isw.iswkozen.core.utilities.BerTlvs,
        aLogger: com.isw.iswkozen.core.utilities.IBerTlvLogger?
    ) {
        for (tlv in aTlv.list) {
            if (aPadding != null) {
                if (aLogger != null) {
                    log(aPadding, tlv, aLogger)
                }
            }
        }
    }

    fun log(
        aPadding: String,
        aTlv: BerTlv?,
        aLogger: com.isw.iswkozen.core.utilities.IBerTlvLogger
    ) {
        if (aTlv == null) {
            aLogger.debug("{} is null", aPadding)
            return
        }
        if (aTlv.isConstructed() == true) {
            aLogger.debug(
                "{} [{}]", aPadding, com.isw.iswkozen.core.utilities.HexUtil.toHexString(
                    aTlv.getTag()!!.bytes
                )
            )
            for (child in aTlv.getValues()!!) {
                log("$aPadding    ", child, aLogger)
            }
        } else {
            aLogger.debug(
                "{} [{}] {}", aPadding, com.isw.iswkozen.core.utilities.HexUtil.toHexString(
                    aTlv.getTag()!!.bytes
                ), aTlv.getHexValue()
            )
        }
    }
}