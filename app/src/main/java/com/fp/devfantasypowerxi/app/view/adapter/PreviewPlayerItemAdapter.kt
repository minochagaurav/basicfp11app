package com.fp.devfantasypowerxi.app.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.databinding.RecyclerPreviewPlayerItemBinding
import java.util.*

class PreviewPlayerItemAdapter(isFromPreviewPoint: Boolean, playerList: List<PlayerListResult>) :
    RecyclerView.Adapter<PreviewPlayerItemAdapter.ViewHolder>() {
    private var playerList: List<PlayerListResult>
    var isFromPreviewPoint: Boolean

     class ViewHolder(binding: RecyclerPreviewPlayerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding: RecyclerPreviewPlayerItemBinding = binding

     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerPreviewPlayerItemBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_preview_player_item,
                parent, false
            )
        binding.root.getLayoutParams().width = parent.measuredWidth / playerList.size
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.player = playerList[position]
        val player: PlayerListResult = playerList[position]
        holder.binding.playertypeImage.setImageURI(player.image)
        holder.binding.playerNametxt.text = player.getShortName()
        if (isFromPreviewPoint) {
            holder.binding.playerPointtxt.text = player.points.toString() + ""
        } else {
            holder.binding.playerPointtxt.text = player.credit.toString() + " Cr"
        }
        if (player.captain == 1) {
            holder.binding.cVcTxt.text = "C"
            holder.binding.cVcTxt.visibility = View.VISIBLE
            holder.binding.cVcTxt.setBackgroundResource(R.drawable.cap_selected)
        } else if (player.vicecaptain == 1) {
            holder.binding.cVcTxt.text = "VC"
            holder.binding.cVcTxt.visibility = View.VISIBLE
            holder.binding.cVcTxt.setBackgroundResource(R.drawable.vcap_selected)
        }
        if (!"team1".equals(player.team, ignoreCase = true)) {
            holder.binding.playerNametxt.setBackgroundResource(R.drawable.rounded_corner_filled_blue)

        } else {
            holder.binding.playerNametxt.setBackgroundResource(R.drawable.accent_green_selected)

        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    fun updateData(list: ArrayList<PlayerListResult>, role: String?) {
        playerList = list
        notifyDataSetChanged()
    }

    init {
        this.playerList = playerList
        this.isFromPreviewPoint = isFromPreviewPoint
    }
}