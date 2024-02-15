package com.yuvraj.visionai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yuvraj.visionai.databinding.ItemEyeTestsBinding
import com.yuvraj.visionai.model.EyeTests
import com.yuvraj.visionai.ui.home.fragments.LandingFragmentDirections

class EyeTestsList(
    private val tests: List<EyeTests>,
    private val navigateToTest: (EyeTests) -> Unit) :
    RecyclerView.Adapter<EyeTestsList.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemEyeTestsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(test: EyeTests) {
            binding.testTitle.text = test.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEyeTestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tests[position])

        holder.itemView.setOnClickListener {
            navigateToTest(tests[position])
        }
    }

    override fun getItemCount(): Int = tests.size
}