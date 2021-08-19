package com.fp.devfantasypowerxi.app.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.PlayerListResult
import com.fp.devfantasypowerxi.app.utils.SelectedPlayer
import com.fp.devfantasypowerxi.app.view.activity.CreateTeamActivity
import com.fp.devfantasypowerxi.app.view.listners.PlayerItemClickListener
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.RecyclerItemPlayerBinding
import java.util.*

// Created on Gaurav Minocha
class PlayerItemAdapter(
    private val mContext: Context,
    private var mainPlayerList: List<PlayerListResult>,
    private
    var playerTypeList: List<PlayerListResult>,
    private
    val listener: PlayerItemClickListener,
    private
    var type: Int,
    private
    val fantasyType: Int
) : RecyclerView.Adapter<PlayerItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemPlayerBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemPlayerBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_player,
                parent, false
            )
        return ViewHolder(binding)
    }

    fun sortWithCredit(flag: Boolean) {
        if (flag) {
            Collections.sort(
                playerTypeList
            ) { contest: PlayerListResult, t1: PlayerListResult ->
                java.lang.Double.valueOf(contest.credit).compareTo(t1.credit)
            }
        } else {
            Collections.sort(
                playerTypeList
            ) { contest: PlayerListResult, t1: PlayerListResult ->
                java.lang.Double.valueOf(t1.credit).compareTo(contest.credit)
            }
        }
        notifyDataSetChanged()
    }

    fun sortWithPoints(flag: Boolean) {
        if (flag) {
            Collections.sort(
                playerTypeList
            ) { contest: PlayerListResult, t1: PlayerListResult ->
                java.lang.Double.valueOf(contest.series_points.toDouble())
                    .compareTo(t1.series_points.toDouble())
            }
        } else {
            Collections.sort(
                playerTypeList
            ) { contest: PlayerListResult, t1: PlayerListResult ->
                java.lang.Double.valueOf(t1.series_points.toDouble())
                    .compareTo(contest.series_points.toDouble())
            }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.player = playerTypeList[position]
        holder.binding.ivPlayer.setImageURI(playerTypeList[position].image)

        if (playerTypeList[position].team != "team1") {
            holder.binding.tvName.setBackgroundResource(R.drawable.rounded_corner_filled_blue)
        } else {
            holder.binding.tvName.setBackgroundResource(R.drawable.accent_green_selected)
        }
        holder.itemView.setOnClickListener { v: View? ->
            if (playerTypeList[position].isSelected) {
                listener.onPlayerClick(false, position, type)
            } else {
                if (fantasyType == 1) {
                    if (type != Constants.BOWLER) listener.onPlayerClick(
                        true,
                        position,
                        type
                    ) else {
                        Toast.makeText(
                            mContext,
                            "This is batting fantasy! you can not choose bowler",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (fantasyType == 2) {
                    if (type == Constants.BOWLER || type == Constants.AR) listener.onPlayerClick(
                        true,
                        position,
                        type
                    ) else {
                        Toast.makeText(
                            mContext,
                            "This is bowling fantasy you can not choose batsman and wicket keeper",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    listener.onPlayerClick(true, position, type)
                }
            }
        }

        if (playerTypeList[position].isSelected) {
            holder.binding.ivSelected.setImageResource(R.drawable.ic_remove_player)
            holder.binding.llBackground.setBackgroundResource(R.color.selectedColor)
        } else {
            holder.binding.ivSelected.setImageResource(R.drawable.ic_add_player)
            holder.binding.llBackground.setBackgroundResource(R.color.white)
        }

        if (playerTypeList[position].is_playing_show == 1) {
            holder.binding.isPNpLayout.visibility = View.VISIBLE
            if (playerTypeList[position].is_playing == 1) {
                holder.binding.isPlayingView.visibility = View.VISIBLE
                holder.binding.isNotPlayingView.visibility = View.GONE
                holder.binding.tvPlayingNotPlaying.text = "Playing"
                holder.binding.tvPlayingNotPlaying.setTextColor(
                    mContext.resources.getColor(R.color.color_green)
                )
            } else {
                holder.binding.isPlayingView.visibility = View.GONE
                holder.binding.isNotPlayingView.visibility = View.VISIBLE
                holder.binding.tvPlayingNotPlaying.text = "Not Playing"
                holder.binding.tvPlayingNotPlaying.setTextColor(
                    mContext.resources.getColor(R.color.color_red)
                )
            }
        } else {
            holder.binding.isPNpLayout.visibility = View.GONE
        }


        holder.binding.ivPlayer.setOnClickListener { view ->
            if (mContext is CreateTeamActivity) (mContext).openPlayerInfoActivity(
                playerTypeList[position].id.toString() + "",
                playerTypeList[position].name,
                playerTypeList[position].team,
                playerTypeList[position].image
            )
        }


        if (mContext is CreateTeamActivity) {
            val selectedPlayer: SelectedPlayer = (mContext).selectedPlayer
            if (mContext.fantasyType > 0) {
                if (playerTypeList[position].team == "team1") {
                    if (selectedPlayer.localTeamplayerCount == 4) {
                        if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                            0.3f else holder.binding.llBackground.alpha = 1.0f
                    } else {
                        checkList(holder, position, selectedPlayer)
                    }
                } else if (playerTypeList[position].team == "team2") {
                    if (selectedPlayer.visitorTeamPlayerCount == 4) {
                        if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                            0.3f else holder.binding.llBackground.alpha = 1.0f
                    } else {
                        checkList(holder, position, selectedPlayer)
                    }
                }
            } else {
                if (playerTypeList[position].team == "team1") {
                    if (selectedPlayer.localTeamplayerCount == 7) {
                        if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                            0.3f else holder.binding.llBackground.alpha = 1.0f
                    } else {
                        checkList(holder, position, selectedPlayer)
                    }
                } else if (playerTypeList[position].team == "team2") {
                    if (selectedPlayer.visitorTeamPlayerCount == 7) {
                        if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                            0.3f else holder.binding.llBackground.alpha = 1.0f
                    } else {
                        checkList(holder, position, selectedPlayer)
                    }
                }
            }
        }
        holder.binding.invalidateAll()
        holder.binding.executePendingBindings()

    }

    override fun getItemCount(): Int {
        return playerTypeList.size
    }

    fun updateData(playerTypeList: ArrayList<PlayerListResult>, type: Int) {
        this.playerTypeList = playerTypeList
        this.type = type

        notifyDataSetChanged()
    }

    fun checkList(
        holder: ViewHolder,
        position: Int,
        selectedPlayer: SelectedPlayer
    ) {
        if (type == WK) {
            if (selectedPlayer.wk_selected == selectedPlayer.wk_max_count) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    0.3f else holder.binding.llBackground.alpha = 1.0f
            } else if (selectedPlayer.wk_selected >= selectedPlayer.wk_min_count && selectedPlayer.extra_player == 0) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    0.3f else holder.binding.llBackground.alpha =
                    1.0f
            } else if ((mContext as CreateTeamActivity).exeedCredit) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    1.0f else if (100 - selectedPlayer.total_credit >= selectedPlayer.total_credit + playerTypeList[position].credit) holder.binding.llBackground.alpha =
                    1.0f else holder.binding.llBackground.alpha = 0.3f
            } else {
                holder.binding.llBackground.alpha = 1.0f
            }
        } else if (type == AR) {
            if (selectedPlayer.ar_selected == selectedPlayer.ar_maxcount) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    0.3f else holder.binding.llBackground.alpha = 1.0f
            } else if (selectedPlayer.ar_selected >= selectedPlayer.ar_mincount && selectedPlayer.extra_player == 0) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    0.3f else holder.binding.llBackground.alpha = 1.0f
            } else if ((mContext as CreateTeamActivity).exeedCredit) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    1.0f else if (100 - selectedPlayer.total_credit >= selectedPlayer.total_credit + playerTypeList[position].credit) holder.binding.llBackground.alpha =
                    1.0f else holder.binding.llBackground.alpha = 0.3f
            } else {
                holder.binding.llBackground.alpha = 1.0f
            }
        } else if (type == BAT) {
            if (selectedPlayer.bat_selected == selectedPlayer.bat_maxcount) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    0.3f else holder.binding.llBackground.alpha = 1.0f
            } else if (selectedPlayer.bat_selected >= selectedPlayer.bat_mincount && selectedPlayer.extra_player == 0) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.setAlpha(
                    0.3f
                ) else holder.binding.llBackground.alpha = 1.0f
            } else if ( (mContext as CreateTeamActivity).exeedCredit) {
                if (playerTypeList[position].isSelected) holder.binding.llBackground.setAlpha(1.0f) else if (100 - selectedPlayer.total_credit >= selectedPlayer.total_credit + playerTypeList[position].credit) holder.binding.llBackground.alpha =
                    1.0f else holder.binding.llBackground.alpha = 0.3f
            } else {
                holder.binding.llBackground.alpha = 1.0f
            }
        } else if (type == BOWLER) {
            if (selectedPlayer.bowl_selected == selectedPlayer.bowl_maxcount) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.setAlpha(
                    0.3f
                ) else holder.binding.llBackground.alpha = 1.0f
            } else if (selectedPlayer.bowl_selected >= selectedPlayer.bat_mincount && selectedPlayer.extra_player == 0) {
                if (!playerTypeList[position].isSelected) holder.binding.llBackground.alpha =
                    0.3f else holder.binding.llBackground.setAlpha(1.0f)
            } else if ( /*(mContext as ChooseTeamActivity).*/(mContext as CreateTeamActivity).exeedCredit) {
                if (playerTypeList[position].isSelected) holder.binding.llBackground.setAlpha(1.0f) else if (100 - selectedPlayer.total_credit >= selectedPlayer.total_credit + playerTypeList[position].credit) holder.binding.llBackground.alpha =
                    1.0f else holder.binding.llBackground.alpha = 0.3f
            } else {
                holder.binding.llBackground.alpha = 1.0f
            }
        }
    }

    companion object {

        private const val WK = 1
        private const val BAT = 2
        private const val AR = 3
        private const val BOWLER = 4

      /*  private const val PG = 1
        private const val SG = 2
        private const val SF = 3
        private const val PF = 4
        private const val C = 5*/


    }
}