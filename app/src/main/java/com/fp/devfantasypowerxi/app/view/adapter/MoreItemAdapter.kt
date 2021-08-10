package com.fp.devfantasypowerxi.app.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.dataModel.MoreInfoData
import com.fp.devfantasypowerxi.app.view.interfaces.OnMoreItemClickListener
import com.fp.devfantasypowerxi.databinding.RecyclerItemMoreBinding
// Created on Gaurav Minocha
class MoreItemAdapter(
    private val moreInfoDataList: List<MoreInfoData>,
    private val listener: OnMoreItemClickListener,
    private val isForPaymentOptions: Boolean
) : RecyclerView.Adapter<MoreItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemMoreBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemMoreBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_more,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.moreInfo = moreInfoDataList[position]
        if (isForPaymentOptions) {
            holder.binding.imgForward.visibility = View.GONE
            holder.binding.tvName.setTextColor(Color.parseColor("#828282"))
        } else {
            holder.binding.imgForward.visibility = View.VISIBLE
            holder.binding.tvName.setTextColor(Color.parseColor("#272727"))
        }
        holder.binding.ivMenu.setImageResource(moreInfoDataList[position].resourceId!!)
        holder.itemView.setOnClickListener {
            listener.onMoreItemClick(
                position,
                moreInfoDataList[position].name
            )
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return moreInfoDataList.size
    }
}