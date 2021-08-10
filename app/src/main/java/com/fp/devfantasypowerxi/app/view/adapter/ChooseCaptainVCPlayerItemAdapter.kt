package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestDetailActivity
import com.fp.devfantasypowerxi.databinding.RecyclerItemCVcPlayerBinding
import com.fp.devfantasypowerxi.databinding.RecyclerItemContestBinding
// Created on Gaurav Minocha
class ChooseCaptainVCPlayerItemAdapter(private  val mContext:Context) : RecyclerView.Adapter<ChooseCaptainVCPlayerItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemCVcPlayerBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemCVcPlayerBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_c_vc_player,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return 10
    }
}