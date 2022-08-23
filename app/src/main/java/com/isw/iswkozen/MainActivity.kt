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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.isw.iswkozen.core.utilities.DeviceUtils.requestPermissionsCompat
import com.isw.iswkozen.core.utilities.DisplayUtils.hide
import com.isw.iswkozen.core.utilities.DisplayUtils.show
import com.isw.iswkozen.views.viewmodels.BluetoothViewModel
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import com.isw_smart_bluetooth.enums.TillReturnCodes
import com.isw_smart_bluetooth.interfaces.TillCallBackListener
import com.isw_smart_bluetooth.interfaces.TillCommand
import com.isw_smart_bluetooth.service.TillBluetoothManager
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent

class MainActivity : AppCompatActivity(), View.OnClickListener, KoinComponent {

    private val viewmodel : IswKozenViewModel by viewModel()

    private val blueviewmodel: BluetoothViewModel by viewModel()
    lateinit var tillBluetooth : TillBluetoothManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupPage()
        checkPermission()
        setupTerminals()

        tillBluetooth = TillBluetoothManager(this, listener)
        tillBluetooth.startBluetooth()

        with(blueviewmodel) {
            bluetoothTransactionResponse.observe(this@MainActivity, Observer {
                tillBluetooth.sendTransactionResponse(it)
            })
        }
    }

    private fun setupPage(){

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNav, navHostFragment!!.findNavController())

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.amountFragment || destination.id == R.id.processingFragment
                || destination.id == R.id.receiptFragment
                || destination.id == R.id.transactionHistoryFragment
                || destination.id == R.id.settingsLandingFragment
                || destination.id == R.id.ussdFragment
                || destination.id == R.id.transferFragment) {
                bottomNav.hide()
            } else {
                bottomNav.show()
            }
        }
    }

    val listener = object: TillCallBackListener {

        override fun onMessageReceived(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun onCommandReceived(command: TillCommand) {
            when(command) {
                is TillCommand.Purchase -> {
                    Toast.makeText(this@MainActivity, command.command.amount, Toast.LENGTH_SHORT).show()
                    blueviewmodel.pushCommand(command)
                }
            }
        }

        override fun onConnected(device: String) {
            Toast.makeText(this@MainActivity, "Connected to $device", Toast.LENGTH_SHORT).show()
        }

        override fun onStateChanged() {
            TODO("Not yet implemented")
        }

        override fun onError(error: TillReturnCodes, message: String?) {
            TODO("Not yet implemented")
        }

        override fun onDisConnected() {
            Toast.makeText(this@MainActivity, "Disconnected", Toast.LENGTH_SHORT).show()
        }


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


    fun setupTerminals() {
        if (Prefs.getString("KSN", "").isNullOrEmpty()) {
            Toast.makeText(this, "Kindly wait while details are being downloaded", Toast.LENGTH_LONG).show()
           runBlocking {
//               viewmodel.eraseKeys()
//               viewmodel.writePinKey()
               viewmodel.writeDukptKey()
               viewmodel.loadAllConfig()
//               viewmodel.dowloadDetails()
           }
        }

        viewmodel.checkKey()

    }

//    fun handleClicks(){
//        eraseKeyBtn.setOnClickListener(this)
//        dukptKeyBtn.setOnClickListener(this)
//        pinKeyBtn.setOnClickListener(this)
//        readTermInfoBtn.setOnClickListener(this)
//        downloadTermInfoBtn.setOnClickListener(this)
//        getISWToken.setOnClickListener(this)
//        startTransactionBtn.setOnClickListener(this)
//        getTransactionDataBtn.setOnClickListener(this)
//    }

    override fun onClick(p0: View?) {
        when(p0) {
//            eraseKeyBtn -> {
//              viewmodel.eraseKeys()
//            }
//
//            dukptKeyBtn -> {
//                viewmodel.writeDukptKey()
//            }
//
//            pinKeyBtn -> {
//                viewmodel.writePinKey()
//            }
//
//            readTermInfoBtn -> {
//                viewmodel.readterminalDetails()
//            }
//
//            downloadTermInfoBtn -> {
//                viewmodel.dowloadDetails()
//                viewmodel.downloadterminalDetailsStatus.observe(this, Observer {
//                   println("download successful => $it")
//                })
//            }
//
//            getISWToken -> {
//                viewmodel.getISWToken()
//            }
//
//            startTransactionBtn -> {
//                viewmodel.startTransaction(1000, 0, 0, this)
//            }
//
//            getTransactionDataBtn -> {
//                viewmodel.getTransactionData()
//                viewmodel.makeOnlineRequest()
//            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}