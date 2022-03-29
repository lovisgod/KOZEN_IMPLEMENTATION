package com.isw.iswkozen.core.utilities

import kotlin.jvm.JvmOverloads
import com.isw.iswkozen.core.utilities.BerTag
import com.isw.iswkozen.core.utilities.BerTlvBuilder
import com.isw.iswkozen.core.utilities.BerTlv
import android.annotation.SuppressLint
import java.lang.IllegalStateException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class BerTlvBuilder @JvmOverloads constructor(
    private val theTemplate: BerTag? = null as BerTag?,
    private val theBuffer: ByteArray = ByteArray(
        DEFAULT_SIZE
    ),
    aOffset: Int = 0,
    aLength: Int = DEFAULT_SIZE
) {
    constructor(size: Int) : this(null, ByteArray(size), 0, size) {}
    constructor(tlvs: BerTlvs?) : this(null as BerTag?) {
        if (tlvs != null) {
            for (tlv in tlvs.list) {
                addBerTlv(tlv)
            }
        }
    }

    fun addEmpty(aObject: BerTag?): BerTlvBuilder {
        return addBytes(aObject, byteArrayOf(), 0, 0)
    }

    fun addByte(aObject: BerTag, aByte: Byte): BerTlvBuilder {
        val len: Int = aObject.bytes.size
        System.arraycopy(aObject.bytes, 0, theBuffer, thePos, len)
        thePos += len
        theBuffer[thePos++] = 1
        theBuffer[thePos++] = aByte
        return this
    }

    fun addDate(aObject: BerTag?, aDate: Date?): BerTlvBuilder {
        val format = SimpleDateFormat("yyMMdd")
        return addHex(aObject, format.format(aDate))
    }

    fun addTime(aObject: BerTag?, aDate: Date?): BerTlvBuilder {
        val format = SimpleDateFormat("HHmmss")
        return addHex(aObject, format.format(aDate))
    }

    fun build(): Int {
        if (theTemplate != null) {
            val tagLen: Int = theTemplate.bytes.size
            val lengthBytesCount = calculateBytesCountForLength(thePos)
            System.arraycopy(
                theBuffer,
                theBufferOffset,
                theBuffer,
                tagLen + lengthBytesCount,
                thePos
            )
            System.arraycopy(
                theTemplate.bytes,
                0,
                theBuffer,
                theBufferOffset,
                theTemplate.bytes.size
            )
            fillLength(theBuffer, tagLen, thePos)
            thePos += tagLen + lengthBytesCount
        }
        return thePos
    }

    private fun fillLength(aBuffer: ByteArray, aOffset: Int, aLength: Int) {
        if (aLength < 0x80) {
            aBuffer[aOffset] = aLength.toByte()
        } else if (aLength < 0x100) {
            aBuffer[aOffset] = 0x81.toByte()
            aBuffer[aOffset + 1] = aLength.toByte()
        } else if (aLength < 0x10000) {
            aBuffer[aOffset] = 0x82.toByte()
            aBuffer[aOffset + 1] = (aLength / 0x100).toByte()
            aBuffer[aOffset + 2] = (aLength % 0x100).toByte()
        } else if (aLength < 0x1000000) {
            aBuffer[aOffset] = 0x83.toByte()
            aBuffer[aOffset + 1] = (aLength / 0x10000).toByte()
            aBuffer[aOffset + 2] = (aLength / 0x100).toByte()
            aBuffer[aOffset + 3] = (aLength % 0x100).toByte()
        } else {
            throw IllegalStateException("length [$aLength] out of range (0x1000000)")
        }
    }

    private fun calculateBytesCountForLength(aLength: Int): Int {
        val ret: Int
        ret = if (aLength < 0x80) {
            1
        } else if (aLength < 0x100) {
            2
        } else if (aLength < 0x10000) {
            3
        } else if (aLength < 0x1000000) {
            4
        } else {
            throw IllegalStateException("length [$aLength] out of range (0x1000000)")
        }
        return ret
    }

    fun addHex(aObject: BerTag?, aHex: String?): BerTlvBuilder {
        val buffer: ByteArray? = aHex?.let { HexUtil.parseHex(it) }
        return addBytes(aObject, buffer, 0, buffer?.size!!)
    }

    fun addBytes(aObject: BerTag?, aBytes: ByteArray?): BerTlvBuilder {
        return addBytes(aObject, aBytes, 0, aBytes!!.size)
    }

    fun addBytes(aTag: BerTag?, aBytes: ByteArray?, aFrom: Int, aLength: Int): BerTlvBuilder {
        val tagLength: Int = aTag!!.bytes.size
        val lengthBytesCount = calculateBytesCountForLength(aLength)
        System.arraycopy(aTag.bytes, 0, theBuffer, thePos, tagLength)
        thePos += tagLength
        fillLength(theBuffer, thePos, aLength)
        thePos += lengthBytesCount
        System.arraycopy(aBytes, aFrom, theBuffer, thePos, aLength)
        thePos += aLength
        return this
    }

    fun add(aBuilder: BerTlvBuilder): BerTlvBuilder {
        val array = aBuilder.buildArray()
        System.arraycopy(array, 0, theBuffer, thePos, array.size)
        thePos += array.size
        return this
    }

    fun addBerTlv(aTlv: BerTlv): BerTlvBuilder {
        return if (aTlv.isConstructed() == true) {
            add(from(aTlv))
        } else {
            addBytes(aTlv.getTag(), aTlv.getBytesValue())
        }
    }

    fun addText(aTag: BerTag?, aText: String): BerTlvBuilder {
        return addText(aTag, aText, ASCII)
    }

    @SuppressLint("NewApi")
    fun addText(aTag: BerTag?, aText: String, aCharset: Charset?): BerTlvBuilder {
        val buffer = aText.toByteArray(aCharset!!)
        return addBytes(aTag, buffer, 0, buffer.size)
    }

    fun addIntAsHex(aObject: BerTag?, aCode: Int): BerTlvBuilder {
        var s = aCode.toString() + ""
        if (s.length % 2 != 0) {
            s = "0$s"
        }
        return addHex(aObject, s)
    }

    fun buildArray(): ByteArray {
        val count = build()
        val buf = ByteArray(count)
        System.arraycopy(theBuffer, 0, buf, 0, count)
        return buf
    }

    fun buildTlv(): BerTlv {
        val count = build()
        return com.isw.iswkozen.core.utilities.BerTlvParser()
            .parseConstructed(theBuffer, theBufferOffset, count)
    }

    fun buildTlvs(): BerTlvs {
        val count = build()
        return com.isw.iswkozen.core.utilities.BerTlvParser()
            .parse(theBuffer, theBufferOffset, count)
    }

    private val theBufferOffset: Int
    private var thePos = 0

    companion object {
        private val ASCII = Charset.forName("US-ASCII")
        private const val DEFAULT_SIZE = 30 * 1024
        fun from(aTlv: BerTlv): BerTlvBuilder {
            return if (aTlv.isConstructed() == true) {
                val builder = template(aTlv.getTag())
                for (tlv in aTlv.theList!!) {
                    builder.addBerTlv(tlv)
                }
                builder
            } else {
                BerTlvBuilder().addBerTlv(aTlv)
            }
        }

        fun template(aTemplate: BerTag?): BerTlvBuilder {
            return BerTlvBuilder(aTemplate)
        }
    }

    init {
        thePos = aOffset
        theBufferOffset = aOffset
    }
}