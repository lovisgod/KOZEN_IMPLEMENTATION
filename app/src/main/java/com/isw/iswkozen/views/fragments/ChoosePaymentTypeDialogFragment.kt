package com.isw.iswkozen.views.fragments

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.isw.iswkozen.core.data.utilsData.PaymentType
import com.isw.iswkozen.databinding.FragmentChoosePaymentTypeDialogListDialog2Binding
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class ChoosePaymentTypeDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChoosePaymentTypeDialogListDialog2Binding? = null
    val viewmodel: IswKozenViewModel by viewModel()
    private val arguements by navArgs<ChoosePaymentTypeDialogFragmentArgs>()
    private val details by lazy { arguements.transactiondetails }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        getTillevents()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding =
            FragmentChoosePaymentTypeDialogListDialog2Binding.inflate(inflater, container, false)

        return binding.root

    }

    private fun getTillevents() {
        val payMentOptions = details.split(",").get(2)
        if (!payMentOptions.isNullOrEmpty()) {
            handleNavogation(payMentOptions.lowercase())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      handleClicks()
    }

    private fun handleClicks() {
        binding.payWithTransfer.setOnClickListener {
           handleNavogation("transfer")
        }

        binding.payWithUssd.setOnClickListener {
          handleNavogation("ussd")
        }

        binding.payWithQR.setOnClickListener {
          handleNavogation("qr")
        }

        binding.iswCardPayment.setOnClickListener {
           handleNavogation("card")
        }

        binding.payWithPaycode.setOnClickListener {
           handleNavogation("paycode")
        }
    }


    fun handleNavogation(option: String) {
        when (option) {
            "card" -> {
                val direction = ChoosePaymentTypeDialogFragmentDirections.
                actionChoosePaymentTypeDialogFragmentToProcessingFragment(
                    details
                )

                findNavController().navigate(direction)
            }

            "ussd" -> {
                val direction = ChoosePaymentTypeDialogFragmentDirections.
                actionChoosePaymentTypeDialogFragmentToUssdFragment(
                    details
                )

                findNavController().navigate(direction)
            }

            "paycode" -> {
                val direction = ChoosePaymentTypeDialogFragmentDirections.
                actionChoosePaymentTypeDialogFragmentToPayCodeFragment(
                    details
                )

                findNavController().navigate(direction)
            }

            "qr" -> {
                val direction = ChoosePaymentTypeDialogFragmentDirections.
                actionChoosePaymentTypeDialogFragmentToQrcodeFragment(
                    details
                )

                findNavController().navigate(direction)
            }

            "transfer" -> {
                val direction = ChoosePaymentTypeDialogFragmentDirections.
                actionChoosePaymentTypeDialogFragmentToTransferFragment(
                    details
                )

                findNavController().navigate(direction)
            }
        }
    }

    companion object {

        fun newInstance(): ChoosePaymentTypeDialogFragment =
            ChoosePaymentTypeDialogFragment()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}