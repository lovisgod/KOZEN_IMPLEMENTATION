package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.isw.iswkozen.R
import com.isw.iswkozen.databinding.FragmentPaymentLandingBinding
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import com.pixplicity.easyprefs.library.Prefs
import com.pos.sdk.security.POIHsmManage
import org.koin.android.viewmodel.ext.android.viewModel

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
//            viewModel.downloadNibbsParams()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Prefs.getBoolean("ISNIBSS", false)) {
            binding.withdrawal.visibility = View.GONE
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = PaymentLandingFragment()
    }
}