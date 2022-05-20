package com.isw.iswkozen.views.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.google.android.material.snackbar.Snackbar
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.TransactionType
import com.isw.iswkozen.core.database.entities.TransactionResultData
import com.isw.iswkozen.core.database.entities.createTransactionResultFromCardLess
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentInfo
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentRequest
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentType
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.network.CardLess.response.PaymentStatus
import com.isw.iswkozen.core.network.CardLess.response.Transaction
import com.isw.iswkozen.core.network.CardLess.response.getTransactionStatus
import com.isw.iswkozen.core.network.models.PurchaseResponse
import com.isw.iswkozen.core.network.models.fromResponseData
import com.isw.iswkozen.core.utilities.DisplayUtils
import com.isw.iswkozen.core.utilities.DisplayUtils.hide
import com.isw.iswkozen.core.utilities.DisplayUtils.show
import com.isw.iswkozen.databinding.FragmentTransferBinding
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class TransferFragment : Fragment() {

    private lateinit var binding: FragmentTransferBinding
    val viewmodel: IswKozenViewModel by viewModel()
    private lateinit var terminalInfo: TerminalInfo
    private lateinit var txPaymentInfo: CardLessPaymentInfo
    private val arguements by navArgs<TransferFragmentArgs>()
    private val details by lazy { arguements.transactionDetails }
    var TRANSACTIONTYPE = ""
    var AMOUNT = ""
    var codeResponse: CodeResponse? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_transfer, container, false)
        getTerminalInfo()
        handleClick()
        return binding.root
    }

    private fun handleClick() {
        binding.backBtn.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnConfirmPayment.setOnClickListener {
            binding.loader.show()
            if (codeResponse != null) {
                viewmodel.checkPaymentStatus(
                    codeResponse?.transactionReference.toString(),
                    terminalInfo.qtbMerchantCode.toString(),
                    paymentType = CardLessPaymentType.Transfer
                )

                viewmodel.paymentStatus.observe(viewLifecycleOwner, Observer {
                  val transaction = getTransactionStatus(it)
                  println("transaction: ${transaction?.amount}")
                  txPaymentInfo.reference = codeResponse?.transactionReference.toString()
                  txPaymentInfo.amount = txPaymentInfo.amount / 100
                  val transactionResultData = createTransactionResultFromCardLess(
                          paymentInfo = txPaymentInfo,
                          paymentStatus = transaction!!,
                          transactionName = "purchase",
                          transactionType = TransactionType.Transfer,
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
                    val direction =TransferFragmentDirections.actionTransferFragmentToReceiptFragment(
                        purchaseResponse,
                        RequestIccData(TRANSACTION_AMOUNT = AMOUNT),
                        TRANSACTIONTYPE
                    )
                    findNavController().navigate(direction)
                })
            } else {
                binding.loader.hide()
            }
        }
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
                    CardLessPaymentRequest.TRANSACTION_TRANSFER
                )
                viewmodel.initiateTransfer(req)
                observeTransferDetails()
            }

            "CASHOUT" -> {
                Toast.makeText(this.requireContext(), "Transaction Type Not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeTransferDetails() {
        viewmodel.virtualAccount.observe(viewLifecycleOwner, Observer {
            binding.loaderCard.hide()
            it.let {
               when(it){
                   is None -> {
                       Snackbar.make(binding.tvAccountNumber,
                           "Merchant account could not be fetched",
                           Snackbar.LENGTH_LONG).show()
                   }

                   is Some -> {
                       if (!it.value.accountName.isNullOrEmpty()) {
                           codeResponse = it.value
                           binding.detailsContainer.show()
                           binding.btnConfirmPayment.show()
                           binding.tvAccountNumber.text = it?.value.accountNumber
                           binding.tvBeneficiaryName.text = it?.value.accountName
                           binding.loader.hide()
                           binding.tvBankName.text = it?.value.bankName


                           // set title amount
                           val amountString = getString(R.string.isw_title_amount, (AMOUNT.toInt() / 100).toString())
                           val pinHintString = getString(R.string.isw_title_transfer_amount, amountString)
                           val spannableHint = SpannableString(pinHintString)


                           // increase font size and change font color of amount
                           val startIndex = 24
                           val endIndex = startIndex + amountString.length + 1
                           val codeColor = ContextCompat.getColor(requireContext(), R.color.purple_700)
                           spannableHint.setSpan(AbsoluteSizeSpan(18, true), startIndex, endIndex, 0)
                           spannableHint.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
                           spannableHint.setSpan(
                               ForegroundColorSpan(codeColor),
                               startIndex,
                               endIndex,
                               0
                           )

                           // set the hint for pin
                           binding.tvTransferHint.show()
                           binding.tvTransferHint.text = spannableHint
                       }
                   }
               }
            }
        })
    }


    private fun getTerminalInfo(){
        viewmodel.readterminalDetails()
        viewmodel.terminalInfo.observe(viewLifecycleOwner, Observer {
            it.let {
                terminalInfo = it
                startTheTransaction()
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() = TransferFragment()
    }
}