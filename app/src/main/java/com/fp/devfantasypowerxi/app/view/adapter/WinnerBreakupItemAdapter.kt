package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.GetWinnerScoreCardResult
import com.fp.devfantasypowerxi.app.view.listners.OnImageClickListener
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.databinding.RecyclerItemPriceCardBinding
import java.util.*

// Created on Gaurav Minocha
class WinnerBreakupItemAdapter(private  val mContext:Context,var moreInfoDataList: ArrayList<GetWinnerScoreCardResult>,var listener: OnImageClickListener) : RecyclerView.Adapter<WinnerBreakupItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemPriceCardBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemPriceCardBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_price_card,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.priceCard =moreInfoDataList.get(position)
        if (moreInfoDataList.get(position).image!="") {
            holder.binding.ivBreakup.visibility = View.VISIBLE
            holder.binding.price.visibility = View.GONE
            AppUtils.loadPopupImage(
                holder.binding.ivBreakup,
                moreInfoDataList.get(position).image
            )
        } else {
            holder.binding.ivBreakup.visibility = View.GONE
            holder.binding.price.visibility = View.VISIBLE
        }


        holder.binding.ivBreakup.setOnClickListener {
            listener.onImageClick(
                moreInfoDataList.get(
                    position
                ).image
            )
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return moreInfoDataList.size
    }
    fun update(list: ArrayList<GetWinnerScoreCardResult>) {
        moreInfoDataList = list
    }

}