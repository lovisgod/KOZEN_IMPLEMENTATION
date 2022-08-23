package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.databinding.FragmentPaymentLandingBinding
import com.isw.iswkozen.views.viewmodels.BluetoothViewModel
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import com.isw_smart_bluetooth.interfaces.TillCommand
import com.pixplicity.easyprefs.library.Prefs
import com.pos.sdk.security.POIHsmManage
import org.koin.android.viewmodel.ext.android.viewModel

class PaymentLandingFragment : Fragment() {

    private lateinit var binding: FragmentPaymentLandingBinding
    private val viewModel: IswKozenViewModel by viewModel()
    private val blViewModel: BluetoothViewModel by viewModel()



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
        observeBluetooth()
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

    private fun observeBluetooth() {
        with(blViewModel) {
            bluetoothCommand.observe(viewLifecycleOwner, Observer {
                when(it) {
                    is TillCommand.Purchase -> {
                        val tillDetails = "${it.command.amount},PURCHASE, ${it.command.paymentOption.name}"

                        val action = PaymentLandingFragmentDirections.actionPaymentLandingFragmentToChoosePaymentTypeDialogFragment(tillDetails)
                        blViewModel.resetCommand()
                        findNavController().navigate(action)
                    }
                    else -> { }
                }
            })
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = PaymentLandingFragment()
    }
}