package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.activity.BreakupPlayerPointsActivity
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestDetailActivity
import com.fp.devfantasypowerxi.databinding.RecyclerItemPlayerPointsBinding

// Created on Gaurav Minocha
class PlayerPointsItemAdapter(private  val mContext:Context) : RecyclerView.Adapter<PlayerPointsItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemPlayerPointsBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemPlayerPointsBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_player_points,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
         //   mContext.startActivity(Intent(mContext, BreakupPlayerPointsActivity::class.java))

            val intent =
                Intent(mContext, BreakupPlayerPointsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return 10
    }
}