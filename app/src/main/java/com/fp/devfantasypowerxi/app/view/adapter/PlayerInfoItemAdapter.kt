package com.fp.devfantasypowerxi.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerInfoMatchesItem
import com.fp.devfantasypowerxi.databinding.RecyclerItemPlayerInfoBinding
import java.util.*

class PlayerInfoItemAdapter(moreInfoDataList: ArrayList<PlayerInfoMatchesItem>) :
    RecyclerView.Adapter<PlayerInfoItemAdapter.ViewHolder>() {
    private var playerInfoMatchesItems: ArrayList<PlayerInfoMatchesItem>

    inner class ViewHolder(binding: RecyclerItemPlayerInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding: RecyclerItemPlayerInfoBinding

        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemPlayerInfoBinding = DataBindingUtil
            .inflate(LayoutInflater.from(parent.context),
                R.layout.recycler_item_player_info,
                parent,
                false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.playerInfoMatchesItem = playerInfoMatchesItems[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return playerInfoMatchesItems.size
    }

    fun updateData(playerInfoMatchesItems: ArrayList<PlayerInfoMatchesItem>) {
        this.playerInfoMatchesItems = playerInfoMatchesItems
        notifyDataSetChanged()
    }

    init {
        playerInfoMatchesItems = moreInfoDataList
    }
}