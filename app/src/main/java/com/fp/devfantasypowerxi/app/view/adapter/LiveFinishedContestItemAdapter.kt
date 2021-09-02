package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.api.response.LiveFinishedContestData
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.databinding.RecyclerItemLiveFinishedBinding

// Created on Gaurav Minocha
class LiveFinishedContestItemAdapter(
    private val mContext: Context,
    var liveFinishedContestData: ArrayList<LiveFinishedContestData>,
    var listener: OnContestItemClickListener
) :
    RecyclerView.Adapter<LiveFinishedContestItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemLiveFinishedBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemLiveFinishedBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_live_finished,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.contestData = liveFinishedContestData[position]
        holder.itemView.setOnClickListener {
            val contest = League()
            contest.id = liveFinishedContestData[position].id
            contest.getjoinedpercentage = (
                    liveFinishedContestData[position].getjoinedpercentage + ""
                    )
            contest.entryfee = liveFinishedContestData[position].entryfee
            contest.totalwinners = liveFinishedContestData[position].totalwinners.toString()
            contest.win_amount = liveFinishedContestData[position].win_amount
            contest.maximum_user = liveFinishedContestData[position].maximum_user
            contest.joinedusers = liveFinishedContestData[position].joinedusers
            contest.maximum_user = liveFinishedContestData[position].maximum_user
            contest.joinedusers = liveFinishedContestData[position].joinedusers
            contest.winningpercentage = liveFinishedContestData[position].winningpercentage
            contest.firstprize = liveFinishedContestData[position].firstprize.toInt()
            contest.image_description = liveFinishedContestData[position].image_description
            contest.image = liveFinishedContestData[position].image
            listener.onContestClick(contest, true)
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return liveFinishedContestData.size
    }

    fun updateData(list: ArrayList<LiveFinishedContestData>) {
        liveFinishedContestData = list
        notifyDataSetChanged()
    }

}