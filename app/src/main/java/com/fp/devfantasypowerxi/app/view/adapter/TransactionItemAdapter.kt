package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.MyTransactionHistoryData
import com.fp.devfantasypowerxi.databinding.RecyclerItemTransactionHistoryBinding

class TransactionItemAdapter (private  val mContext: Context , private var transactionItems:ArrayList<MyTransactionHistoryData>) : RecyclerView.Adapter<TransactionItemAdapter.ViewHolder>(){
    class ViewHolder(val binding: RecyclerItemTransactionHistoryBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemTransactionHistoryBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_transaction_history,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.transaction = transactionItems[position]
        holder.binding.executePendingBindings()
    }

    fun updateData(list: ArrayList<MyTransactionHistoryData>) {
        transactionItems = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return transactionItems.size
    }
}