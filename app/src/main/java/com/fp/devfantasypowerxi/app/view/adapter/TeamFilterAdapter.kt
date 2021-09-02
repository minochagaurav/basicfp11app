package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.listners.TeamFilterClickListener
import com.fp.devfantasypowerxi.databinding.RecyclerItemFilterTeamBinding

class TeamFilterAdapter(
    private val mContext: Context,
    private var teamNames: ArrayList<String>,
    private val listener: TeamFilterClickListener,
    private val selectTeamPosition: Int,
) : RecyclerView.Adapter<TeamFilterAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemFilterTeamBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemFilterTeamBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_filter_team,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.teamName.text = teamNames[position]
        holder.binding.rbTeam.isChecked = position == selectTeamPosition
        holder.binding.rbTeam.setOnClickListener {
            listener.getTeamDataClick(teamNames[position],position)
        }
        holder.binding.constraintLayout.setOnClickListener {
            listener.getTeamDataClick(teamNames[position],position)
        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return teamNames.size
    }
}