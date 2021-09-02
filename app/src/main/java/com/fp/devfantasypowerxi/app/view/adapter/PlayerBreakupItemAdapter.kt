package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerBreakUpItem
import com.fp.devfantasypowerxi.databinding.RecyclerItemPlayerPointsBreakupBinding

// Created on Gaurav Minocha
class PlayerBreakupItemAdapter(private val mContext: Context,var playerPointItems:ArrayList<PlayerBreakUpItem>) :
    RecyclerView.Adapter<PlayerBreakupItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemPlayerPointsBreakupBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemPlayerPointsBreakupBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_player_points_breakup,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvEvent.text = playerPointItems[position].event
        holder.binding.tvActual.text = playerPointItems[position].actual
        holder.binding.tvPoint.text = playerPointItems[position].points
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return playerPointItems.size
    }
    fun updateData(list: ArrayList<PlayerBreakUpItem>) {
        playerPointItems = list
        notifyDataSetChanged()
    }

}