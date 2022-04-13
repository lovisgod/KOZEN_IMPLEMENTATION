package com.isw.iswkozen.core.network.models

import com.isw.iswkozen.core.data.utilsData.ICCData
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.utilities.HexUtil
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "CardData", strict = false)
class CardData {
    @field:Element(name = "cardSequenceNumber", required = false)
    var cardSequenceNumber: String = ""

    @field:Element(name = "emvData", required = false)
    var emvData: RequestIccData? = null

//    @field:Element(name = "mifareData", required = false)
//    var mifareData: MiFareData? = null

    @field:Element(name = "track2", required = false)
    var track2: Track2? = null

    @field:Element(name = "wasFallback", required = false)
    var wasFallback: Boolean = false

    companion object {
        fun create(iccDataInfo: RequestIccData ) = CardData().apply {
            val track2data =iccDataInfo.TRACK_2_DATA
            println("track2 data => ${track2data}")
            // extract pan and expiry
            val strTrack2 = track2data.split("F")[0]
            var panX = strTrack2.split("D")[0]
            val expiry = strTrack2.split("D")[1].substring(0, 4)
            val src = strTrack2.split("D")[1].substring(4, 7)

            cardSequenceNumber = "0${iccDataInfo.APP_PAN_SEQUENCE_NUMBER}"
            emvData = iccDataInfo
            track2 = Track2().apply {
                pan = panX
                expiryMonth = expiry.takeLast(2)
                expiryYear = expiry.take(2)
                track2 = let {
                    val neededLength = strTrack2.length - 2
                    val isVisa = strTrack2.startsWith('4')
                    val hasCharacter = strTrack2.last().isLetter()

                    // remove character suffix for visa
                    if (isVisa && hasCharacter) strTrack2.substring(0..neededLength)
                    else strTrack2
                }
            }

        }
    }
}