package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.EyeTestResultAdapter
import com.yuvraj.visionai.databinding.FragmentHomeStatisticsBinding
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.getEyeTestsPagingData().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}