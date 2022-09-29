package com.arsylk.androidex.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.arsylk.androidex.app.databinding.ActivityMainBinding
import com.arsylk.androidex.lib.domain.lifecycle.LifecycleEx
import kotlinx.coroutines.flow.receiveAsFlow

class MainActivity : AppCompatActivity(), LifecycleEx {
    private var binding: ActivityMainBinding? = null
    private val viewModel by viewModels<MainViewModel>()
    private val adapter by lazy { Adapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.recyclerView?.adapter = adapter
        adapter.submitData(viewModel.service.root, viewModel.service.cyclic.progress)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        findViewById<View>(R.id.startButton).setOnClickListener {
            viewModel.service.getOrStart()
            adapter.submitData(viewModel.service.root, viewModel.service.cyclic.progress)
        }
    }

    private fun setupObservers() {
        viewModel.service.cyclic.progress.onProgress.receiveAsFlow().collectOnResumed {
            adapter.setProgress(it)
        }
    }
}