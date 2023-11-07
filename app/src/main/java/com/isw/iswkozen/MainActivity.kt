package com.isw.iswkozen

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.isw.iswkozen.core.network.NetworkStatusChecker
import com.isw.iswkozen.core.utilities.DeviceUtils
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

    lateinit var connectionLayout: FrameLayout
    lateinit var connectionText: TextView

    var handler : Handler = Handler()
    var runnable: Runnable? = null
    var delay  = 300000
//    var delay = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectionLayout = findViewById(R.id.network_cont)
        connectionText = findViewById(R.id.network_text)

        setupPage()
        checkPermission()
        setupTerminals()

//        tillBluetooth = TillBluetoothManager(this, listener)
//        tillBluetooth.startBluetooth()

        with(blueviewmodel) {
            bluetoothTransactionResponse.observe(this@MainActivity, Observer {
//                tillBluetooth.sendTransactionResponse(it)
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
//               viewmodel.writeDukptKey()
//               viewmodel.loadAllConfig()
               viewmodel.dowloadDetails()
           }
        }

//        viewmodel.checkKey()

    }


    override fun onClick(p0: View?) {
        when(p0) {

        }
    }

    override fun onResume() {
        super.onResume()
        var status  = NetworkStatusChecker.isConnectedToInternet(this)
//        var status = NetworkStatusChecker.networkConnectionState
        runOnUiThread {
            if (status) {
                connectionText.text = "Connection is good"
                connectionLayout.background = ContextCompat.getDrawable(this, R.drawable.success_bg)
            } else {
                connectionText.text = "Connection is not ok"
                connectionLayout.background = ContextCompat.getDrawable(this, R.drawable.error_bg)
            }
        }
    }


    override fun onPause() {
        super.onPause()
    }
}