package com.isw.iswkozen.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.isw.iswkozen.R
import com.isw.iswkozen.databinding.FragmentReceiptBinding
import com.isw.iswkozen.databinding.FragmentTransactionHistoryBinding
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class TransactionHistoryFragment : Fragment() {

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
        viewmodel.getAllTransactionHistory()
    }

    companion object {

        @JvmStatic
        fun newInstance() = TransactionHistoryFragment()
    }
}