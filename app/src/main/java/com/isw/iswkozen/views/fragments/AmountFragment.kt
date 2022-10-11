package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.interswitchng.smartpos.shared.utilities.console
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.utilsData.AccountType
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.databinding.FragmentAmountBinding
import com.isw.iswkozen.views.utilViews.Keyboard
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import getSurchargeFromETT
import kotlinx.android.synthetic.main.fragment_amount.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class AmountFragment : Fragment(), Keyboard.KeyBoardListener {

    private lateinit var keyboard: Keyboard
    private lateinit var binding: FragmentAmountBinding
    private var accountType = AccountType.Default
    val viewmodel: IswKozenViewModel by viewModel()
    private val defaultAmount = "0.00"
    private var currentAmount = 0
    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val transType by lazy { amountFragmentArgs.transtype }
    private lateinit var accountTYpeDialogFragment: AccountTypeFragment
    var current = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_amount, container, false)
        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.output.text = "0.00"
        keyboard = Keyboard(this.requireActivity(), this, binding.root.findViewById(R.id.keypad_layout))
    }

    companion object {
        @JvmStatic
        fun newInstance() = AmountFragment()
    }

    override fun onTextChange(text: String) {
        if (text != current) {
            val text = if (text.isEmpty()) defaultAmount else text
            val cleanString = text.replace("[$,.]".toRegex(), "")
            val parsed = cleanString.toDouble()
            val numberFormat = NumberFormat.getInstance()
            numberFormat.minimumFractionDigits = 2
            numberFormat.maximumFractionDigits = 2
            val formatted = numberFormat.format(parsed / 100)
            currentAmount = Integer.valueOf(cleanString)
            binding.output.text = formatted
            current = cleanString
            keyboard!!.setText(cleanString)
        }
        if (text == "") {
            val text = defaultAmount
            val cleanString = text.replace("[$,.]".toRegex(), "")
            binding.output.text = text
            keyboard!!.setText(cleanString)
        }
    }

    override fun onSubmit(text: String) {
        val enteredAmount = binding.output.text.toString()
        if (enteredAmount.isEmpty() || enteredAmount == defaultAmount) {
            Snackbar.make(binding.keypadLayout.btn0, "kindly input amount", Snackbar.LENGTH_LONG).show()
        } else {
            makePayment(currentAmount)
        }
    }

    private fun makePayment(currentAmount: Int) {
        console.log("amount","Amount is => $currentAmount")
        when(transType) {
            "PURCHASE" -> {
                val direction = AmountFragmentDirections.actionAmountFragmentToChoosePaymentTypeDialogFragment("${currentAmount},PURCHASE")
                findNavController().navigate(direction)
            }

            "CASHOUT" -> {
                showAccountTypeFragment()
            }
        }
    }

    fun showAccountTypeFragment(){
        accountTYpeDialogFragment = AccountTypeFragment {
            accountType = when (it) {
                0 -> AccountType.Default
                1 -> AccountType.Savings
                2 -> AccountType.Current
                else -> AccountType.Default
            }

            Constants.additionalTransactionInfo.apply {
                amount = currentAmount.toString()
                extendedTransactionType = "6103"
                receivingInstitutionId = "627629"
                destinationAccountNumber = "2089430464"
                surcharge = getSurchargeFromETT(ETT.ETT_6103, currentAmount.toString())
                fromAccount = accountType.name

            }

            val direction = AmountFragmentDirections.actionAmountFragmentToProcessingFragment("${currentAmount},CASHOUT")
            findNavController().navigate(direction)
        }
        accountTYpeDialogFragment.show(childFragmentManager, "account_type")
    }


}