package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.Contest
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestDetailActivity
import com.fp.devfantasypowerxi.databinding.RecyclerItemJoinedContestTeamBinding
import java.util.*

// Created on Gaurav Minocha
class ContestJoinTeamItemAdapter(private  val mContext:Context,var isContestDetail: Boolean,
                                var moreInfoDataList: ArrayList<Contest>,
                                var sportKey: String,
                                var fantasyType: Int) : RecyclerView.Adapter<ContestJoinTeamItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemJoinedContestTeamBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemJoinedContestTeamBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_joined_contest_team,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.binding.setContestData(moreInfoDataList[position])
        holder.binding.ivTeamImage.setImageURI(Uri.parse(moreInfoDataList[position].user_image))

        if (isContestDetail) {
            holder.binding.tvRank.visibility = View.GONE
            holder.binding.tvPoints.visibility = View.GONE
            holder.binding.tvAmount.visibility = View.GONE
            if (moreInfoDataList[position].isjoined) {
                holder.binding.ivTeamSwitch.visibility = View.VISIBLE
            } else {
                holder.binding.ivTeamSwitch.visibility = View.GONE
            }
        } else {
            holder.binding.tvRank.visibility = View.VISIBLE
            holder.binding.tvPoints.visibility = View.VISIBLE
            holder.binding.tvAmount.visibility = View.VISIBLE
            holder.binding.ivTeamSwitch.visibility = View.GONE
        }


        holder.binding.ivTeamSwitch.setOnClickListener {
            if (mContext is UpComingContestDetailActivity) {
                if (moreInfoDataList[position].isjoined) mContext.switchTeam(
                    moreInfoDataList[position].join_id.toString() + ""
                )
            }
        }

        holder.itemView.setOnClickListener {
            if (isContestDetail) {
                if (moreInfoDataList[position].isCurrentTeam()) {
                    if (mContext is UpComingContestDetailActivity) mContext.openPlayerPointActivityForLeatherBoard(
                        isContestDetail,
                        moreInfoDataList[position].team_id,
                        moreInfoDataList[position].challenge_id,
                        moreInfoDataList[position].teamname,
                        moreInfoDataList[position].points,
                        sportKey,
                        fantasyType
                    )
                } else {
                    Toast.makeText(
                        mContext,
                        "Please wait,Match has not started yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (mContext is UpComingContestDetailActivity) mContext.openPlayerPointActivityForLeatherBoard(
                    isContestDetail,
                    moreInfoDataList[position].team_id,
                    moreInfoDataList[position].challenge_id,
                    moreInfoDataList[position].teamname,
                    moreInfoDataList[position].points,
                    sportKey,
                    fantasyType

                )
            }
        }


        holder.binding.executePendingBindings()
    }
    fun updateData(list: ArrayList<Contest>) {
        moreInfoDataList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return moreInfoDataList.size
    }
}