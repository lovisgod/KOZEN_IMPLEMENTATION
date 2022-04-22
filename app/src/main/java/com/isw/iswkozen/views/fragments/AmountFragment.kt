package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.databinding.FragmentAmountBinding
import com.isw.iswkozen.views.utilViews.Keyboard
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import kotlinx.android.synthetic.main.fragment_amount.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class AmountFragment : Fragment(), Keyboard.KeyBoardListener {

    private lateinit var keyboard: Keyboard
    private lateinit var binding: FragmentAmountBinding
    val viewmodel: IswKozenViewModel by viewModel()
    private val defaultAmount = "0.00"
    private var currentAmount = 0
    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val transType by lazy { amountFragmentArgs.transtype }

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
        if (text != "") {
            val text = if (text.isEmpty()) defaultAmount else text
            val cleanString = text.replace("[$,.]".toRegex(), "")
            val parsed = cleanString.toDouble()
            val numberFormat = NumberFormat.getInstance()
            numberFormat.minimumFractionDigits = 2
            numberFormat.maximumFractionDigits = 2
            val formatted = numberFormat.format(parsed / 100)
            currentAmount = Integer.valueOf(cleanString)
            binding.output.text = formatted
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
        println("Amount is => $currentAmount")
        when(transType) {
            "PURCHASE" -> {
                val direction = AmountFragmentDirections.actionAmountFragmentToProcessingFragment("${currentAmount},PURCHASE")
                findNavController().navigate(direction)
            }
        }
    }


}