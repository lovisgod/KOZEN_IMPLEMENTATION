package com.isw.iswkozen

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.IswTxnHandler
import com.interswitchng.smartpos.emv.pax.services.POSDeviceImpl
import com.interswitchng.smartpos.models.core.Environment
import com.interswitchng.smartpos.models.core.IswLocal
import com.interswitchng.smartpos.models.core.POSConfig
import com.interswitchng.smartpos.models.core.PurchaseConfig
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.isw.iswkozen.core.data.models.DeviceType
import com.isw.iswkozen.di.ExportModules
import com.pixplicity.easyprefs.library.Prefs
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext


class IswApplication: Application() {
    var paxHandler: IswTxnHandler ? = null
    override fun onCreate() {
        super.onCreate()
        setUpPax()

        loadModules()

        Prefs.Builder()
            .setContext(this)
            .setMode(MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    private fun setUpPax() {
        if (DEVICE_TYPE == DeviceType.PAX) {
            configureTerminal()
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun configureTerminal() {
        var device: POSDevice? = null
        val logo = ContextCompat.getDrawable(this, R.drawable.ic_success)
        val bm: Bitmap? = drawableToBitmap(logo!!)
        val service = POSDeviceImpl.create(applicationContext)
        if (bm  != null) {
            service.setCompanyLogo(bm)
        }
        device = service


        val clientId = "IKIA4733CE041F41ED78E52BD3B157F3AAE8E3FE153D"
        val clientSecret = "t1ll73stS3cr3t"
        val alias = "000001"
        val merchantCode = "MX1065"
        val merchantPhone = "080311402392"
        val config =
            POSConfig(alias, clientId, clientSecret, merchantCode, merchantPhone, Environment.Production)
        config.withPurchaseConfig(PurchaseConfig(1, "tech@isw.ng", IswLocal.NIGERIA))
        IswPos.setDeviceSetialNumber(getDeviceSerialNew(device)!!)
        // setup terminal
        IswPos.setupTerminal(this, device, null, config, false, true)


        paxHandler = IswTxnHandler(posDevice = device)
    }

    private fun getDeviceSerialNew(posDevice: POSDevice): String? {
        return posDevice.serialNumber()
    }

    fun appContext(app: Application) = module(override = true) {
        single { app.applicationContext }
    }

    fun loadModules(){
// set up koin
        var modules = arrayListOf<Module>()
        modules.add(appContext(this))
        modules.addAll(ExportModules.modules)
        StandAloneContext.loadKoinModules(modules)
    }

    companion object {
        val clientId: String = "IKIA4733CE041F41ED78E52BD3B157F3AAE8E3FE153D"
        val clientSecret: String = "t1ll73stS3cr3t"
        val DEVICE_TYPE = DeviceType.PAX
        val paxHandler = IswApplication().paxHandler
    }
}