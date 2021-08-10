package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.view.activity.LiveFinishedContestActivity
import com.fp.devfantasypowerxi.databinding.RecyclerMyItemMatchBinding
// Created on Gaurav Minocha
class MyMatchItemAdapter(private  val mContext:Context) : RecyclerView.Adapter<MyMatchItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerMyItemMatchBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerMyItemMatchBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_my_item_match,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
           mContext. startActivity(Intent(mContext, LiveFinishedContestActivity::class.java))
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return 10
    }
}