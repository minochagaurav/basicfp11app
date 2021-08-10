package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.databinding.RecyclerItemContestBinding
import com.fp.devfantasypowerxi.databinding.RecyclerItemMatchBinding
import com.fp.devfantasypowerxi.databinding.RecyclerItemTeamBinding
// Created on Gaurav Minocha
class TeamItemAdapter(private  val mContext:Context) : RecyclerView.Adapter<TeamItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemTeamBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemTeamBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_team,
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