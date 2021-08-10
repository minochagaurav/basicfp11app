package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.databinding.LayoutPlayerSelectedBinding
import com.fp.devfantasypowerxi.databinding.RecyclerItemMatchBinding
// Created on Gaurav Minocha
class SelectedUnSelectedPlayerAdapter(private  val mContext:Context) : RecyclerView.Adapter<SelectedUnSelectedPlayerAdapter.ViewHolder>() {
    class ViewHolder(val binding: LayoutPlayerSelectedBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutPlayerSelectedBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.layout_player_selected,
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