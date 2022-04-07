package com.isw.iswkozen.core.data.models;

import androidx.annotation.NonNull;


import com.isw.iswkozen.core.utilities.BerTag;
import com.isw.iswkozen.core.utilities.BerTlv;
import com.isw.iswkozen.core.utilities.BerTlvParser;
import com.isw.iswkozen.core.utilities.BerTlvs;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmvCard implements Serializable {

    EmvTrack1 track1;
    EmvTrack2 track2;
    private String    cardNumber;
    private Date      cardExpireDate;
    private int       cardSequenceNumber = -1;
    private String    cardHolderName;
    private String    appLabel;
    private String    serviceCode;
    private int       issuerCountryCode  = -1;

    public EmvCard(byte[] data) {
        BerTlvParser tlvParser = new BerTlvParser();
        BerTlvs tlvs = tlvParser.parse(data);
        BerTlv tlv = tlvs.find(new BerTag("5F24"));
        if (tlv != null) {
            Date date = null;
            DateFormat format = new SimpleDateFormat("yyMMdd", Locale.getDefault());
            try {
                date = format.parse(tlv.getHexValue());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cardExpireDate = date;
        }

        tlv = tlvs.find(new BerTag("5F20"));
        if (tlv != null) {
            cardHolderName = tlv.getTextValue();
        }

        tlv = tlvs.find(new BerTag("5F28"));
        if (tlv != null) {
            issuerCountryCode = Integer.parseInt(tlv.getHexValue());
        }

        tlv = tlvs.find(new BerTag("9F12"));
        if (tlv == null) {
            tlv = tlvs.find(new BerTag("50"));
        }
        if (tlv != null) {
            appLabel = tlv.getTextValue();
        }

        tlv = tlvs.find(new BerTag("5A"));

        if (tlv != null) {
            cardNumber = tlv.getHexValue();
        } else {
            tlv = tlvs.find(new BerTag("DF33"));
            if (tlv != null) {
                cardNumber = tlv.getTextValue();
            }
        }

        tlv = tlvs.find(new BerTag("5F34"));
        if (tlv != null) {
            cardSequenceNumber = tlv.getIntValue();
        }

        tlv = tlvs.find(new BerTag("5F30"));
        if (tlv != null) {
            serviceCode = tlv.getHexValue();
        }

        tlv = tlvs.find(new BerTag("56"));
        if (tlv != null) {
            track1 = extractTrack1Data(tlv.getBytesValue());
        } else {
            tlv = tlvs.find(new BerTag("DF41"));
            if (tlv != null) {
                String str = new String(tlv.getBytesValue()).replace("*", "0");
                track1 = extractTrack1Data(str.getBytes());
            }
        }

        tlv = tlvs.find(new BerTag("57"));
        if (tlv == null) {
            tlv = tlvs.find(new BerTag("9F20"));
        }
        if (tlv == null) {
            tlv = tlvs.find(new BerTag("9F6B"));
        }
        if (tlv != null) {
            track2 = extractTrack2Data(tlv.getHexValue().getBytes());
        } else {
            tlv = tlvs.find(new BerTag("DF42"));
            if (tlv != null) {
                String str = new String(tlv.getBytesValue()).replace("*", "0");
                track2 = extractTrack2Data(str.getBytes());
            }
        }

        if (track1 != null) {
            if (cardNumber == null) {
                cardNumber = track1.getCardNumber();
            }

            if (cardExpireDate == null) {
                cardExpireDate = track1.getExpireDate();
            }

            if (serviceCode == null) {
                serviceCode = track1.getService();
            }

            if (cardHolderName == null) {
                cardHolderName = track1.getCardHolderName();
            }
        }

        if (track2 != null) {
            if (cardNumber == null) {
                cardNumber = track2.getCardNumber();
            }

            if (cardExpireDate == null) {
                cardExpireDate = track2.getExpireDate();
            }

            if (serviceCode == null) {
                serviceCode = track2.getService();
            }
        }

        if (cardNumber != null) {
            int cardIndex = cardNumber.toUpperCase().indexOf('F');
            if (cardIndex != -1) {
                cardNumber = cardNumber.substring(0, cardIndex);
            }
        }
    }

   EmvTrack1 getTrack1() {
        return track1;
    }

    EmvTrack2 getTrack2() {
        return track2;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Date getCardExpireDate() {
        return cardExpireDate;
    }

    public int getCardSequenceNumber() {
        return cardSequenceNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public int getIssuerCountryCode() {
        return issuerCountryCode;
    }

    private static final Pattern TRACK1_PATTERN = Pattern.compile("%?([A-Z])([0-9]{1,19})(\\?[0-9])?\\^([^\\^]{2,26})\\^([0-9]{4}|\\^)([0-9]{3}|\\^)([^?]+)\\??");
    private static final Pattern TRACK2_PATTERN = Pattern.compile("([0-9]{1,19})[=D]([0-9]{4})([0-9]{3})?(.*)");

    public static EmvTrack1 extractTrack1Data(byte[] data) {
        EmvTrack1 track1 = null;

        if (data != null) {
           new EmvTrack1();
            track1.setRaw(data);
            Matcher matcher = TRACK1_PATTERN.matcher(new String(data));
            if (matcher.find()) {
                track1.setFormatCode(matcher.group(1));
                track1.setCardNumber(matcher.group(2));
                String name = matcher.group(4);
                if (name != null) {
                    track1.setCardHolderName(name.trim());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyMM", Locale.getDefault());
                try {
                    track1.setExpireDate(sdf.parse(Objects.requireNonNull(matcher.group(5))));
                } catch (ParseException e) {
                    return null;
                }
                track1.setService(matcher.group(6));
            }
        }

        return track1;
    }

    public static EmvTrack2 extractTrack2Data(byte[] data) {
        EmvTrack2 track2 = null;

        if (data != null) {
            track2 = new EmvTrack2();
            track2.setRaw(data);
            Matcher matcher = TRACK2_PATTERN.matcher(new String(data));
            if (matcher.find()) {
                track2.setCardNumber(matcher.group(1));
                SimpleDateFormat sdf = new SimpleDateFormat("yyMM", Locale.getDefault());
                try {
                    track2.setExpireDate(sdf.parse(Objects.requireNonNull(matcher.group(2))));
                } catch (ParseException e) {
                    return null;
                }
                track2.setService(matcher.group(3));
            }
        }

        return track2;
    }

    @NonNull
    @Override
    public String toString() {
        return "EmvCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardExpireDate=" + cardExpireDate +
                ", cardSequenceNumber=" + cardSequenceNumber +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", appLabel='" + appLabel + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", issuerCountryCode=" + issuerCountryCode +
                ", track2=" + track2 +
                ", track1=" + track1 +
                '}';
    }
}
