package com.yuvraj.visionai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuvraj.visionai.databinding.ItemHomeStatisticsResultBinding
import com.yuvraj.visionai.model.EyeTestResult


class EyeTestResultAdapter : PagingDataAdapter<EyeTestResult, EyeTestResultAdapter.EyeTestViewHolder>(EyeTestDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EyeTestViewHolder {
        val binding = ItemHomeStatisticsResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EyeTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EyeTestViewHolder, position: Int) {
        val eyeTest = getItem(position)
        if (eyeTest != null) {
            holder.bind(eyeTest)
        }
    }

    class EyeTestViewHolder(private val binding: ItemHomeStatisticsResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(eyeTest: EyeTestResult) {
            binding.tvDateAndTime.text = eyeTest.id
        }
    }

    class EyeTestDiffCallback : DiffUtil.ItemCallback<EyeTestResult>() {
        override fun areItemsTheSame(oldItem: EyeTestResult, newItem: EyeTestResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EyeTestResult, newItem: EyeTestResult): Boolean {
            return oldItem == newItem
        }
    }
}
