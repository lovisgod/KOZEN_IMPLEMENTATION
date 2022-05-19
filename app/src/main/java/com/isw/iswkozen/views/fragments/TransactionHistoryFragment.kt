package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.data.utilsData.RequestIccData
import com.isw.iswkozen.core.database.entities.TransactionResultData
import com.isw.iswkozen.core.network.models.fromResponseData
import com.isw.iswkozen.databinding.FragmentReceiptBinding
import com.isw.iswkozen.databinding.FragmentTransactionHistoryBinding
import com.isw.iswkozen.views.adapters.GenericListAdapter
import com.isw.iswkozen.views.adapters.HistoryItemClickListener
import com.isw.iswkozen.views.adapters.getGenericAdapter
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class TransactionHistoryFragment : Fragment(), HistoryItemClickListener {

    lateinit var adapter: GenericListAdapter<TransactionResultData>
    lateinit var terminalInfo: TerminalInfo

    private lateinit var binding: FragmentTransactionHistoryBinding
    val viewmodel: IswKozenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_transaction_history, container, false)

        fetchData()
        return binding.root
    }


    private fun fetchData() {
        viewmodel. getAllTransactionHistory()
        viewmodel.readterminalDetails()
        viewmodel.terminalInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter = getGenericAdapter(R.layout.transaction_item, it, this)
                binding.transList.adapter = adapter
                binding.transList.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
                viewmodel.transactionDataList.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        adapter.submitList(it)
                    }
                })
            }
        })

    }

    companion object {

        @JvmStatic
        fun newInstance() = TransactionHistoryFragment()
    }

    override fun onItemClicked(item: TransactionResultData, terminalInfo: TerminalInfo) {
        // create an empty iccdata for the receipt page
        // create purchase response object for the receipt page

        val purchaseResponse = item.let { response ->
            fromResponseData(response)
        }
        val direction =TransactionHistoryFragmentDirections.actionTransactionHistoryFragmentToReceiptFragment(
            purchaseResponse,
            RequestIccData(
                TRANSACTION_AMOUNT = item.amount
            ),
            item.paymentType.uppercase()
        )
        findNavController().navigate(direction)
    }


}