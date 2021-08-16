package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.databinding.LayoutPlayerSelectedBinding

// Created on Gaurav Minocha
class SelectedUnSelectedPlayerAdapter(
    private val mContext: Context,
    private var selectedPlayerCount: Int,
    private var totalPlayerCount: Int
) : RecyclerView.Adapter<SelectedUnSelectedPlayerAdapter.ViewHolder>() {
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
        if (position < selectedPlayerCount) holder.binding.ivSelectedUnselected.setImageResource(R.drawable.ic_selected_player) else holder.binding.ivSelectedUnselected.setImageResource(
            R.drawable.ic_unselected
        )

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return totalPlayerCount
    }
    fun update(selectedPlayerCount: Int) {
        this.selectedPlayerCount = selectedPlayerCount
        notifyDataSetChanged()
    }

    fun updateTotalPlayerCount(totalPlayerCount: Int) {
        this.totalPlayerCount = totalPlayerCount
        notifyDataSetChanged()
    }

}