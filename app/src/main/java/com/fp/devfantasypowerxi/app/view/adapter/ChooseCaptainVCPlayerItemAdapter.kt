package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.view.activity.ChooseCandVCActivity
import com.fp.devfantasypowerxi.app.view.listners.PlayerItemClickListener
import com.fp.devfantasypowerxi.databinding.RecyclerItemCVcPlayerBinding
import java.util.*

// Created on Gaurav Minocha
class ChooseCaptainVCPlayerItemAdapter(
    private val mContext: Context,
    var selectedCaptainName: String,
    var selectedVcCaptainName: String,
    var playerList: ArrayList<PlayerListResult>,
    val listener: PlayerItemClickListener,
    val sportKey: String
) : RecyclerView.Adapter<ChooseCaptainVCPlayerItemAdapter.ViewHolder>() {
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
        holder.binding.player = playerList[position]
        if (playerList[position].team != "team1") {
            holder.binding.teamNameTv.setBackgroundResource(
                R.drawable.rounded_corner_filled_blue
            )
        } else {
            holder.binding.teamNameTv.setBackgroundResource(
                R.drawable.accent_green_selected
            )
        }


        holder.binding.llCaptainLayout.visibility = View.VISIBLE
        holder.binding.llViceCaptainLayout.visibility = View.VISIBLE
        holder.binding.llSpLayout.visibility = View.GONE

        holder.binding.ivPlayer.setImageURI(
            playerList[position].image
        )

        holder.binding.captain.setOnClickListener {
            for (i in playerList.indices) {
                playerList[i].isCaptain = false
            }
            if (playerList[position].isVcCaptain) {
                selectedVcCaptainName = ""
                playerList[position].isVcCaptain = false
            }
            playerList[position].isCaptain = true
            selectedCaptainName = playerList[position].name

            ChooseCandVCActivity.setCaptainVcCaptionName(
                selectedCaptainName,
                selectedVcCaptainName
            )
            notifyDataSetChanged()
        }

        holder.binding.spSelected.setOnClickListener {
            for (i in playerList.indices) {
                playerList[i].isCaptain = false
            }
            if (playerList[position].isVcCaptain) {
                selectedVcCaptainName = ""
                playerList[position].isVcCaptain = false
            }
            playerList[position].isCaptain = true

            selectedCaptainName = playerList[position].name
            ChooseCandVCActivity.setCaptainVcCaptionName(
                selectedCaptainName,
                selectedVcCaptainName
            )
            notifyDataSetChanged()
        }


        holder.binding.vicecaptain.setOnClickListener {
            for (i in playerList.indices) {
                playerList[i].isVcCaptain = false
            }
            if (playerList[position].isCaptain) {
                selectedCaptainName = ""
                playerList[position].isCaptain = false
            }
            playerList[position].isVcCaptain = true
            selectedVcCaptainName = playerList[position].name
            ChooseCandVCActivity.setCaptainVcCaptionName(
                selectedCaptainName,
                selectedVcCaptainName
            )
            notifyDataSetChanged()
        }


        holder.binding.ivPlayer.setOnClickListener {
            if (mContext is ChooseCandVCActivity) mContext.openPlayerInfoActivity(
                playerList[position].id.toString() + "",
                playerList[position].name,
                playerList[position].team
            )
        }

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    fun sortWithPoints(flag: Boolean) {
        if (flag) {
            playerList.sortWith { contest, t1 ->
                (contest.series_points).compareTo(t1.series_points)
            }
        } else {
            playerList.sortWith { contest, t1 ->
                (t1.series_points).compareTo(contest.series_points)
            }
        }
        notifyDataSetChanged()
    }

    fun sortWithC(flag: Boolean) {
        if (flag) {
            playerList.sortWith { contest, t1 ->
                (contest.captain_selected_by)
                    .compareTo(t1.captain_selected_by)
            }
        } else {
            playerList.sortWith { contest, t1 ->
                (t1.captain_selected_by)
                    .compareTo(contest.captain_selected_by)
            }
        }
        notifyDataSetChanged()
    }

    fun sortWithVc(flag: Boolean) {
        if (flag) {
            playerList.sortWith { contest, t1 ->
                (contest.vice_captain_selected_by)
                    .compareTo(t1.vice_captain_selected_by)
            }
        } else {
            playerList.sortWith { contest, t1 ->
                (t1.vice_captain_selected_by)
                    .compareTo(contest.vice_captain_selected_by)
            }
        }
        notifyDataSetChanged()
    }
}