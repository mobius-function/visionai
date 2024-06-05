package com.yuvraj.visionai.adapters
//
//import androidx.paging.PagingDataAdapter
//import com.yuvraj.visionai.model.EyeTestResult
//
//
//class EyeTestResultAdapter : PagingDataAdapter<EyeTestResult, EyeTestResultAdapter.EyeTestViewHolder>(EyeTestDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EyeTestViewHolder {
//        val binding = ItemEyeTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return EyeTestViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: EyeTestViewHolder, position: Int) {
//        val eyeTest = getItem(position)
//        if (eyeTest != null) {
//            holder.bind(eyeTest)
//        }
//    }
//
//    class EyeTestViewHolder(private val binding: ItemEyeTestBinding) : RecyclerView.ViewHolder(binding.root) {
////        fun bind(eyeTest: EyeTestResult) {
////            binding.eyeTest = eyeTest
////            binding.executePendingBindings()
////        }
//    }
//
//    class EyeTestDiffCallback : DiffUtil.ItemCallback<EyeTest>() {
//        override fun areItemsTheSame(oldItem: EyeTest, newItem: EyeTest): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: EyeTest, newItem: EyeTest): Boolean {
//            return oldItem == newItem
//        }
//    }
//}
