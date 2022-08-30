package com.isw.iswkozen.views.fragments

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.isw.iswkozen.R
import com.isw.iswkozen.databinding.FragmentPaymentLandingBinding
import com.isw.iswkozen.views.utilViews.Greetings
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class PaymentLandingFragment : Fragment() {

    private lateinit var binding: FragmentPaymentLandingBinding
    private val viewModel: IswKozenViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_payment_landing, container, false)
        setupUI()
        return binding.root
    }


    private fun setupUI(){
        binding.instantPayment.setOnClickListener {
            val direction = PaymentLandingFragmentDirections.actionPaymentLandingFragmentToAmountFragment("PURCHASE")
            findNavController().navigate(direction)
        }

        binding.withdrawal.setOnClickListener {
            val direction = PaymentLandingFragmentDirections.actionPaymentLandingFragmentToAmountFragment("CASHOUT")
            findNavController().navigate(direction)
        }

        val formatter = java.text.SimpleDateFormat("EEE, MMM, d", Locale.ROOT)
        binding.timeText.text = formatter.format(Date())

        binding.getPaidText.text = Greetings.greet()
    }


    override fun onResume() {
        super.onResume()
        val formatter = java.text.SimpleDateFormat("EEE, MMM, d", Locale.ROOT)
        binding.timeText.text = formatter.format(Date())

        binding.getPaidText.text = Greetings.greet()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                  this@PaymentLandingFragment.activity?.finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }


    companion object {
        @JvmStatic
        fun newInstance() = PaymentLandingFragment()
    }
}