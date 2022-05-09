package com.isw.iswkozen.views.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.utilities.DisplayUtils
import com.isw.iswkozen.core.utilities.DisplayUtils.hide
import com.isw.iswkozen.databinding.FragmentReceiptBinding
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ReceiptFragment : Fragment() {

    private lateinit var binding: FragmentReceiptBinding
    val viewmodel: IswKozenViewModel by viewModel()
    private val arguements by navArgs<ReceiptFragmentArgs>()
    private val transactionResponse by lazy { arguements.transResponse }
    private val transactionData by lazy { arguements.transdata }
    private lateinit var terminalInfo: TerminalInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_receipt, container, false)

        getTerminalInfo()
        handleclicks()
        return binding.root
    }

    private fun handleclicks() {
        binding.closeBtn.setOnClickListener {
            findNavController().navigate(R.id.paymentLandingFragment)
        }

        binding.shareBtn.setOnClickListener {
            handleShare()
        }
    }

    private fun getTerminalInfo(){
        viewmodel.readterminalDetails()
        viewmodel.terminalInfo.observe(viewLifecycleOwner, Observer {
            it.let {
                terminalInfo = it
                this.requireActivity().runOnUiThread {
                    setupUI()
                }
            }
        })
    }

    private fun setupUI() {
        transactionResponse.let {
            binding.cvm.hide()
            binding.cvmLabel.hide()
            binding.paymentMethod.text = "Card"
            terminalInfo?.let {
                binding.merchantName.text = terminalInfo.merchantName.toString()
                binding.tid.text = terminalInfo.terminalCode.toString()
                binding.merchantLocation.text = "${terminalInfo.merchantAddress1} ${terminalInfo.merchantAddress2}"
            }
            binding.transactionType.text = it.transTYpe

            binding.stan.text = it.stan.toString()
            binding.rrn.text = it.stan.padStart(12, '0')
            binding.responseCode.text = it.responseCode.toString()
            binding.description.text = it.description

            transactionData.let {
                if (!it.EMC_CARD_?.cardNumber.isNullOrEmpty()) {
                    binding.cardPan.text = it.EMC_CARD_?.cardNumber?.let { it1 -> DisplayUtils.maskPan(it1) }
                }

                binding.date.text = it.TRANSACTION_DATE
                binding.aid.text = it.DEDICATED_FILE_NAME
                binding.amount.text = DisplayUtils.getAmountWithCurrency(it.TRANSACTION_AMOUNT, terminalInfo)
            }
//            println("pin status => ${it.pinStatus}")

//            if (it.pinStatus !== null) {
//                binding.cvm.text = it.pinStatus.toString()
//            } else {
//                binding.cvm.text = ""
//            }
//            description.text = "Transaction Not permitted for cardholder"


        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.paymentLandingFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    companion object {

        @JvmStatic
        fun newInstance() = ReceiptFragment()
    }


    private  fun shareImage(bitmap: Bitmap) {
        val path: String = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap, "Design", null
        )
        val uri: Uri = Uri.parse(path)
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_STREAM,
            uri
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    fun handleRecpSharing() {
        DisplayUtils.getScreenBitMap(this.requireActivity(),
            binding.receiptPage
        )?.let {
            shareImage(it)
        }
    }

    private fun handleShare() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                this@ReceiptFragment.requireActivity().runOnUiThread {
                    handleRecpSharing()
                }
            }

        }, 1000)

    }

}