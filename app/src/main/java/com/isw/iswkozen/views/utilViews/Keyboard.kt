package com.isw.iswkozen.views.utilViews

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import com.isw.iswkozen.R

class Keyboard(activity: Activity, callback: KeyBoardListener, view: View) : View.OnClickListener {

    interface KeyBoardListener {
        fun onTextChange(text: String)
        fun onSubmit(text: String)
    }


    private lateinit var mContext: Context
    private var result = ""
    private lateinit var mCallback: KeyBoardListener


    init {
        println("activity: $activity")
        setupButtons(activity, callback, view)
    }


    private fun setupButtons(activity: Activity, callback: KeyBoardListener, view: View) {
        mContext = activity
        // add buttons
        view.findViewById<View>(R.id.btn0).setOnClickListener(this)
        view.findViewById<View>(R.id.btn1).setOnClickListener(this)
        view.findViewById<View>(R.id.btn2).setOnClickListener(this)
        view.findViewById<View>(R.id.btn3).setOnClickListener(this)
        view.findViewById<View>(R.id.btn4).setOnClickListener(this)
        view.findViewById<View>(R.id.btn5).setOnClickListener(this)
        view.findViewById<View>(R.id.btn6).setOnClickListener(this)
        view.findViewById<View>(R.id.btn7).setOnClickListener(this)
        view.findViewById<View>(R.id.btn8).setOnClickListener(this)
        view.findViewById<View>(R.id.btn9).setOnClickListener(this)
        view.findViewById<View>(R.id.btn_dot).setOnClickListener(this)
//        view.findViewById<View>(R.id.btnClear).setOnClickListener(this)
        view.findViewById<View>(R.id.btnConfirm).setOnClickListener(this)




        view.findViewById<View>(R.id.btnClear).let {
            it.setOnClickListener(this)

            // clear text on long click
            it.setOnLongClickListener {
                result = ""
                mCallback.onTextChange(result)
                true
            }
        }


        // set callback
        mCallback = callback
    }

    override fun onClick(view: View) {
        val maxValue = 1000000000L // 10 million (10,000,000.00)
        var result = result
        when (view.id) {
            R.id.btn1 -> {
                result += "1"
            }
            R.id.btn2 -> {
                result += "2"
            }
            R.id.btn3 -> {
                result += "3"
            }
            R.id.btn4 -> {
                result += "4"
            }
            R.id.btn5 -> {
                result += "5"
            }
            R.id.btn6 -> {
                result += "6"
            }
            R.id.btn7 -> {
                result += "7"
            }
            R.id.btn8 -> {
                result += "8"
            }
            R.id.btn9 -> {
                result += "9"
            }
            R.id.btn0 -> {
                result += "0"
            }
//            R.id.btn9 -> {
//                result += "00"
//            }
//            R.id.btn0 -> {
//                result += "000"
//            }
            R.id.btnClear -> {
                if (result.length > 0) {
                    result = result.substring(0, result.length -1)
                }
            }
            R.id.btnConfirm -> {
                return mCallback.onSubmit(result)
            }
            else -> {
                return
            }
        }
        val isDisabled =
            !result.isEmpty() && java.lang.Long.valueOf(result) > maxValue
        if (!isDisabled) {
            this.result = result
            mCallback.onTextChange(result)
        } else {
            // show max input
            Toast.makeText(mContext, "Max value is 10 Million", Toast.LENGTH_SHORT).show()
        }
    }

    fun setText(text: String) {
        result = text
    }

}