package com.isw.iswkozen.core.data.models

import java.util.*

class TransactionData {

    private var transId: String? = null
    private var cardType = 0
    private var transType = 0
    private var transResult = 0
    private var appleVasResult = 0
    private var transAmount: Double? = null
    private var transAmountOther: Double? = null
    private var transDate: Calendar? = null
    private var transData: ByteArray? =  null
    private var appleVasData: ByteArray? = null

    fun TransactionData() {
        transDate = Calendar.getInstance()
    }

    fun getTransId(): String {
        return transId!!
    }

    fun setTransId(transId: String) {
        this.transId = transId
    }

    fun getCardType(): Int {
        return cardType
    }

    fun setCardType(cardType: Int) {
        this.cardType = cardType
    }

    fun getTransType(): Int {
        return transType
    }

    fun setTransType(transType: Int) {
        this.transType = transType
    }

    fun getTransAmountOther(): Double? {
        return transAmountOther
    }

    fun setTransAmountOther(transAmountOther: Double?) {
        this.transAmountOther = transAmountOther
    }

    fun getTransResult(): Int {
        return transResult
    }

    fun setTransResult(transResult: Int) {
        this.transResult = transResult
    }

    fun getAppleVasResult(): Int {
        return appleVasResult
    }

    fun setAppleVasResult(appleVasResult: Int) {
        this.appleVasResult = appleVasResult
    }

    fun getTransAmount(): Double? {
        return transAmount
    }

    fun setTransAmount(transAmount: Double?) {
        this.transAmount = transAmount
    }

    fun getTransDate(): Calendar? {
        return transDate
    }

    fun setTransDate(transDate: Calendar?) {
        this.transDate = transDate
    }

    fun getTransData(): ByteArray? {
        return transData
    }

    fun setTransData(transData: ByteArray) {
        this.transData = transData
    }

    fun getAppleVasData(): ByteArray? {
        return appleVasData
    }

    fun setAppleVasData(appleVasData: ByteArray) {
        this.appleVasData = appleVasData
    }

    override fun toString(): String {
        return transId!!
    }

    override fun equals(obj: Any?): Boolean {
        return (obj is TransactionData
                && transId == obj.transId)
    }

    override fun hashCode(): Int {
        return Objects.hash(transId)
    }

    protected fun clone(): Any? {
        return TransactionData()
    }
}