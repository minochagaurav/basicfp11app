package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.CategoryByContestCategory
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.databinding.RecyclerCategoryItemContestBinding
import java.util.*

// Created on Gaurav Minocha
class CategoryContestItemAdapter(
    private val mContext: Context,
    var moreInfoDataList: ArrayList<CategoryByContestCategory>,
    var listener: OnContestItemClickListener
) : RecyclerView.Adapter<CategoryContestItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerCategoryItemContestBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerCategoryItemContestBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_category_item_contest,
                parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.categoriesItem = moreInfoDataList[position]
        val mAdapter =
            ContestItemAdapter(mContext, moreInfoDataList[position].leagues, listener, false)
        holder.binding.rvSubContest.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mContext)
        holder.binding.rvSubContest.layoutManager = mLayoutManager
        holder.binding.rvSubContest.adapter = mAdapter
        if (moreInfoDataList[position].contest_image_url != "") {
            holder.binding.contestImage.setImageURI(Uri.parse(moreInfoDataList[position].contest_image_url))
        } else {
            holder.binding.contestImage.visibility = View.GONE
        }
        if (moreInfoDataList[position].leagues.size > 3) {
            holder.binding.llViewMore.visibility = View.VISIBLE
        } else {
            holder.binding.llViewMore.visibility = View.GONE
        }

        holder.binding.llViewMore.setOnClickListener { view ->
             UpComingContestActivity().openAllContestActivity(
                moreInfoDataList[position].id
            )
        }
        holder.binding.executePendingBindings()
    }

    /*fun updateData(list: ArrayList<CategoryByContestCategory>) {
        moreInfoDataList = list
        notifyDataSetChanged()
    }
*/
    override fun getItemCount(): Int {
        return moreInfoDataList.size
    }
}