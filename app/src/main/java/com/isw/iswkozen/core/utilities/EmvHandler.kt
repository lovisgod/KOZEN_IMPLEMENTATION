package com.isw.iswkozen.core.utilities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.interswitchng.smartpos.shared.utilities.console
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.models.EmvCard
import com.isw.iswkozen.core.data.models.EmvPinData
import com.isw.iswkozen.core.data.models.TransactionData
import com.isw.iswkozen.core.data.utilsData.*
import com.isw.iswkozen.core.data.utilsData.getIccData
import com.isw.iswkozen.core.utilities.EmvUtils.bcd2Str
import com.isw.iswkozen.core.utilities.views.PasswordDialog
import com.pos.sdk.emvcore.IPosEmvCoreListener
import com.pos.sdk.emvcore.POIEmvCoreManager
import com.pos.sdk.emvcore.PosEmvErrorCode
import com.pos.sdk.security.POIHsmManage
import kotlinx.coroutines.delay
import java.lang.Exception




class EmvHandler {

    // communication channel with cardreader
//    private val channel = Channel<EmvMessage>()

    var emvCoreManager: POIEmvCoreManager? = null
    var emvCoreListener: POIEmvCoreListener? = null
    var mode:Int = 0
    var deviceDetectedMessage: String?  = ""
    var transData : TransactionData?  = TransactionData()
    var iccData: RequestIccData?  = RequestIccData()
    var cardType: Int = 0
    var context: Context? = null
    var activity: Activity? = null
    var emvEvents: EMVEvents ? = null
    var pinMode :Int ?  = POIHsmManage.PED_PINBLOCK_FETCH_MODE_DUKPT
    var pinKey: Int? = KeysUtils.DUKPTKEY_INDEX


    fun setEmvContext(context: Context) {
        this.context = context
    }

    fun setEmvPINMODE(pinMode: Int) {
        this.pinMode = pinMode

        if (pinMode == POIHsmManage.PED_PINBLOCK_FETCH_MODE_DUKPT) {
            this.pinKey = KeysUtils.DUKPTKEY_INDEX
        } else {
            this.pinKey = KeysUtils.PINKEY_INDEX
        }
    }

     fun startTransaction(
        hasContactless: Boolean = true,
        hasContact: Boolean = true,
        amount: Long,
        amountOther: Long,
        transType: Int,
        emvEvents: EMVEvents
    ) {

        try {
            // clear previous pinblock and ksn data

            Constants.memoryPinData.pinBlock = ""
            Constants.memoryPinData.ksn = ""
            this.emvEvents = emvEvents
            this.emvEvents!!.onInsertCard()

            emvCoreManager = POIEmvCoreManager.getDefault()
            emvCoreListener = POIEmvCoreListener()

            println("checkRead: it got here for read card startttt")
            val bundle = Bundle()
            bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TYPE, transType)
            bundle.putLong(POIEmvCoreManager.EmvTransDataConstraints.TRANS_AMOUNT, amount)
            bundle.putLong(
                POIEmvCoreManager.EmvTransDataConstraints.TRANS_AMOUNT_OTHER,
                amountOther
            )

                println("checkRead: it got here for read card non fallback")
                var mode = 0
                if (hasContact) {
                    mode = mode or POIEmvCoreManager.DEVICE_CONTACT
                }
                if (hasContactless) {
                    mode = mode or POIEmvCoreManager.DEVICE_CONTACTLESS
                }

                bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_MODE, mode)

            bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TIMEOUT, 60)
            bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_CONTACT, false)
            bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_MAGSTRIPE, false)
            transData?.setTransType(transType)
            transData?.setTransAmount(amount.toDouble())
            transData?.setTransAmountOther(amountOther.toDouble())
            transData?.setTransResult(PosEmvErrorCode.EMV_OTHER_ERROR)

            // check if it gets here
            println("checkRead: it got here for read card")
            val result = emvCoreManager!!.startTransaction(bundle, emvCoreListener)
            println("checkRead: it got here for read card result $result")
            if (PosEmvErrorCode.EXCEPTION_ERROR == result) {
                transData?.setTransResult(PosEmvErrorCode.EXCEPTION_ERROR)
            } else if (PosEmvErrorCode.EMV_ENCRYPT_ERROR == result) {
                Log.e("EMV_ERROR", "startTransaction encrypt error")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }






    // POI LISTENER
    inner class POIEmvCoreListener : IPosEmvCoreListener.Stub() {
        private val TAG = "PosEmvCoreListener"
        override fun onEmvProcess(type: Int, bundle: Bundle?) {
                cardType = type
                when (type) {
                    POIEmvCoreManager.DEVICE_CONTACT -> {
                        console.log("card transaction type","Contact Card Trans")
                        this@EmvHandler.emvEvents?.onEmvProcessing(message = "Contact Card Trans")
                    }
                    POIEmvCoreManager.DEVICE_CONTACTLESS -> {
                        console.log("card transaction type", "Contactless Card Trans")
                    }
                    POIEmvCoreManager.DEVICE_MAGSTRIPE -> { console.log("card transaction type","Magstripe Card Trans") }
                    else -> {
                    }
                }

        }

        override fun onSelectApplication(list: List<String>, isFirstSelect: Boolean) {
            console.log("", "called application selection")
                val names = list.toTypedArray()
                emvCoreManager?.onSetSelectResponse(1)
//                val dialog = MaterialDialog(this@TransActivity)
//                dialog.showListConfirmChoseDialog("Select Application", names,
//                    object : OnChoseListener() {
//                        fun onChose(position: Int) {
//                            emvCoreManager.onSetSelectResponse(position)
//                        }
//                    })

        }

        override fun onConfirmCardInfo(mode: Int, bundle: Bundle?) {
            val outBundle = Bundle()
            if (mode == POIEmvCoreManager.CMD_AMOUNT_CONFIG) {
                outBundle.putString(POIEmvCoreManager.EmvCardInfoConstraints.OUT_AMOUNT, "11")
                outBundle.putString(POIEmvCoreManager.EmvCardInfoConstraints.OUT_AMOUNT_OTHER, "22")
            } else if (mode == POIEmvCoreManager.CMD_TRY_OTHER_APPLICATION) {
                outBundle.putBoolean(POIEmvCoreManager.EmvCardInfoConstraints.OUT_CONFIRM, true)
            } else if (mode == POIEmvCoreManager.CMD_ISSUER_REFERRAL) {
                outBundle.putBoolean(POIEmvCoreManager.EmvCardInfoConstraints.OUT_CONFIRM, true)
            }
            emvCoreManager?.onSetCardInfoResponse(outBundle)
        }

        override fun onKernelType(type: Int) {
            transData?.setCardType(type)
        }

        override fun onSecondTapCard() {
            console.log("","second tap needed")
        }

        override fun onRequestInputPin(bundle: Bundle?) {
            AppExecutors.getInstance().mainThread().execute {
                this@EmvHandler.emvEvents?.onPinInput()
                val isIcSlot = cardType == POIEmvCoreManager.DEVICE_CONTACT
                val dialog =
                    PasswordDialog( this@EmvHandler.context, isIcSlot,
                        bundle, pinKey!!, pinMode!!)
                dialog.showDialog()
            }

        }

        override fun onRequestOnlineProcess(bundle: Bundle) {
           console.log("", "  Authorizing,Please Wait  ")
                var data: ByteArray?
                val vasResult: Int
                val vasData: ByteArray?
                val vasMerchant: ByteArray?
                val encryptResult: Int
                val encryptData: ByteArray?
//                vasResult = bundle.getInt(
//                    POIEmvCoreManager.EmvOnlineConstraints.APPLE_RESULT,
//                    PosEmvErrorCode.APPLE_VAS_UNTREATED
//                )
                encryptResult = bundle.getInt(
                    POIEmvCoreManager.EmvOnlineConstraints.ENCRYPT_RESULT,
                    PosEmvErrorCode.EMV_UNENCRYPTED
                )
//                if (vasResult != -1) {
//                    Log.d(TAG, "VAS Result : $vasResult")
//                }
                if (encryptResult != -1) {
                    Log.d(TAG, "Encrypt Result : $encryptResult")
                }
//                vasData = bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.APPLE_DATA)
//                if (vasData != null) {
//                    Log.d(TAG, "VAS Data : " + HexUtil.toHexString(vasData))
//                }
//                vasMerchant =
//                    bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.APPLE_MERCHANT)
//                if (vasMerchant != null) {
//                    Log.d(TAG, "VAS Merchant : " + HexUtil.toHexString(vasMerchant))
//                }
                data = bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.EMV_DATA)
                if (data != null) {
                    Log.d(TAG, "Trans Data : " + HexUtil.toHexString(data))
                    iccData?.iccAsString = HexUtil.toHexString(data)
                }
                encryptData =
                    bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.ENCRYPT_DATA)
                if (encryptData != null) {
                    Log.d(TAG, "Encrypt Data : " + HexUtil.toHexString(encryptData))
                    if (!HexUtil.toHexString(encryptData).isNullOrEmpty()) {
                        iccData?.iccAsString = HexUtil.toHexString(data)

                    }
                }
                if (data != null) {
                    val tlvBuilder = BerTlvBuilder()
                    val tlvParser = BerTlvParser()
                    val tlvs = tlvParser.parse(data)

                    for (tag in REQUEST_TAGS) {
                        if (tlvs.find(BerTag(tag.tag)) != null) {
                            println("not null here ::::")
                            tlvBuilder.addBerTlv(tlvs.find(BerTag(tag.tag))!!)
                        }
                    }
//                    for (tlv: BerTlv? in tlvs.list) {
//                        tlvBuilder.addBerTlv(tlv!!)
//                    }
                    if (encryptResult == PosEmvErrorCode.EMV_OK && encryptData != null) {
                        val encryptTlvs = BerTlvParser().parse(encryptData)

                        for (tag in REQUEST_TAGS) {
                            if (encryptTlvs.find(BerTag(tag.tag)) != null) {
                                println("not null here ::::")
                                tlvBuilder.addBerTlv(encryptTlvs.find(BerTag(tag.tag))!!)
                            }
                        }
//                        for (tlv: BerTlv? in encryptTlvs.list) {
//                            tlvBuilder.addBerTlv(tlv!!)
//                        }
                    }
                    data = tlvBuilder.buildArray()
                }
                if (data != null) {
                    iccString = HexUtil.toHexString(data)
                    Log.d(TAG, "Trans Data ccccccc: " + iccString)
                    transData?.setTransData(data)
                    iccString = HexUtil.toHexString(data)

                }
//                if (vasResult != PosEmvErrorCode.APPLE_VAS_UNTREATED) {
//                    val tlvBuilder = BerTlvBuilder()
//                    if (vasData != null) {
//                        val tlvs = BerTlvParser().parse(vasData)
//                        for (tlv: BerTlv? in tlvs.list) {
//                            tlvBuilder.addBerTlv(tlv!!)
//                        }
//                    }
//                    if (vasMerchant != null) {
//                        val tlvs = BerTlvParser().parse(vasMerchant)
//                        for (tlv: BerTlv? in tlvs.list) {
//                            tlvBuilder.addBerTlv(tlv!!)
//                        }
//                    }
//                    transData?.setAppleVasResult(vasResult)
//                    transData?.setAppleVasData(tlvBuilder.buildArray())
//                }
                val outBundle = Bundle()
                outBundle.putInt(
                    POIEmvCoreManager.EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                    0
                )
                emvCoreManager?.onSetOnlineResponse(outBundle)
        }

        override fun onTransactionResult(result: Int, bundle: Bundle) {
            Log.d(TAG, "onTransactionResult $result")
            when (result) {
                PosEmvErrorCode.EMV_CANCEL, PosEmvErrorCode.EMV_TIMEOUT -> {
//                    onTransEnd()
                    println("transaction timed out")
                    return
                }
                else -> {
                }
            }
                var data: ByteArray?
                val vasResult: Int
                val vasData: ByteArray?
                val vasMerchant: ByteArray?
                val encryptResult: Int
                val encryptData: ByteArray?
                val scriptResult: ByteArray?
//                vasResult = bundle.getInt(
//                    POIEmvCoreManager.EmvResultConstraints.APPLE_RESULT,
//                    PosEmvErrorCode.APPLE_VAS_UNTREATED
//                )
                encryptResult = bundle.getInt(
                    POIEmvCoreManager.EmvResultConstraints.ENCRYPT_RESULT,
                    PosEmvErrorCode.EMV_UNENCRYPTED
                )
//                if (vasResult != -1) {
//                    Log.d(TAG, "VAS Result : $vasResult")
//                }
                if (encryptResult != -1) {
                    Log.d(TAG, "Encrypt Result : $encryptResult")
                }
//                vasData = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.APPLE_DATA)
//                if (vasData != null) {
//                    Log.d(TAG, "VAS Data : " + HexUtil.toHexString(vasData))
//                }
//                vasMerchant =
//                    bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.APPLE_MERCHANT)
//                if (vasMerchant != null) {
//                    Log.d(TAG, "VAS Merchant : " + HexUtil.toHexString(vasMerchant))
//                }
                data = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.EMV_DATA)
                if (data != null) {
                    Log.d(TAG, "Trans Data : " + HexUtil.toHexString(data))
                    iccData?.iccAsString = HexUtil.toHexString(data)
                }
                encryptData =
                    bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.ENCRYPT_DATA)
                if (encryptData != null) {
                    Log.d(TAG, "Encrypt Data : " + HexUtil.toHexString(encryptData))
                    if (!HexUtil.toHexString(encryptData).isNullOrEmpty()) {
                        iccData?.iccAsString = HexUtil.toHexString(data)
                    }
                }
                scriptResult =
                    bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.SCRIPT_RESULT)
                if (scriptResult != null) {
                    Log.d(TAG, "Script Result : " + HexUtil.toHexString(scriptResult))
                }
                if (data != null) {
                    val tlvBuilder = BerTlvBuilder()
                    val tlvParser = BerTlvParser()
                    val tlvs = tlvParser.parse(data)
                    for (tag in REQUEST_TAGS) {
                        if (tlvs.find(BerTag(tag.tag)) != null) {
                            println("not null here ::::")
                            tlvBuilder.addBerTlv(tlvs.find(BerTag(tag.tag))!!)
                        }
                    }
                    for (tlv: BerTlv in tlvs.list) {
//                        tlvBuilder.addBerTlv(tlv)
                        if (tlv.isConstructed()!!) {
                            Log.d(
                                TAG,
                                java.lang.String.format(
                                    "Tag : %1$-4s  >>",
                                    tlv.getTag()?.berTagHex
                                )
                            )
                            for (value: BerTlv in tlv.getValues()!!) {
                                Log.d(
                                    TAG,
                                    java.lang.String.format(
                                        "Tag : %1$-4s",
                                        value.getTag()?.berTagHex
                                    ).toString() +
                                            " Value : " + value.getHexValue()
                                )
                            }
                            Log.d(
                                TAG,
                                java.lang.String.format(
                                    "Tag : %1$-4s  <<",
                                    tlv.getTag()?.berTagHex
                                )
                            )
                        } else {
                            Log.d(
                                TAG,
                                java.lang.String.format("Tag : %1$-4s", tlv.getTag()?.berTagHex)
                                    .toString() + " Value : " + tlv.getHexValue()
                            )
                        }
                    }
                    if (encryptResult == PosEmvErrorCode.EMV_OK && encryptData != null) {
                        val encryptTlvs = BerTlvParser().parse(encryptData)


                        for (tag in REQUEST_TAGS) {
                            if (encryptTlvs.find(BerTag(tag.tag)) != null) {
                                println("not null here ::::")
                                tlvBuilder.addBerTlv(encryptTlvs.find(BerTag(tag.tag))!!)
                            }
                        }
                        for (tlv: BerTlv in encryptTlvs.list) {
//                            tlvBuilder.addBerTlv(tlv)
                            Log.d(
                                TAG,
                                java.lang.String.format("Tag : %1$-4s", tlv.getTag()?.berTagHex)
                                    .toString() + " Value : " + tlv.getHexValue()
                            )
                        }
                    }
                    data = tlvBuilder.buildArray()
                }
                when (result) {
                    PosEmvErrorCode.EMV_MULTI_CONTACTLESS -> {
                        console.log("result","multi contactless")
                    }
                    PosEmvErrorCode.EMV_FALLBACK -> {

                        console.log("result", "Please Magnetic Stripe")
                        console.log("result", "FallBack")
                    }
                    PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE -> {
                        console.log("result", "Please Insert Card")
                    }
                    PosEmvErrorCode.EMV_APP_EMPTY -> {
                        console.log("result","Please Magnetic Stripe")
                        console.log("result","AID Empty")
                    }
                    PosEmvErrorCode.EMV_SEE_PHONE, PosEmvErrorCode.APPLE_VAS_WAITING_INTERVENTION, PosEmvErrorCode.APPLE_VAS_WAITING_ACTIVATION -> {
                        console.log("result","Please See Phone")
                    }
                    else -> {
                    }
                }
//                if (vasResult != PosEmvErrorCode.APPLE_VAS_UNTREATED) {
//                    val tlvBuilder = BerTlvBuilder()
//                    if (vasData != null) {
//                        val tlvs = BerTlvParser().parse(vasData)
//                        for (tlv: BerTlv? in tlvs.getList()) {
//                            tlvBuilder.addBerTlv((tlv)!!)
//                        }
//                    }
//                    if (vasMerchant != null) {
//                        val tlvs = BerTlvParser().parse(vasMerchant)
//                        for (tlv: BerTlv? in tlvs.getList()) {
//                            tlvBuilder.addBerTlv((tlv)!!)
//                        }
//                    }
//                    transData?.setAppleVasResult(vasResult)
//                    transData.setAppleVasData(tlvBuilder.buildArray())
//                }
            if (data != null) {
                transData?.setTransData(data)
                iccString = HexUtil.toHexString(data)
                Log.d(TAG, "Trans Data ccccccc:::::::: " + iccString)
            }
                transData?.setTransResult(result)
                if (data != null) {
                    val emvCard = EmvCard(data)
                    iccData = transData?.getTransData()?.let { getIccData(it) }
                    if (emvCard.getCardNumber() != null) {
//                        transRepository.createTransaction(transData)
                      iccData?.EMC_CARD_ = emvCard
                    }

                    if (!Constants.memoryPinData.pinBlock.isNullOrEmpty()) {
                        iccData?.EMV_CARD_PIN_DATA = EmvPinData(
                            CardPinBlock = Constants.memoryPinData.pinBlock,
                            ksn = Constants.memoryPinData.ksn
                        )

                    } else {
                        iccData?.EMV_CARD_PIN_DATA = EmvPinData(
                            CardPinBlock = "",
                            ksn = ""
                        )
                    }
                    iccData?.haspin = !iccData?.EMV_CARD_PIN_DATA?.CardPinBlock.isNullOrEmpty()

                    val isIcSlot = cardType == POIEmvCoreManager.DEVICE_CONTACT

                    if(isIcSlot) {
                        Constants.POS_ENTRY_MODE = "051"
                        if (iccData?.haspin!!) {
                            Constants.POS_DATA_CODE = Constants.CONTACT_POS_DATA_CODE_PIN
                        } else {
                            Constants.POS_DATA_CODE = Constants.CONTACT_POS_DATA_CODE_PIN
                        }
                    } else {
                        Constants.POS_ENTRY_MODE = "071"
                        Constants.POS_DATA_CODE = Constants.CLSS_POS_DATA_CODE
                    }

                    println(
                        "iccData => {" +
                                "date: ${iccData?.TRANSACTION_DATE} " +
                                "name: ${iccData?.CARD_HOLDER_NAME} " +
                                "amount: ${iccData?.TRANSACTION_AMOUNT} " +
                                "Track2Data ${iccData?.TRACK_2_DATA}" +
                                "haspin ${iccData?.haspin}" +
                                "iccString ${iccData?.iccAsString}" +
                        "}"
                    )

//                    for (tag in REQUEST_TAGS) {
//                        println("tag value ::::::::::::::: $tag")
//                        val tlv = tag.getTlvBytes(data)
//                        tagValues.add(Pair(tag, tlv))
//                    }
//
//                    iccString = buildIccString(tagValues)

                    println("iccccc =>->=>>>>>>>>>>>>>>> ${iccString}")

                    iccData?.let {
                        this@EmvHandler.emvEvents?.onEmvProcessed(it)
//                        Constants.memoryPinData.ksn = ""
//                        Constants.memoryPinData.pinBlock = ""
                    }
                }
//                else if (vasData != null) {
//                    if (vasResult == PosEmvErrorCode.APPLE_VAS_APPROVED) {
////                        transRepository.createTransaction(transData)
//                    }
//                }
//                Handler(Looper.getMainLooper()).postDelayed({
//                    TransDetailActivity.startActivity(this@TransActivity, transData)
//                    finish()
//                }, 1000)
        }
    }


    companion object {
        var iccString = ""

        val tagValues: MutableList<Pair<ICCData, ByteArray?>> = mutableListOf()


        @JvmStatic
         fun buildIccString(tagValues: List<Pair<ICCData, ByteArray?>>): String {
            var hex = ""
            println("getting here")
            for (tagValue in tagValues) {
                println("tag valuex ::::::::::::::: ${tagValue.first.tagName}")
                tagValue.second?.apply {
                    println("getting here here here")
                    val tag = Integer.toHexString(tagValue.first.tag.toInt()).toUpperCase()
                    var value = bcd2Str(this)
                    var length = Integer.toHexString(size)

                    // truncate tag value if it exceeds max length
                    if (size > tagValue.first.max) {
                        val expectedLength = 2 * tagValue.first.max // hex is double the length
                        value = value.substring(0 until expectedLength)
                        length = Integer.toHexString(tagValue.first.max)
                    }

                    // prepend 0 based on value length
                    val lengthStr = (if (length.length > 1) length else "0$length").toUpperCase()
                    hex = "$hex$tag$lengthStr$value"
                }
            }

            return hex
        }
    }


}




