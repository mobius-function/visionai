package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.EyeTestResultAdapter
import com.yuvraj.visionai.databinding.FragmentHomeStatisticsBinding
import com.yuvraj.visionai.utils.loadStates.EyeTestLoadStateAdapter
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_home_statistics) {

    private var _binding: FragmentHomeStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapter: EyeTestResultAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        clickableViews()
        setupRecyclerView()
        observeData()
    }


    private fun initViews(view: View) {
//        _binding = FragmentHomeStatisticsBinding.bind(view)
        _binding = FragmentHomeStatisticsBinding.bind(view)
    }

    private fun clickableViews() {
        binding.apply {

        }
    }

    private fun setupRecyclerView() {
        adapter = EyeTestResultAdapter()
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = EyeTestLoadStateAdapter { adapter.retry() }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeData() {
        lifecycleScope.launch {
            Log.d("DebugEyeTests","Getting Eye Tests (fragment)")
            viewModel.getEyeTestsPagingData().collectLatest { pagingData ->
                Log.d("DebugEyeTests","Got Eye Tests (fragment) $pagingData")
                adapter.submitData(pagingData)
            }

            // viewModel.getEyeTestsPagingData().onEach { pagingData ->
            //     adapter.submitData(lifecycle, pagingData)
            // }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}