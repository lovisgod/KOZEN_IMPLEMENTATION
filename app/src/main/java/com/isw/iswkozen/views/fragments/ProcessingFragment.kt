package com.isw.iswkozen.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.databinding.FragmentProcessingBinding
import com.isw.iswkozen.views.utilViews.Animator.Companion.changeText
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class ProcessingFragment : Fragment(), EMVEvents {

    private lateinit var binding: FragmentProcessingBinding
    val viewmodel: IswKozenViewModel by viewModel()
    private val arguements by navArgs<ProcessingFragmentArgs>()
    private val details by lazy { arguements.transactiondetails }
    var TRANSACTIONTYPE = ""
    lateinit var iccData: RequestIccData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_processing, container, false)

        startTheTransaction()
        manageData()
        return binding.root
    }

    private fun manageData() {
        viewmodel.transactionResponse.observe(viewLifecycleOwner, Observer{
            println("got here")
            println(it.description)
            it.let {
                println("response data => ${it.responseMessage}  ${it.responseCode}")
                val direction = ProcessingFragmentDirections.actionProcessingFragmentToReceiptFragment(it,
                    iccData, TRANSACTIONTYPE, "processing")
                findNavController().navigate(direction)
            }
        })
    }

    private fun startTheTransaction() {
        val detailsSeperated = details.split(',')
        var amount = detailsSeperated[0]
        var transType = detailsSeperated[1]
        TRANSACTIONTYPE = transType
        when (transType) {
            "PURCHASE" -> {
                viewmodel.startTransaction(amount.toLong(),
                    0,
                    PaymentType.PURCHASE.value,
                    this.requireContext(),
                    this
                )
            }

            "CASHOUT" -> {
                viewmodel.startTransaction(amount.toLong(),
                    0,
                    PaymentType.CASHOUT.value,
                    this.requireContext(),
                    this
                )
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ProcessingFragment()
    }

    override fun onInsertCard() {
        this.requireActivity().runOnUiThread(Runnable {
            binding.introText.changeText(getString(R.string.insert_card))
        })
    }

    override fun onRemoveCard() {
        println("card to be removed")
        this.requireActivity().runOnUiThread {
            Toast.makeText(this.requireContext(), "Kindly remove the card", Toast.LENGTH_LONG).show()
            findNavController().popBackStack(R.id.paymentLandingFragment, false)
        }
    }

    override fun onPinInput() {
        this.requireActivity().runOnUiThread {
           binding.introText.changeText(getString(R.string.input_pin))
        }
    }

    override fun onEmvProcessing(message: String) {
        println("emv processing => $message")
        this.requireActivity().runOnUiThread(Runnable {
            binding.introText.text = message
        })
    }

    override fun onEmvProcessed(data: Any) {
        this.requireActivity().runOnUiThread {
            binding.introText.changeText("Card reading finished, Sending request to remote server")
            iccData = data as RequestIccData
            println("request data => ${data}")
            data.let {
            when (TRANSACTIONTYPE) {
                "PURCHASE" -> {
                    viewmodel.makeOnlineRequest("purchase")
                }

                "CASHOUT" -> {
                    viewmodel.makeOnlineRequest("cashout")
                }
            }
//                findNavController().navigate(R.id.receiptFragment)
            }
        }
    }
}