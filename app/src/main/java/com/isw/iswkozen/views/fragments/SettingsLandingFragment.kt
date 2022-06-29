package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.isw.iswkozen.R
import com.isw.iswkozen.databinding.FragmentPaymentLandingBinding
import com.isw.iswkozen.databinding.FragmentSettingsLandingBinding
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class SettingsLandingFragment : Fragment() {

    private lateinit var binding: FragmentSettingsLandingBinding
    private val viewModel: IswKozenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_settings_landing, container, false)

        setupPage()

        return binding.root
    }

    private fun setupPage() {
        setTexts()
        handleClicks()
    }

    private fun setTexts() {
        viewModel.readterminalDetails()
        viewModel.terminalInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
               binding.merchantNameText.text = it.merchantName ?: "Merchant"
               binding.tidText.text = it.terminalCode
            }
        })
        viewModel.terminalSetupStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.loaderCard.visibility = View.GONE
            }
        })

        viewModel.keyMStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.loaderCard.visibility = View.GONE
                if (it == 0) {
                    Toast.makeText(requireContext(), "Successful", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Not successful", Toast.LENGTH_LONG).show()
                }
            }
        })


        viewModel.nibbsKeyMStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.loaderCard.visibility = View.GONE
                if (it) {
                    Toast.makeText(requireContext(), "Successful", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Not successful", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun handleClicks() {
        binding.resetConfigCard.setOnClickListener {
            binding.loaderCard.visibility = View.VISIBLE
            viewModel.resetTerminalConfig()
        }

        binding.downloadConfigCard.setOnClickListener {
            binding.loaderCard.visibility = View.VISIBLE
            viewModel.loadAllKeys()
        }


        binding.downloadNibssKeyCard.setOnClickListener {
            binding.loaderCard.visibility = View.VISIBLE
            viewModel.downloadNibbsKey()
        }

    }

    companion object {

        @JvmStatic
        fun newInstance() = SettingsLandingFragment()
    }
}