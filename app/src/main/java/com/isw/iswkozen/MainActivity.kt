package com.isw.iswkozen

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.isw.iswkozen.core.utilities.DeviceUtils.requestPermissionsCompat
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent

class MainActivity : AppCompatActivity(), View.OnClickListener, KoinComponent {

    private val viewmodel : IswKozenViewModel by viewModel()

    lateinit var eraseKeyBtn: Button
    lateinit var dukptKeyBtn: Button
    lateinit var pinKeyBtn: Button
    lateinit var readTermInfoBtn: Button
    lateinit var downloadTermInfoBtn: Button
    lateinit var getISWToken: Button
    lateinit var startTransactionBtn: Button
    lateinit var getTransactionDataBtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        testLoads()
        eraseKeyBtn = findViewById(R.id.erase_key)
        dukptKeyBtn = findViewById(R.id.dukpy_key)
        pinKeyBtn = findViewById(R.id.pinkey)
        readTermInfoBtn = findViewById(R.id.read_term_info)
        downloadTermInfoBtn = findViewById(R.id.downl_term_info)
        getISWToken = findViewById(R.id.get_token)
        startTransactionBtn = findViewById(R.id.start_trans)
        getTransactionDataBtn = findViewById(R.id.get_transaction_data)



        checkPermission()

        handleClicks()
    }


    private fun hasReadPhonePermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
    }

    fun checkPermission() {

        if (hasReadPhonePermissions()) {
           Toast.makeText(this, "permissions granted", Toast.LENGTH_LONG)
        }  else {
          requestPermission()
        }

        // Wait for permission result
    }

    private fun requestPermission() {
        // Request the permission. The result will be received in onRequestPermissionResult().
        requestPermissionsCompat(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE), 4, this)
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
        downloadTermInfoBtn.setOnClickListener(this)
        getISWToken.setOnClickListener(this)
        startTransactionBtn.setOnClickListener(this)
        getTransactionDataBtn.setOnClickListener(this)
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

            downloadTermInfoBtn -> {
                viewmodel.dowloadDetails()
                viewmodel.downloadterminalDetailsStatus.observe(this, Observer {
                   println("download successful => $it")
                })
            }

            getISWToken -> {
                viewmodel.getISWToken()
            }

            startTransactionBtn -> {
                viewmodel.startTransaction(1000, 0, 0, this)
            }

            getTransactionDataBtn -> {
                viewmodel.getTransactionData()
                viewmodel.makeOnlineRequest()
            }
        }
    }
}