package com.isw.iswkozen.core.utilities.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.isw.iswkozen.R;
import com.isw.iswkozen.core.data.utilsData.Constants;
import com.isw.iswkozen.core.data.utilsData.KeysUtils;
import com.isw.iswkozen.core.network.models.MemoryPinData;
import com.isw.iswkozen.core.network.models.StringManipulator;
import com.isw.iswkozen.core.utilities.HexUtil;
import com.isw.pinencrypter.Converter;
import com.pixplicity.easyprefs.library.Prefs;
import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvPinConstraints;
import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.security.PedRsaPinKey;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PasswordDialog {

    public static final int PLAIN_PIN    = 1;
    public static final int ONLINE_PIN   = 2;
    public static final int ENCIPHER_PIN = 3;

    private String DEFAULT_EXP_PIN_LEN_IND = "0,4,5,6,7,8,9,10,11,12";
    private int    DEFAULT_TIMEOUT_MS      = 30000;

    private int keyIndex;
    private int keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK;
    private int icSlot;

    private boolean isKeyboardFix = true;
    private boolean isEncrypt;
    private String  pinCard;
    private int     pinType;
    private boolean pinBypass;
    private int     pinCounter;
    private byte[]  pinRandom;
    private byte[]  pinModule;
    private byte[]  pinExponent;
    private String pinX = "";

    private String title;
    private String message;

    private POIHsmManage     hsmManage;
    private PinEventListener pinEventListener;

    private Dialog    dialog;
    private TextView  tvMessage;
    private EditText  etPin;
    private Button    btnConfirm;
    private ImageView btnClear;
    private TextView  btnEsc, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;

    public PasswordDialog(Context context, boolean isIcSlot, Bundle bundle, int keyIndex, int pinMode) {
        this.hsmManage = POIHsmManage.getDefault();
        this.pinEventListener = new PinEventListener();
        if (isIcSlot) {
            this.icSlot = 0;
        } else {
            this.icSlot = 10;
        }
        this.keyIndex = keyIndex;
        this.keyMode = pinMode;

        switch (bundle.getInt(EmvPinConstraints.PIN_TYPE, -1)) {
            case POIEmvCoreManager.PIN_PLAIN_PIN:
                pinType = PLAIN_PIN;
                break;
            case POIEmvCoreManager.PIN_ONLINE_PIN:
                pinType = ONLINE_PIN;
                break;
            case POIEmvCoreManager.PIN_ENCIPHER_PIN:
                pinType = ENCIPHER_PIN;
                break;
            default:
                break;
        }

        if (bundle.containsKey(EmvPinConstraints.PIN_ENCRYPT)) {
            isEncrypt = false;
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_CARD)) {
            pinCard = bundle.getString(EmvPinConstraints.PIN_CARD);
            System.out.println("pin card => " + pinCard);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_BYPASS)) {
            pinBypass = bundle.getBoolean(EmvPinConstraints.PIN_BYPASS);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_COUNTER)) {
            pinCounter = bundle.getInt(EmvPinConstraints.PIN_COUNTER);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_CARD_RANDOM)) {
            pinRandom = bundle.getByteArray(EmvPinConstraints.PIN_CARD_RANDOM);
            System.out.println("pin card random => " + pinCard);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_MODULE)) {
            pinModule = bundle.getByteArray(EmvPinConstraints.PIN_MODULE);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_EXPONENT)) {
            pinExponent = bundle.getByteArray(EmvPinConstraints.PIN_EXPONENT);
        }

        switch (pinType) {
            case ONLINE_PIN:
                title = "Online PIN";
                break;
            case PLAIN_PIN:
            case ENCIPHER_PIN:
                title = "Offline PIN";
                if (pinCounter > 1) {
                    message = "PIN " + pinCounter + " ";
                } else if (pinCounter == 1) {
                    message = "PIN Last Times";
                }
                break;
            default:
                break;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.layout_password, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvMessage = view.findViewById(R.id.tvMessage);
        etPin = view.findViewById(R.id.etPin);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnClear = view.findViewById(R.id.btnClear);
        btnEsc = view.findViewById(R.id.btnEsc);
        btn0 = view.findViewById(R.id.btn0);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);
        btn7 = view.findViewById(R.id.btn7);
        btn8 = view.findViewById(R.id.btn8);
        btn9 = view.findViewById(R.id.btn9);

        Group groupKeyboard = view.findViewById(R.id.groupKeyboard);
//        if (Constants.INSTANCE.isHardWareKeyBoard()) {
//            groupKeyboard.setVisibility(View.VISIBLE);
//        }

        tvTitle.setText(title);
        tvMessage.setText(message);

        System.out.println("context: " + context);

        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.BOTTOM);

//        handleKeyInput();


    }

    public void checkPinInput(){
        StringBuilder info = new StringBuilder();
        int xxxx  = pinX.length();
        while (0 != (xxxx--)) {
            info.append("*");
        }
        if (info.length() <= 12) {
            etPin.setText(info.toString());
        }
    }

    public void handleKeyInput(){
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                pinX += ((TextView) view).getText().toString();
                checkPinInput();
            }
        });

        btnEsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("keyclicked :" + ((TextView) view).getText().toString());
                dialog.dismiss();
            }
        });

        btnClear.setOnClickListener(view -> {
//                System.out.println("keyclicked :" + ((ImageView) view).getText().toString());
            pinX = StringManipulator.INSTANCE.dropLastCharacter(pinX);
            checkPinInput();
        });

        btnConfirm.setOnClickListener(view -> {
            System.out.println("keyclicked :" + ((TextView) view).getText().toString());
            System.out.println("pin inputs :" + pinX);
            System.out.println("pin card :" + pinCard);
            String IPEKK = Prefs.getString("IPEK", "");
            String KSNX = Prefs.getString("KSN", "");
            String newKSN = KSNX + Constants.INSTANCE.getNextKsnCounter();

            String block = Converter.INSTANCE.GetPinBlock(IPEKK, newKSN, pinX, pinCard);
            onPinSuccessISW(block, StringManipulator.INSTANCE.dropFirstCharacter(newKSN));
            dialog.dismiss();
        });

    }


    public int showDialog() {
        int result;
        System.out.println("pintype :" + pinType);
        switch (pinType) {
            case PLAIN_PIN:
                result = onVerifyPlainPin();
                break;
            case ONLINE_PIN:
                result = onOnlinePin();
                break;
            case ENCIPHER_PIN:
                result = onVerifyEncipherPin();
                break;
            default:
                result = -1;
                break;
        }

        return result;
    }

    public void closeDialog() {
        dialog.dismiss();
        hsmManage.unregisterListener(pinEventListener);
    }

    private int onVerifyPlainPin() {
        dialog.show();
        hsmManage.registerListener(pinEventListener);
        return hsmManage.PedVerifyPlainPin(icSlot, 0, DEFAULT_TIMEOUT_MS, DEFAULT_EXP_PIN_LEN_IND);
    }

    private int onVerifyEncipherPin() {
        dialog.show();
        hsmManage.registerListener(pinEventListener);
        if (pinModule == null) {
            return -1;
        }
        byte[] module = new byte[pinModule.length];
        byte[] exponent = new byte[pinExponent.length];
        byte[] random = new byte[pinRandom.length];
        System.arraycopy(pinModule, 0, module, 0, pinModule.length);
        System.arraycopy(pinExponent, 0, exponent, 0, pinExponent.length);
        System.arraycopy(pinRandom, 0, random, 0, pinRandom.length);

        PedRsaPinKey rsaPinKey = new PedRsaPinKey(module, exponent, random);
        return hsmManage.PedVerifyCipherPin(icSlot, 0, DEFAULT_TIMEOUT_MS, DEFAULT_EXP_PIN_LEN_IND, rsaPinKey);
    }

    private int onOnlinePin() {

        dialog.show();
//        handleKeyInput();
        hsmManage.registerListener(pinEventListener);

        byte[] data = new byte[24];
        if (!isEncrypt) {
            byte[] temp = CalcPinBlock.calcPinBlock(pinCard).getBytes();
            System.arraycopy(temp, 0, data, 0, 16);
        } else {
            byte[] temp = pinCard.getBytes();
            System.arraycopy(temp, 0, data, 0, 16);
        }

        byte[] formatData = {0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(formatData, 0, data, 16, 8);

        int ret  = hsmManage.PedGetPinBlock(keyMode, keyIndex,
                0,
                DEFAULT_TIMEOUT_MS,
                data, DEFAULT_EXP_PIN_LEN_IND);

        System.out.println("pin block ret => " + ret);
        return  0;
    }

    private class PinEventListener implements POIHsmManage.EventListener {

        private String TAG = "PinEventListener";

        @Override
        public void onPedVerifyPin(POIHsmManage manage, int type, byte[] rspBuf) {
            if (type == POIHsmManage.PED_VERIFY_PIN_TYPE_PLAIN || type == POIHsmManage.PED_VERIFY_PIN_TYPE_CIPHER) {
                int sw1 = (rspBuf[1] >= 0 ? rspBuf[1] : (rspBuf[1] + 256));
                int sw2 = (rspBuf[2] >= 0 ? rspBuf[2] : (rspBuf[2] + 256));

                if (sw1 == 0x90 && sw2 == 0x00) {

                    onPinSuccess(null, null);
                } else if (sw1 == 0x63 && (sw2 & 0xc0) == (int) 0xc0) {
                    if ((sw2 & 0x0F) == 0) {
                        onPinError(EmvPinConstraints.VERIFY_PIN_BLOCK, 0);
                    } else {
                        onPinError(EmvPinConstraints.VERIFY_ERROR, sw2 & 0x0F);
                    }
                } else if (sw1 == 0x69 && (sw2 == (int) 0x83 || sw2 == (int) 0x84)) {
                    onPinError(EmvPinConstraints.VERIFY_PIN_BLOCK, 0);
                } else {
                    onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
                }
            } else {
                onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
            }
            closeDialog();
        }

        @Override
        public void onPedPinBlockRet(POIHsmManage manage, int type, byte[] rspBuf) {
            Log.d("rspbuff", "rsp GOT HERE " + HexUtil.toHexString(rspBuf));
            if (rspBuf[0] != 0) {
                byte[] pinBlock = new byte[rspBuf[0]];
                System.arraycopy(rspBuf, 1, pinBlock, 0, rspBuf[0]);
                if (rspBuf.length > (rspBuf[0] + 1)) {

                    Log.d("KSN", "KSN GOT HERE");

                    byte[] ksn = new byte[rspBuf[rspBuf[0] + 1]];
                    System.arraycopy(rspBuf, rspBuf[0] + 2, ksn, 0, rspBuf[rspBuf[0] + 1]);
                    onPinSuccess(pinBlock, ksn);
                }
                else {
                    onPinSuccess(pinBlock, null);
                }
            }
            closeDialog();
        }

        @Override
        public void onKeyboardShow(POIHsmManage manage, byte[] keys, int timeout) {
            if (isKeyboardFix) {
                byte[] fix = new byte[keys.length];
                byte[] random = new byte[keys.length];
                System.arraycopy(keys, 0, random, 0, keys.length);
                System.arraycopy(keys, 0, fix, 0, keys.length);

                fix[0] = 0x31;
                fix[1] = 0x32;
                fix[2] = 0x33;
                fix[3] = 0x34;
                fix[4] = 0x35;
                fix[5] = 0x36;
                fix[6] = 0x37;
                fix[7] = 0x38;
                fix[8] = 0x39;
                fix[10] = 0x30;

                byte[] KeyLayout = calculation(fix);
                byte[] randomKeyLayout = new byte[KeyLayout.length];
                switchFixToRandom(KeyLayout, random, randomKeyLayout);
                hsmManage.PedSetKeyLayout(randomKeyLayout, 0);
            } else {
                hsmManage.PedSetKeyLayout(calculation(keys), 0);
            }
        }

        @Override
        public void onKeyboardInput(POIHsmManage manage, int numKeys) {
            System.out.println("keys input => " + numKeys);
            StringBuilder info = new StringBuilder();
            while (0 != (numKeys--)) {
                info.append("*");
            }
            if (info.length() <= 12) {
                etPin.setText(info.toString());
            }
        }

        @Override
        public void onInfo(POIHsmManage manage, int what, int extra) {
            Log.e(TAG, "onInfo");
        }

        @Override
        public void onError(POIHsmManage manage, int what, final int extra) {
            Log.e(TAG, "onError:" + extra);

            Log.e(TAG, "onErrorWhat:" + what);

            switch (extra) {
                case 0xFFFF:
                case 0xFFFD:
                    onPinError(EmvPinConstraints.VERIFY_CANCELED, 0);
                    closeDialog();
                    return;
                case 0xFFFC:
                    tvMessage.setText("The terminal triggers a security check.");
                    break;
                case 0xFED3:
                    tvMessage.setText("The terminal did not write the PIN key. Please check.");
                    break;
                case 0XFECF:
                    if (pinBypass) {
                        onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
                    } else {
                        onPinError(EmvPinConstraints.VERIFY_ERROR, 0);
                    }
                    closeDialog();
                    return;
                default:
                    onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
                    closeDialog();
                    return;
            }

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    onPinError(EmvPinConstraints.VERIFY_CANCELED, 0);
                    closeDialog();
                }
            }, 2000);
        }

        @Override
        public void onHwSelfCheckRet(POIHsmManage manage, int type, int checkResult) {
            Log.e(TAG, "onHwSelfCheckRet");
        }

        @Override
        public void onHwSensorTriggered(POIHsmManage manage, int triggered, byte[] sensorValue, byte[] triggerTime) {
            Log.e(TAG, "onHwSensorTriggered");
        }

        @Override
        public void onPedKeyManageRet(POIHsmManage manage, int ret) {
            Log.e(TAG, "onPedKeyManageRet");
        }
    }

    private void onPinSuccess(byte[] pinBlock, byte[] pinKsn) {
        if (pinBlock != null) {
            Log.d("KSN", "KSN " + pinKsn);
            Log.d("PINBLOCK", "PINBLOCK " + HexUtil.toHexString(pinBlock));

           if (pinKsn != null) {
               MemoryPinData memoryPinData = new MemoryPinData(
                       HexUtil.toHexString(pinBlock), "dukpt",
                       StringManipulator.INSTANCE.dropFirstCharacter( HexUtil.toHexString(pinKsn)),
                       "605"
               );
               Constants.INSTANCE.setMemoryPinData(memoryPinData);
           } else {
               MemoryPinData memoryPinData = new MemoryPinData(
                       HexUtil.toHexString(pinBlock), "tpk",
                       "",
                       "605"
               );
               Constants.INSTANCE.setMemoryPinData(memoryPinData);
           }
        }
        Bundle bundle = new Bundle();
        bundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, EmvPinConstraints.VERIFY_SUCCESS);
        bundle.putInt(EmvPinConstraints.OUT_PIN_TRY_COUNTER, 0);
        if (pinBlock != null) {
            bundle.putByteArray(EmvPinConstraints.OUT_PIN_BLOCK, pinBlock);
        }
        if (pinKsn != null) {
            Log.e(null, "KSN " + HexUtil.toHexString(pinKsn));
        }
        POIEmvCoreManager.getDefault().onSetPinResponse(bundle);
    }


    private void onPinSuccessISW( String pinBlock, String pinKsn) {
        if (pinBlock != null) {
            MemoryPinData memoryPinData = new MemoryPinData(
                    pinBlock, "dukpt",
                    pinKsn, "605"
            );
            Constants.INSTANCE.setMemoryPinData(memoryPinData);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, EmvPinConstraints.VERIFY_SUCCESS);
        bundle.putInt(EmvPinConstraints.OUT_PIN_TRY_COUNTER, 0);
        if (pinBlock != null) {
            bundle.putByteArray(EmvPinConstraints.OUT_PIN_BLOCK, pinBlock.getBytes(StandardCharsets.UTF_8));
        }
        if (pinKsn != null) {
            Log.e(null, "KSN " + HexUtil.toHexString(pinKsn.getBytes(StandardCharsets.UTF_8)));
        }
        POIEmvCoreManager.getDefault().onSetPinResponse(bundle);
    }

    private void onPinError(int verifyResult, int pinTryCntOut) {
        Bundle bundle = new Bundle();
        bundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, verifyResult);
        bundle.putInt(EmvPinConstraints.OUT_PIN_TRY_COUNTER, pinTryCntOut);
        POIEmvCoreManager.getDefault().onSetPinResponse(bundle);
    }

    private byte[] calculation(byte[] keys) {
        HashMap<String, String> map = new HashMap<>();
        ByteBuffer coordinate = ByteBuffer.allocate(104);

        String esc = "Esc";
        String enter = "Enter";
        String clear = "Clear";

        map.put("0", "0");
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        map.put("4", "4");
        map.put("5", "5");
        map.put("6", "6");
        map.put("7", "7");
        map.put("8", "8");
        map.put("9", "9");
        map.put("-21", esc);
        map.put("-35", enter);
        map.put("-40", clear);

        TextView[] keyView = new TextView[11];

        if (isKeyboardFix) {
            keyView[0] = btn1;
            keyView[1] = btn2;
            keyView[2] = btn3;
            keyView[3] = btn4;
            keyView[4] = btn5;
            keyView[5] = btn6;
            keyView[6] = btn7;
            keyView[7] = btn8;
            keyView[8] = btn9;
            keyView[9] = btn0;
        } else {
            keyView[0] = btn0;
            keyView[1] = btn1;
            keyView[2] = btn2;
            keyView[3] = btn3;
            keyView[4] = btn4;
            keyView[5] = btn5;
            keyView[6] = btn6;
            keyView[7] = btn7;
            keyView[8] = btn8;
            keyView[9] = btn9;
            keyView[10] = btnEsc;
        }

        ImageView ivClear = btnClear;
        Button btnConfirm = this.btnConfirm;
        int viewIndex = 0;

        for (int i = 0; i <= 12; i++) {
            String value = map.get(String.valueOf(keys[i] - 0x30));
            View tv;

            if (value == null) {
                continue;
            } else if (value.equals(enter)) {
                tv = btnConfirm;
            } else if (value.equals(clear)) {
                tv = ivClear;
            } else {
                if (value.equals(esc)) {
                    tv = btnEsc;
                } else {
                    keyView[viewIndex].setText(value);
                    tv = keyView[viewIndex++];
                }
            }

            byte[] pos = new byte[8];
            int[] location = new int[2];
            tv.getLocationOnScreen(location);
            int leftX = location[0];
            int leftY = location[1];
            int rightX = location[0] + tv.getWidth();
            int rightY = location[1] + tv.getHeight();
            byte[] tmp0 = intToBytes(leftX);
            byte[] tmp1 = intToBytes(leftY);
            byte[] tmp2 = intToBytes(rightX);
            byte[] tmp3 = intToBytes(rightY);
            pos[0] = tmp0[2];
            pos[1] = tmp0[3];
            pos[2] = tmp1[2];
            pos[3] = tmp1[3];
            pos[4] = tmp2[2];
            pos[5] = tmp2[3];
            pos[6] = tmp3[2];
            pos[7] = tmp3[3];
            coordinate.put(pos);
        }

        return coordinate.array();
    }

    private void switchFixToRandom(byte[] fixKeyLayout, byte[] random, byte[] randomKeyLayout) {
        int position;
        System.arraycopy(fixKeyLayout, 0, randomKeyLayout, 0, fixKeyLayout.length);
        for (int i = 0; i < random.length; i++) {
            if (i != 9 && i <= 10) {
                if ((random[i] - 0x30) != 0) {
                    position = (random[i] - 0x30 - 1) * 8;
                } else {
                    position = 10 * 8;
                }
                System.arraycopy(fixKeyLayout, position, randomKeyLayout, i * 8, 8);
            }
        }
    }

    private byte[] intToBytes(int value) {
        return new byte[]{(byte) (value >> 24 & 255), (byte) (value >> 16 & 255), (byte) (value >> 8 & 255), (byte) (value & 255)};
    }

    static class CalcPinBlock {

        static String calcPinBlock(String accountNumber) {
            return "0000" + extractAccountNumberPart(accountNumber);
        }

        static String extractAccountNumberPart(String accountNumber) {
            String accountNumberPart;
            accountNumberPart = takeLastN(accountNumber, 13);
            accountNumberPart = takeFirstN(accountNumberPart, 12);
            return accountNumberPart;
        }

        static String takeLastN(String str, int n) {
            if (str.length() > n) {
                return str.substring(str.length() - n);
            } else {
                if (str.length() < n) {
                    return zero(str, n);
                } else {
                    return str;
                }
            }
        }

        static String takeFirstN(String str, int n) {
            if (str.length() > n) {
                return str.substring(0, n);
            } else {
                if (str.length() < n) {
                    return zero(str, n);
                } else {
                    return str;
                }
            }
        }

        static String zero(String str, int len) {
            str = str.trim();
            StringBuilder builder = new StringBuilder(len);
            int fill = len - str.length();
            while (fill-- > 0) {
                builder.append((char) 0);
            }
            builder.append(str);
            return builder.toString();
        }
    }
}
