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
import android.widget.Adapter
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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
import com.isw.iswkozen.core.database.entities.createTransactionResultFromCardLess
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentInfo
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentRequest
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentRequest.Companion.TRANSACTION_USSD
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentType
import com.isw.iswkozen.core.network.CardLess.request.CodeRequest
import com.isw.iswkozen.core.network.CardLess.response.Bank
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.network.CardLess.response.getTransactionStatus
import com.isw.iswkozen.core.network.models.fromResponseData
import com.isw.iswkozen.core.utilities.DisplayUtils.hide
import com.isw.iswkozen.core.utilities.DisplayUtils.show
import com.isw.iswkozen.core.utilities.Logger
import com.isw.iswkozen.databinding.FragmentUssdBinding
import com.isw.iswkozen.views.adapters.BankListAdapter
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class UssdFragment : Fragment(), AdapterView.OnItemSelectedListener  {

    val viewmodel: IswKozenViewModel by viewModel()

    private val logger by lazy { Logger.with("UssdFragment") }

    private lateinit var adapter: BankListAdapter
    private lateinit var ussdCode: String
    private lateinit var selectedBank: Bank
    private var selectedBankIndex: Int = -1

    private lateinit var terminalInfo: TerminalInfo
    private lateinit var txPaymentInfo: CardLessPaymentInfo

    // flag to prevent callback on initial setup of spinner
    private var justSetupSpinner = false

    lateinit var binding: FragmentUssdBinding

    private val arguements by navArgs<UssdFragmentArgs>()
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
         binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_ussd, container, false)

        val detailsSeperated = details.split(',')
        var amount = detailsSeperated[0]
        var transType = detailsSeperated[1]
        TRANSACTIONTYPE = transType
        AMOUNT = amount
        handleClick()
        getTerminalInfo()
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
                    paymentType = CardLessPaymentType.USSD
                )

                viewmodel.paymentStatus.observe(viewLifecycleOwner, {
                    val transaction = getTransactionStatus(it)
                    println("transaction: ${transaction?.amount}")
                    txPaymentInfo.reference = codeResponse?.transactionReference.toString()
                    txPaymentInfo.amount = txPaymentInfo.amount / 100
                    val transactionResultData = createTransactionResultFromCardLess(
                        paymentInfo = txPaymentInfo,
                        paymentStatus = transaction!!,
                        transactionName = "purchase",
                        transactionType = TransactionType.Ussd,
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
                    val direction =UssdFragmentDirections.actionUssdFragmentToReceiptFragment(
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

    private fun getTerminalInfo(){
        viewmodel.readterminalDetails()
        viewmodel.terminalInfo.observe(viewLifecycleOwner,  {
            it.let {
                terminalInfo = it
                loadBanks()
            }
        })
    }

    private fun loadBanks() {
        // set the default banks selection
        binding.etBanksSpinner.setText(getString(R.string.isw_select_bank))
        binding.loadingMessage.text = "Loading Banks"
        binding.loaderCard.show()

        // load banks from server
        viewmodel.loadUssdBanks()
        viewmodel.ussdBanks.observe(viewLifecycleOwner, {
            binding.loaderCard.hide()
            it?.let {
                when (it) {
                    is None -> {
                        Snackbar.make(binding.btnsContainer,
                            "Unable to load bank issuers, please try again.",
                            Snackbar.LENGTH_LONG).show()
                        this::loadBanks
                    }

                    is Some -> {
                        setBanks(issuers = it.value)
                    }
                }
            }
        })
    }


    private fun setBanks(issuers: List<Bank>) {
        // create adapter with banks
        adapter = BankListAdapter(issuers, this::getBankCode)

        // set flag
        justSetupSpinner = true

        // setup the hidden spinner
        // create banks adapter
        binding.banksSpinner.adapter = adapter
        // set default selection
        binding.banksSpinner.setSelection(Adapter.NO_SELECTION)

        // set item select listener
        binding.banksSpinner.onItemSelectedListener = this
        binding.banksSpinner.isClickable = true

        // set click listener for banks
        binding.etBanksSpinner.setOnClickListener {
            // trigger click on the hidden spinner
        binding.banksSpinner.performClick()
        }
    }

    private fun getBankCode(selectedBank: Bank) {
            println("amount is => $AMOUNT")
            // show loading indicator
            binding.loadingMessage.text = "Loading USSD Code..."
            binding.loaderCard.show()

        val paymentInfo = CardLessPaymentInfo(
            amount = AMOUNT.toInt(),
            Constants.getNextStan(),
            surcharge = 0,
            additionalAmounts = 0)

        txPaymentInfo = paymentInfo

            // create request for ussd
            val request = CardLessPaymentRequest.from(
                //iswPos.config.alias,
                terminalInfo,
                txPaymentInfo,
                TRANSACTION_USSD,
                bankCode = selectedBank.code
            )

            // get ussd code
            viewmodel.initiateUssd(request)
            viewmodel.ussdDetail.observe(viewLifecycleOwner, {
                binding.loaderCard.hide()
                it?.let {
                    when(it) {
                        is Some -> {
                            codeResponse = it.value
                            showUssdButtons()
                            handleResponse(it.value)
                        }

                        is None -> handleError()
                    }
                }
            })

    }


    private fun handleResponse(response: CodeResponse) {
        when (response.responseCode) {
            CodeResponse.OK -> {

                ussdCode = response.bankShortCode ?: response.defaultShortCode!!
                ussdCode.apply {
                    binding.tvUssd.text = this
                    val code = substring(lastIndexOf("*") + 1 until lastIndexOf("#"))

                    // get the entire hint as spannable string
                    val hint = getString(R.string.isw_hint_enter_ussd_code, code)
                    val spannableHint = SpannableString(hint)


                    // increase font size and change font color
                    val startIndex = 16
                    val endIndex = startIndex + code.length + 1
                    val codeColor = ContextCompat.getColor(requireContext(), R.color.iswColorPrimary)
                    spannableHint.setSpan(AbsoluteSizeSpan(21, true), startIndex, endIndex, 0)
                    spannableHint.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
                    spannableHint.setSpan(
                        ForegroundColorSpan(codeColor),
                        startIndex,
                        endIndex,
                        0
                    )


                    // set the text value
                    binding.tvUssdHint.text = spannableHint
                    binding.tvUssdHint.visibility = View.VISIBLE
                }


            }
            else -> {
                val errorMessage = "An error occurred: ${response.responseDescription}"
                logger.log(errorMessage)
                handleError()
            }
        }
    }

    private fun handleError() {
        // show warning for user to try re-generating code
        Snackbar.make(binding.btnsContainer,
            "Unable to generate bank code",
            Snackbar.LENGTH_LONG).show()
            // retry getting ussd code
            getBankCode(selectedBank)


    }

    companion object {

        @JvmStatic
        fun newInstance() = UssdFragment()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, itemPosition: Int, p3: Long) {
        // get bank at adapter
        // check selected bank has changed
        if (selectedBankIndex == itemPosition) return
        else selectedBankIndex = itemPosition


        // set the name of selected bank
        binding.etBanksSpinner.setText(adapter.getTitle(itemPosition))

        // prevent selection of default option
        if (itemPosition == 0) hideUssdButtons()
        else {
            selectedBank = adapter.getItem(itemPosition - 1)
            // generate bank code
            getBankCode(selectedBank)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        println("Nothing selected")
    }

    private fun hideUssdButtons() {
        arrayOf(binding.btnsContainer, binding.ussdContainer, binding.tvUssdHint).forEach {
            it.visibility = View.GONE
        }
    }

    private fun showUssdButtons() {
        arrayOf(binding.btnsContainer, binding.ussdContainer, binding.tvUssdHint).forEach {
            it.visibility = View.VISIBLE
        }
    }
}