package com.isw.iswkozen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {

    private val viewmodel : IswKozenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testLoads()
    }


    fun testLoads() {
        viewmodel.setupTerminal()
        viewmodel.loadAllConfig()
    }
}