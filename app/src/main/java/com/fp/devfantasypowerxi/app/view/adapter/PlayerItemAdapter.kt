package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestDetailActivity
import com.fp.devfantasypowerxi.databinding.RecyclerItemContestBinding
import com.fp.devfantasypowerxi.databinding.RecyclerItemPlayerBinding
// Created on Gaurav Minocha
class PlayerItemAdapter(private  val mContext:Context) : RecyclerView.Adapter<PlayerItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemPlayerBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemPlayerBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_player,
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