package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.google.android.material.snackbar.Snackbar
import com.interswitchng.smartpos.shared.utilities.console
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.TransactionType
import com.isw.iswkozen.core.database.entities.createTransactionResultFromCardLess
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentInfo
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentRequest
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentRequest.Companion.QR_FORMAT_RAW
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentType
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.network.CardLess.response.getTransactionStatus
import com.isw.iswkozen.core.network.models.fromResponseData
import com.isw.iswkozen.core.utilities.DisplayUtils.hide
import com.isw.iswkozen.core.utilities.DisplayUtils.show
import com.isw.iswkozen.core.utilities.Logger
import com.isw.iswkozen.databinding.FragmentPayCodeBinding
import com.isw.iswkozen.databinding.FragmentQrcodeBinding
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class QrcodeFragment : Fragment() {

    val viewmodel: IswKozenViewModel by viewModel()

    private val logger by lazy { Logger.with("QRCodeFragment") }

    lateinit var binding: FragmentQrcodeBinding
    private val arguements by navArgs<QrcodeFragmentArgs>()
    private val details by lazy { arguements.transactionDetails }
    var TRANSACTIONTYPE = ""
    var AMOUNT = ""
    var codeResponse: CodeResponse? =  null
    private lateinit var terminalInfo: TerminalInfo
    private lateinit var txPaymentInfo: CardLessPaymentInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_qrcode, container, false)

        val detailsSeperated = details.split(',')
        var amount = detailsSeperated[0]
        var transType = detailsSeperated[1]
        TRANSACTIONTYPE = transType
        AMOUNT = amount
        getTerminalInfo()
        handleClick()
//        manageData()
        return binding.root
    }




    private fun handleClick() {
        binding.btnConfirmPayment.setOnClickListener {
            binding.loaderCard.show()
            var code  = codeResponse?.transactionReference.toString()
            if (code.isNullOrEmpty()) {
                Toast.makeText(this.requireContext(), "QR reference is empty", Toast.LENGTH_LONG).show()
            } else {
                viewmodel.checkPaymentStatus(
                    codeResponse?.transactionReference.toString(),
                    terminalInfo.qtbMerchantCode.toString(),
                    paymentType = CardLessPaymentType.QR
                )
            }


                viewmodel.paymentStatus.observe(viewLifecycleOwner, Observer {
                    val transaction = getTransactionStatus(it)
                    println("transaction: ${transaction?.amount}")
                    txPaymentInfo.reference = codeResponse?.transactionReference.toString()
                    txPaymentInfo.amount = txPaymentInfo.amount / 100
                    val transactionResultData = createTransactionResultFromCardLess(
                        paymentInfo = txPaymentInfo,
                        paymentStatus = transaction!!,
                        transactionName = "purchase",
                        transactionType = TransactionType.QR,
                        terminalData = terminalInfo
                    )


                    println(transactionResultData)

                    // save transaction
                    if (transactionResultData != null) {
                        viewmodel.saveTransaction(transactionResultData)
                    }
                    // create an empty iccdata for the receipt page
                    // create purchase response object for the receipt page

                    val purchaseResponse = transactionResultData.let { response ->
                        fromResponseData(response)
                    }
                    val direction =QrcodeFragmentDirections.actionQrcodeFragmentToReceiptFragment(
                        purchaseResponse,
                        RequestIccData(TRANSACTION_AMOUNT = AMOUNT),
                        TRANSACTIONTYPE, "processing"
                    )
                    findNavController().navigate(direction)
                })

        }

        binding.backBtn.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun manageData() {

        viewmodel.qrDetails.observe(viewLifecycleOwner, {
            binding.loaderCard.hide()
            it?.let {
                when (it) {
                    is None -> {
                        Snackbar.make(binding.btnsContainer,
                            "Unable to load QR Code, please try again.",
                            Snackbar.LENGTH_LONG).show()
                    }

                    is Some -> {
                        codeResponse = it.value
                        it.value.setBitmap(this.requireContext())
                        binding.tvpaycode.setImageBitmap(it.value.qrCodeImage)
                    }
                }
            }
        })
    }

    private fun getTerminalInfo(){
        viewmodel.readterminalDetails()
        viewmodel.terminalInfo.observe(viewLifecycleOwner, Observer {
            it.let {
                terminalInfo = it!!
                startTheTransaction()
            }
        })
    }


    private fun startTheTransaction() {
        binding.loadingMessage.text = "Please wait while we load merchant account details"
        binding.loaderCard.show()
        val detailsSeperated = details.split(',')
        var amount = detailsSeperated[0]
        var transType = detailsSeperated[1]
        TRANSACTIONTYPE = transType
        AMOUNT = amount
        when (transType) {
            "PURCHASE" -> {
                val paymentInfo = CardLessPaymentInfo(
                    amount = AMOUNT.toInt(),
                    Constants.getNextStan(),
                    surcharge = 0,
                    additionalAmounts = 0)
                txPaymentInfo = paymentInfo
                val req = CardLessPaymentRequest.from(
                    terminalInfo,
                    paymentInfo,
                    CardLessPaymentRequest.TRANSACTION_QR,
                    qrFormat = QR_FORMAT_RAW
                )
                viewmodel.initiateQr(req)
                manageData()
            }

            "CASHOUT" -> {
                Toast.makeText(this.requireContext(), "Transaction Type Not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = QrcodeFragment()
    }
}