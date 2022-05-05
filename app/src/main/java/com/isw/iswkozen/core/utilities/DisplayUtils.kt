package com.isw.iswkozen.core.utilities

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.IswLocal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


internal object DisplayUtils {

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into dp
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param sp A value in (scalable pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent pixel equivalent to sp value
     */
    fun convertSpToPixels(sp: Float, context: Context): Float {
        val scale = context.resources.displayMetrics.scaledDensity
        return sp * scale
    }



    fun getIsoString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+1")
        return dateFormat.format(date)
    }


    /**
     * This method converts interger amount notation to string (e.g. 1000 -> 1,000.00)
     *
     * @param amount A value in integer representing the transaction amount
     * @return A string representation of the decimal notation for the amount
     */
    fun getAmountString(amount: Double): String {

        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        return numberFormat.format(amount)
    }


    fun getAmountString(amount: Int): String {
        val amountAsDouble: Double = amount / 100.0
        return getAmountString(amountAsDouble)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        try {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
        }
    }

    fun getAmountWithCurrency(amount: String, terminalInfo: TerminalInfo): String {
        // get the currency based on the terminal's configured currency code
        val currency = when (val config = terminalInfo) {
            null -> ""
            else -> when(config.transCurrencyCode) {
                "0566" -> IswLocal.NIGERIA.currency
                IswLocal.NIGERIA.code -> IswLocal.NIGERIA.currency
                IswLocal.GHANA.code -> IswLocal.GHANA.currency
                IswLocal.USA.code -> IswLocal.USA.currency
                else -> ""
            }
        }
        var formattedAmount = getAmountString(amount.toInt())
        return "$currency $formattedAmount"
    }

    /**
     * Print receipt of the transaction*/
    fun getScreenBitMap(activity: Activity, view: ScrollView): Bitmap? {
        var rootview = view

        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        //  this get the width of the layout even beyond the visible screen
        var width = view.getChildAt(0).width

        // this will get the height of the layout even beyond the visible screen
        var height = view.getChildAt(0).height
        // Create a mutable bitmap

        // Create a mutable bitmap
        val secondScreen = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Created a canvas using the bitmap
        val c = Canvas(secondScreen)

        val bgDrawable: Drawable? = view.background
        if (bgDrawable != null) bgDrawable.draw(c) else c.drawColor(Color.WHITE)
        rootview.draw(c)
        return secondScreen
    }

    fun loadBitmapFromView(v: View): Bitmap? {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    fun maskPan(creditCardNumber: String): String? {
            val length = creditCardNumber.length
            val s = creditCardNumber.substring(6, length.toInt() - 4)
            return creditCardNumber.substring(0, 6) + s.replace("[A-Za-z0-9]".toRegex(), "*") +
                    creditCardNumber.substring(length.toInt() - 4)

    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }
}