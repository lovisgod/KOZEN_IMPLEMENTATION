package com.isw.iswkozen.core.data.models;

import androidx.annotation.NonNull;


import com.isw.iswkozen.core.utilities.HexUtil;

import java.util.Date;

public class EmvTrack1 {

    private byte[] raw;
    private String formatCode;
    private String cardNumber;
    private Date   expireDate;
    private String cardHolderName;
    private String service;

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(final byte[] raw) {
        this.raw = raw;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(final Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getFormatCode() {
        return formatCode;
    }

    public void setFormatCode(final String formatCode) {
        this.formatCode = formatCode;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getService() {
        return service;
    }

    public void setService(final String service) {
        this.service = service;
    }

    @NonNull
    @Override
    public String toString() {
        return "EmvTrack1{" +
                "raw=" + HexUtil.toHexString(raw) +
                ", formatCode='" + formatCode + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", expireDate=" + expireDate +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
