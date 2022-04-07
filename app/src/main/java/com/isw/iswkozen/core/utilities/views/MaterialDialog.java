package com.isw.iswkozen.core.utilities.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.isw.iswkozen.R;

public class MaterialDialog {

    private Context context;
    private int position;
    private MaterialAlertDialogBuilder builder;

    public MaterialDialog(Context context) {
        this.builder = new MaterialAlertDialogBuilder(context, R.style.BottomSheetDialog);
        this.context = context;
    }

    public void showInputDialog(String title, String defaultText, final OnInputListener inputListener) {
        this.showInputDialog(title, null, null, defaultText, InputType.TYPE_CLASS_TEXT, inputListener, null);
    }

    public void showInputDialog(String title, String defaultText, final int inputType, final OnInputListener inputListener) {
        this.showInputDialog(title, null, null, defaultText, inputType, inputListener, null);
    }

    public void showInputDialog(String title, String defaultText, final int inputType, final OnInputListener inputListener,
                                final OnInputCancelListener inputCancelListener) {
        this.showInputDialog(title, null, null, defaultText, inputType, inputListener, inputCancelListener);
    }

    public void showInputDialog(String title, String hint, String message, final int inputType, final OnInputListener inputListener) {
        this.showInputDialog(title, hint, message, null, inputType, inputListener, null);
    }

    public void showInputDialog(String title, String hint, String message, String defaultText, final int inputType,
                                final OnInputListener inputListener, final OnInputCancelListener inputCancelListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.layout_input, null);
        final EditText input = view.findViewById(android.R.id.input);
        if (input == null) {
            return;
        }
        input.setHint(hint);
        input.setSingleLine();
        input.setInputType(inputType);
        input.setText(defaultText);
        input.setSelection(input.length());
        if ((inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        Dialog dialog = builder.setTitle(title)
                .setMessage(message)
                .setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(input.getText())) {
                            if ((inputType & InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER) {
                                inputListener.onInput("0");
                            } else {
                                inputListener.onInput(input.getText());
                            }
                        } else {
                            inputListener.onInput(input.getText());
                        }
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (inputCancelListener != null) {
                            inputCancelListener.onCancel();
                        }
                    }
                })
                .show();

        if (inputCancelListener != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
        }
    }

    public void showConfirmDialog(String title, String message, final OnConfirmListener confirmListener) {
        Dialog dialog = builder.setTitle(title).setMessage(message)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        confirmListener.onConfirm();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        confirmListener.onCancel();
                    }
                })
                .show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }

    public void showListConfirmChoseDialog(String title, String[] list, final OnChoseListener choseListener) {
        position = -1;

        Dialog dialog = builder.setTitle(title).setSingleChoiceItems(list, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        MaterialDialog.this.position = position;
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        choseListener.onChose(position);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        choseListener.onChose(-1);
                    }
                }).show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }

    public void showListSimpleChoseDialog(String title, String[] list, final OnChoseListener choseListener) {
        this.showListSimpleChoseDialog(title, list, -1, choseListener);
    }

    public void showListSimpleChoseDialog(String title, String[] list, int checkedItem, final OnChoseListener choseListener) {
        position = -1;

        builder.setTitle(title).setSingleChoiceItems(list, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        MaterialDialog.this.position = position;
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (position != -1) {
                            choseListener.onChose(position);
                        }
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                })
                .show();
    }

    public interface OnConfirmListener {

        void onConfirm();

        void onCancel();
    }

    public interface OnChoseListener {

        void onChose(int position);
    }

    public interface OnInputListener {

        void onInput(CharSequence input);
    }

    public interface OnInputCancelListener {

        void onCancel();
    }
}
