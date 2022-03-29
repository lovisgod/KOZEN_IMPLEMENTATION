//package com.isw.iswkozen.core.utilities
//
//import java.lang.StringBuilder
//import kotlin.jvm.JvmOverloads
//
//object HexUtilNew {
//    private val CHARS_TABLES = "0123456789ABCDEF".toCharArray()
//    private val BYTES = ByteArray(128)
//    @JvmName("toHexString1")
//    fun toHexString(aBytes: ByteArray, aLength: Int): String {
//        return toHexString(aBytes, 0, aLength)
//    }
//
//    fun parseHex(aHexString: String): ByteArray {
//        val src = aHexString.replace("\n", "").replace(" ", "").toUpperCase().toCharArray()
//        val dst = ByteArray(src.size / 2)
//        var si = 0
//        var di = 0
//        while (di < dst.size) {
//            val high = BYTES[src[si++] and 0x7f]
//            val low = BYTES[src[si++] and 0x7f]
//            dst[di] = ((high shl 4) + low) as Byte
//            di++
//        }
//        return dst
//    }
//
//    @JvmOverloads
//    fun toFormattedHexString(
//        aBytes: ByteArray,
//        aOffset: Int = 0,
//        aLength: Int = aBytes.size
//    ): String {
//        val sb = StringBuilder()
//        sb.append("[")
//        sb.append(aLength)
//        sb.append("] :")
//        var si = aOffset
//        var di = 0
//        while (si < aOffset + aLength) {
//            val b = aBytes[si]
//            if (di % 4 == 0) {
//                sb.append("  ")
//            } else {
//                sb.append(' ')
//            }
//            sb.append(CHARS_TABLES[b and 0xf0 ushr 4])
//            sb.append(CHARS_TABLES[b and 0x0f])
//            si++
//            di++
//        }
//        return sb.toString()
//    }
//
//    @JvmOverloads
//    fun toHexString(aBytes: ByteArray, aOffset: Int = 0, aLength: Int = aBytes.size): String {
//        val dst = CharArray(aLength * 2)
//        var si = aOffset
//        var di = 0
//        while (si < aOffset + aLength) {
//            val b = aBytes[si]
//            dst[di++] = CHARS_TABLES[b and 0xf0 ushr 4]
//            dst[di++] = CHARS_TABLES[b and 0x0f]
//            si++
//        }
//        return String(dst)
//    }
//
//    fun fillString(
//        formatString: String?,
//        length: Int,
//        fillChar: Char,
//        leftFillFlag: Boolean
//    ): String {
//        var formatString = formatString
//        if (null == formatString) {
//            formatString = ""
//        }
//        val strLen = formatString.length
//        return if (strLen >= length) {
//            if (true == leftFillFlag) {
//                formatString.substring(strLen - length, strLen)
//            } else {
//                formatString.substring(0, length)
//            }
//        } else {
//            var sbuf: StringBuffer? = StringBuffer()
//            val fillLen = length - formatString.length
//            for (i in 0 until fillLen) {
//                sbuf!!.append(fillChar)
//            }
//            if (true == leftFillFlag) {
//                sbuf!!.append(formatString)
//            } else {
//                sbuf!!.insert(0, formatString)
//            }
//            val returnString = sbuf.toString()
//            sbuf = null
//            returnString
//        }
//    }
//
//    init {
//        for (i in 0..9) {
//            BYTES['0'.toInt() + i] = i.toByte()
//            BYTES['A'.toInt() + i] = (10 + i).toByte()
//            BYTES['a'.toInt() + i] = (10 + i).toByte()
//        }
//    }
//}