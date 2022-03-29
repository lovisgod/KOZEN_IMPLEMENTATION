package com.isw.iswkozen.core.utilities

import android.annotation.SuppressLint
import java.nio.charset.Charset
import java.util.*

class BerTlv {
    constructor(tag: BerTag, list: ArrayList<BerTlv>) {
        theTag = tag
        theList = list
        theValue = null
    }

    constructor(aTag: BerTag, aValue: ByteArray?) {
        theTag = aTag
        theValue = aValue
        theList = null
    }

    private val ASCII = Charset.forName("US-ASCII")

    private var theTag: BerTag? = null
    private var theValue: ByteArray? = null
    var theList: List<BerTlv>? = null

    fun BerTlv(aTag: BerTag?, aList: List<BerTlv>?) {
        theTag = aTag
        theList = aList
        theValue = null
    }

    fun BerTlv(aTag: BerTag?, aValue: ByteArray?) {
        theTag = aTag
        theValue = aValue
        theList = null
    }

    fun getTag(): BerTag? {
        return theTag
    }

    fun isPrimitive(): Boolean {
        return !theTag?.isConstructed!!
    }

    fun isConstructed(): Boolean? {
        return theTag?.isConstructed
    }

    fun isTag(aTag: BerTag?): Boolean? {
        return theTag?.equals(aTag)
    }

    fun find(aTag: BerTag): BerTlv? {
        if (aTag.equals(getTag())) {
            return this
        }
        if (isConstructed() == true) {
            for (tlv in theList!!) {
                val ret = tlv.find(aTag)
                if (ret != null) {
                    return ret
                }
            }
            return null
        }
        return null
    }

    fun findAll(aTag: BerTag): List<BerTlv>? {
        val list: MutableList<BerTlv> = ArrayList()
        if (aTag.equals(getTag())) {
            list.add(this)
            return list
        } else if (isConstructed() == true) {
            for (tlv in theList!!) {
                list.addAll(tlv.findAll(aTag)!!)
            }
        }
        return list
    }

    fun getHexValue(): String? {
        check(!isConstructed()!!) { "Tag is CONSTRUCTED " + theTag?.let { HexUtil.toHexString(it.bytes) } }
        return theValue?.let { HexUtil.toHexString(it) }
    }

    fun getTextValue(): String? {
        return getTextValue(ASCII)
    }

    @SuppressLint("NewApi")
    fun getTextValue(aCharset: Charset?): String? {
        check(!isConstructed()!!) { "TLV is constructed" }
        return String(theValue!!, aCharset!!)
    }

    fun getBytesValue(): ByteArray? {
        check(!isConstructed()!!) { "TLV [$theTag]is constructed" }
        return theValue
    }

    fun getIntValue(): Int {
        var i = 0
        var j = 0
        var number = 0
        i = 0
        while (i < theValue!!.size) {
            j = theValue!![i].toInt()
            number = number * 256 + if (j < 0) 256.let { j += it; j } else j
            i++
        }
        return number
    }

    fun getValues(): List<BerTlv>? {
        check(!isPrimitive()) { "Tag is PRIMITIVE" }
        return theList
    }

    override fun toString(): String {
        return "BerTlv{" + "theTag=" + theTag + ", theValue=" + Arrays.toString(theValue) + ", theList=" + theList + '}'
    }
}
