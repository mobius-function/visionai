package com.yuvraj.visionai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuvraj.visionai.databinding.ItemHomeStatisticsResultBinding
import com.yuvraj.visionai.model.EyeTestResult

class EyeTestResultAdapter : PagingDataAdapter<EyeTestResult, EyeTestResultAdapter.EyeTestViewHolder>(DIFF_CALLBACK) {

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
            binding.apply {
                DateAndTime.text = eyeTest.id

                PlusPowerRightEye.text = eyeTest.plusPowerRightEye.toString()
                PlusPowerLeftEye.text = eyeTest.plusPowerLeftEye.toString()
                MinusPowerRightEye.text = eyeTest.minusPowerRightEye.toString()
                MinusPowerLeftEye.text = eyeTest.minusPowerLeftEye.toString()
                Astigmatism.text = eyeTest.astigmatismResult.toString()
                DryLeftEye.text = eyeTest.dryLeftEyeResult.toString()
                DryRightEye.text = eyeTest.dryRightEyeResult.toString()
                Jaundice.text = eyeTest.jaundiceResult.toString()
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EyeTestResult>() {
            override fun areItemsTheSame(oldItem: EyeTestResult, newItem: EyeTestResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EyeTestResult, newItem: EyeTestResult): Boolean {
                return oldItem == newItem
            }
        }
    }
}
