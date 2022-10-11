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
import com.interswitchng.smartpos.shared.utilities.console
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.network.CardLess.CardLessPaymentInfo
import com.isw.iswkozen.core.network.CardLess.response.Bank
import com.isw.iswkozen.core.network.CardLess.response.CodeResponse
import com.isw.iswkozen.core.utilities.DisplayUtils.hide
import com.isw.iswkozen.core.utilities.DisplayUtils.show
import com.isw.iswkozen.core.utilities.Logger
import com.isw.iswkozen.databinding.FragmentPayCodeBinding
import com.isw.iswkozen.databinding.FragmentUssdBinding
import com.isw.iswkozen.views.adapters.BankListAdapter
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class PayCodeFragment : Fragment() {

    val viewmodel: IswKozenViewModel by viewModel()

    private val logger by lazy { Logger.with("PayCodeFragment") }

    lateinit var binding: FragmentPayCodeBinding
    private val arguements by navArgs<PayCodeFragmentArgs>()
    private val details by lazy { arguements.transactionDetails }
    var TRANSACTIONTYPE = ""
    var AMOUNT = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_pay_code, container, false)

        val detailsSeperated = details.split(',')
        var amount = detailsSeperated[0]
        var transType = detailsSeperated[1]
        TRANSACTIONTYPE = transType
        AMOUNT = amount
        manageData()
        handleClick()
        return binding.root
    }

    private fun handleClick() {
        binding.btnConfirmPayment.setOnClickListener {
            binding.loaderCard.show()
            var code  = binding.tvpaycode.text.toString()
            if (code.isNullOrEmpty()) {
                Toast.makeText(this.requireContext(), "Kindly input a PAYCODE", Toast.LENGTH_LONG).show()
            } else {
                viewmodel.makePayCodeRequest(
                    amount = AMOUNT,
                    transactionName = TRANSACTIONTYPE,
                    code = code
                )
            }
        }

        binding.backBtn.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun manageData() {
        viewmodel.transactionResponse.observe(viewLifecycleOwner, Observer{
            binding.loaderCard.hide()
            it.let {
                console.log("response data", "${it.responseMessage}  ${it.responseCode}")
                val direction = PayCodeFragmentDirections.actionPayCodeFragmentToReceiptFragment(
                    it,
                    RequestIccData(TRANSACTION_AMOUNT = AMOUNT),
                    TRANSACTIONTYPE, "processing")
                findNavController().navigate(direction)
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() = PayCodeFragment()
    }
}