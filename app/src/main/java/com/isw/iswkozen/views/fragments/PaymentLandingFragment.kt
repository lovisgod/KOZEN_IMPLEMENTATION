package com.isw.iswkozen.views.fragments

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.isw.iswkozen.IswApplication
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.models.DeviceType
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.databinding.FragmentPaymentLandingBinding
import com.isw.iswkozen.views.viewmodels.BluetoothViewModel
import com.isw.iswkozen.views.utilViews.Greetings
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import com.isw_smart_bluetooth.interfaces.TillCommand
import com.pixplicity.easyprefs.library.Prefs
import com.pos.sdk.security.POIHsmManage
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.properties.Delegates

class PaymentLandingFragment : Fragment() {

    var isNibbs by Delegates.notNull<Boolean>()

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
            if (isNibbs) {
               Toast.makeText(this.requireContext(), "Transactions not allowed on this terminal", Toast.LENGTH_LONG).show()
               return@setOnClickListener
            }
            val direction = PaymentLandingFragmentDirections.actionPaymentLandingFragmentToAmountFragment("CASHOUT")
            findNavController().navigate(direction)
//            viewModel.downloadNibbsParams()
        }
    }

    override fun onResume() {
        super.onResume()
        if (IswApplication.DEVICE_TYPE == DeviceType.KOZEN) {
            if (Prefs.getBoolean("ISNIBSS", false)) {
                println("is nibbs =< ${Prefs.getBoolean("ISNIBSS", false)}")
//            binding.withdrawal.visibility = View.GONE
                isNibbs = true
                if (!Prefs.getBoolean("NIBSSKEYSUCCESS", false)) {
                    println("NIBBS key already downloaded")
                } else {
                    viewModel.downloadNibbsKey()
                }
            } else {
                isNibbs = false
            }
        } else {
            if (Prefs.getBoolean("ISNIBSS", false)) {
                    println("is nibbs =< ${Prefs.getBoolean("ISNIBSS", false)}")
//            binding.withdrawal.visibility = View.GONE
                    isNibbs = true
                    if (!Prefs.getBoolean("NIBSSKEYSUCCESS", false)) {
                        println("NIBBS key already downloaded")
                    } else {
//                        viewModel.downloadNibbsKey()
                    }
                } else {
                    isNibbs = false
                }
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