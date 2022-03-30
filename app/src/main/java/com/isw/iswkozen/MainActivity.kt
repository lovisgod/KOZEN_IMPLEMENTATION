package com.isw.iswkozen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent

class MainActivity : AppCompatActivity(), View.OnClickListener, KoinComponent {

    private val viewmodel : IswKozenViewModel by viewModel()

    lateinit var eraseKeyBtn: Button
    lateinit var dukptKeyBtn: Button
    lateinit var pinKeyBtn: Button
    lateinit var readTermInfoBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        testLoads()
        eraseKeyBtn = findViewById(R.id.erase_key)
        dukptKeyBtn = findViewById(R.id.dukpy_key)
        pinKeyBtn = findViewById(R.id.pinkey)
        readTermInfoBtn = findViewById(R.id.read_term_info)

        handleClicks()
    }


    fun testLoads() {
        viewmodel.setupTerminal()
        viewmodel.loadAllConfig()
    }

    fun handleClicks(){
        eraseKeyBtn.setOnClickListener(this)
        dukptKeyBtn.setOnClickListener(this)
        pinKeyBtn.setOnClickListener(this)
        readTermInfoBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            eraseKeyBtn -> {
              viewmodel.eraseKeys()
            }

            dukptKeyBtn -> {
                viewmodel.writeDukptKey()
            }

            pinKeyBtn -> {
                viewmodel.writePinKey()
            }

            readTermInfoBtn -> {
                viewmodel.readterminalDetails()
            }
        }
    }
}