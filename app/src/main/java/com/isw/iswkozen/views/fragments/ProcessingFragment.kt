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
import com.interswitchng.smartpos.models.transaction.CardReadTransactionResponse
import com.interswitchng.smartpos.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.utilities.hide
import com.isw.iswkozen.IswApplication
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.dataInteractor.EMVEvents
import com.isw.iswkozen.core.data.models.DeviceType
import com.isw.iswkozen.core.data.utilsData.AccountType
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.data.utilsData.paxDataToRequestIccData
import com.isw.iswkozen.core.network.models.PurchaseResponse
import com.isw.iswkozen.core.utilities.DisplayUtils.show
import com.isw.iswkozen.databinding.FragmentProcessingBinding
import com.isw.iswkozen.views.utilViews.Animator.Companion.changeText
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import getSurchargeFromETT
import org.koin.android.viewmodel.ext.android.viewModel


class ProcessingFragment : Fragment(), EMVEvents {

    private lateinit var binding: FragmentProcessingBinding
    val viewmodel: IswKozenViewModel by viewModel()
    private val arguements by navArgs<ProcessingFragmentArgs>()
    private val details by lazy { arguements.transactiondetails }
    var TRANSACTIONTYPE = ""
    lateinit var iccData: RequestIccData
    private var accountTypex = AccountType.Default
    private lateinit var accountTYpeDialogFragment: AccountTypeFragment

    private lateinit var cardTypXe: CardType

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
        if (isAdded) {
            this.requireActivity().runOnUiThread {
                binding.iswCardPaymentViewAnimator.displayedChild = 0
                Toast.makeText(this.requireContext(), "Kindly remove the card", Toast.LENGTH_LONG).show()
                findNavController().popBackStack(R.id.paymentLandingFragment, false)
            }
        }
    }

    override fun onPinInput() {
        if (isAdded) {
            this.requireActivity().runOnUiThread {
                binding.introText.changeText(getString(R.string.input_pin))
                if (IswApplication.DEVICE_TYPE == DeviceType.PAX) {
                    binding.iswCardPaymentViewAnimator.displayedChild = 1
                }

            }
        }
    }

    override fun onPinText(text: String) {
        super.onPinText(text)
        this.requireActivity().runOnUiThread {
            binding.iswPinFragmentTransfer.cardPin.setText(text)
        }
    }

    override fun onEmvProcessing(message: String) {
        println("emv processing => $message")
        this.requireActivity().runOnUiThread(Runnable {
            binding.iswCardPaymentViewAnimator.displayedChild = 0
            binding.introText.text = message
        })
    }

    override fun onEmvProcessed(data: Any) {
        this.requireActivity().runOnUiThread {
            binding.iswCardPaymentViewAnimator.displayedChild = 0
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

    override fun onCardDetected(cardType: CardType) {
        super.onCardDetected(cardType)
        cardTypXe = cardType
        showAccountTypeFragment()
    }

    override fun onPaxTransactionDone(data: PurchaseResponse, iccData: RequestIccData) {
        super.onPaxTransactionDone(data, iccData)
        data.let {
           this.requireActivity().runOnUiThread {
               binding.iswCardPaymentViewAnimator.displayedChild = 0
               println("response data => ${it.responseMessage}  ${it.responseCode}")
               val direction = ProcessingFragmentDirections.actionProcessingFragmentToReceiptFragment(it,
                   iccData, TRANSACTIONTYPE, "processing")
               findNavController().navigate(direction)
           }
        }

    }



    fun showAccountTypeFragment(){
        val detailsSeperated = details.split(',')
        var amountx = detailsSeperated[0]
        println("transaction amount is ${amountx}")
        var transType = detailsSeperated[1]
        accountTYpeDialogFragment = AccountTypeFragment {
            accountTypex = when (it) {
                0 -> AccountType.Default
                1 -> AccountType.Savings
                2 -> AccountType.Current
                else -> AccountType.Default
            }

            Constants.additionalTransactionInfo.apply {
                cardType = cardTypXe
                amount = amountx
                extendedTransactionType = "6103"
                receivingInstitutionId = "627629"
                destinationAccountNumber = "2089430464"
                surcharge = getSurchargeFromETT(ETT.ETT_6103, amount)
                accountType = accountTypex
                fromAccount = accountTypex.name

            }

            // start transaction
            accountTYpeDialogFragment.dismiss()
            viewmodel.continuePaxTransaction()

        }
        accountTYpeDialogFragment.show(childFragmentManager, "account_type")

    }
}