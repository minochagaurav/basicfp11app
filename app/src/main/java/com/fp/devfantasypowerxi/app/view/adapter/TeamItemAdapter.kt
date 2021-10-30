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
import com.fp.devfantasypowerxi.app.api.response.Team
import com.fp.devfantasypowerxi.app.view.activity.MyTeamsActivity
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.RecyclerItemTeamBinding

// Created on Gaurav Minocha
class TeamItemAdapter(
    private val mContext: Context, var isForJoinContest: Boolean,
    var isForSwitchTeam: Boolean,
    var entry: Int,
    var maxEntry: Int,
    var isForMyTeam: Boolean, var teamList: ArrayList<Team>,
) : RecyclerView.Adapter<TeamItemAdapter.ViewHolder>() {
    var teamId = 0
    var isSelectAll = false
    var totalJoinTeamCount = 0
    var sb1 = StringBuilder()

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

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.team = teamList.get(position)

        holder.binding.ivCaption.setImageURI(teamList[position].getCaptionUrl())
        holder.binding.ivVcCaption.setImageURI(teamList[position].getVcCaptionUrl())

        holder.binding.teamCheckView.setOnCheckedChangeListener(null)
        if (isForMyTeam) {
            holder.binding.teamCheckView.visibility = View.VISIBLE
            holder.binding.teamCheckView.isChecked = teamList[position].isSelected
        } else {
            holder.binding.teamCheckView.visibility = View.GONE
        }

        if (teamList[position].sport_key == Constants.TAG_BASKETBALL) {
            holder.binding.rlVc.visibility = View.GONE
            holder.binding.tvTitleSp.text = "Star Player"
        } else {
            holder.binding.rlVc.visibility = View.VISIBLE
            holder.binding.tvTitleSp.text = "Captain"
        }

        holder.binding.cloneLL.setOnClickListener {
            if (mContext is MyTeamsActivity) mContext.editOrClone(
                teamList[position].players,
                0
            ) else if (mContext is UpComingContestActivity) mContext.editOrClone(
                teamList[position].players, 0
            )
        }

        holder.binding.editLL.setOnClickListener {
            if (mContext is MyTeamsActivity) {
                mContext.editOrClone(
                    teamList[position].players,
                    teamList[position].teamid
                )
            } else if (mContext is UpComingContestActivity) mContext.editOrClone(
                teamList[position].players, teamList[position].teamid
            )
        }

        holder.binding.teamPreviewLayout.setOnClickListener {
            if (mContext is MyTeamsActivity) {

                mContext.openPreviewActivity(
                    teamList[position].players,
                    "Team " + teamList[position].teamnumber
                )
            } else if (mContext is UpComingContestActivity) mContext.openPreviewActivity(
                teamList[position].players, "Team " + teamList[position].teamnumber
            )
        }
        if (isForJoinContest) {
            if (teamList[position].isSelected) holder.binding.teamviewLayout.background =
                mContext.resources
                    .getDrawable(R.drawable.join_background_selector_) else holder.binding.teamviewLayout.background =
                mContext.resources.getDrawable(R.drawable.join_background_unselector_)
        } else {
            holder.binding.teamviewLayout.background =
                mContext.resources.getDrawable(R.drawable.join_background_unselector_)
        }

        if (teamList[position].is_joined == 1) {
            holder.binding.cardViewMain.alpha = 0.5f
        } else {
            holder.binding.cardViewMain.alpha = 1.0f
        }

        holder.binding.teamCheckView.setOnCheckedChangeListener { compoundButton, b ->
            if (teamList[position].is_joined != 1) {
                if (isForJoinContest) {
                    if (isForSwitchTeam || entry == 0) {
                        for (i in teamList.indices) {
                            teamList[i].isSelected = false
                        }
                        teamId = teamList[position].teamid
                        if (totalJoinTeamCount <= maxEntry) {
                            teamList[position].isSelected = true
                        } else {
                            if (maxEntry == 0) {
                                Toast.makeText(
                                    mContext,
                                    "You can join this contest only with " + 1 + " Teams",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnCheckedChangeListener
                            } else {
                                Toast.makeText(
                                    mContext,
                                    "You can join this contest only with $maxEntry Teams",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnCheckedChangeListener
                            }
                        }
                        totalJoinTeamCount = 1
                    } else {
                        val sb = StringBuilder()
                        teamId = teamList[position].teamid
                        if (teamList[position].isSelected) {
                            teamList[position].isSelected = false
                            teamId = 0
                            if (isSelectAll) {
                                isSelectAll = false

                                MyTeamsActivity.setChecked(false)

                            }
                        } else {
                            if (totalJoinTeamCount < maxEntry) {
                                teamList[position].isSelected = true
                            } else {
                                Toast.makeText(
                                    mContext,
                                    "You can join this contest only with " + maxEntry + "Teams",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnCheckedChangeListener
                            }
                        }
                        var j = 0
                        for (i in teamList.indices) {
                            /*           teamList.get(i).setSelected(false);
                                       teamList.get(i).setIsJoined(0);*/
                            if (teamList[i].isSelected) {
                                sb.append(teamList[i].teamid).append(",")
                                ++j
                            }
                        }
                        sb1 = sb
                        totalJoinTeamCount = j
                    }
                    notifyDataSetChanged()
                }
            }
        }

        holder.itemView.setOnClickListener { view: View? -> holder.binding.teamCheckView.performClick() }
        holder.binding.executePendingBindings()
    }

    fun updateData(list: ArrayList<Team>, maxEntry: Int) {
        teamList = list
        this.maxEntry = maxEntry
      //  notifyDataSetChanged()
    }

/*
    public int getSelectedTeamId() {
       return teamId;
    //    return sb.toString();
    }
*/

    /*
    public int getSelectedTeamId() {
       return teamId;
    //    return sb.toString();
    }
*/
    fun getMultipleTeamId(): String {
        return if (isForSwitchTeam || entry == 0) {
            "" + teamId
        } else {
            var returnString = sb1.toString()
            if (returnString[returnString.length - 1] == ',') {
                returnString = returnString.substring(0, returnString.length - 1)
            }
            returnString
        }
    }

  /*  fun selectAll() {
        isSelectAll = true
        val sb = java.lang.StringBuilder()
        var j = 0
        for (i in teamList.indices) {
            if (teamList[i].is_joined == 1) {
                teamId = teamList[i].teamid
                teamList[i].isSelected = true
                if (teamList[i].isSelected) {
                    sb.append(teamList[i].teamid).append(",")
                    ++j
                }
            }
        }
        sb1 = sb
        totalJoinTeamCount = j
        notifyDataSetChanged()
    }
*/

    fun selectAll() {
        var totalSelect = 0
        isSelectAll = true
        val sb = java.lang.StringBuilder()
        var j = 0
        for (i in teamList.indices) {
            if (teamList[i].is_joined != 1) {
                totalSelect += 1
            }
        }
        totalSelect += totalJoinTeamCount
        for (i in teamList.indices) {
            if (teamList[i].is_joined != 1) {
                if (totalSelect < maxEntry) {
                    totalJoinTeamCount += 1
                    teamId = teamList[i].teamid
                    teamList[i].isSelected=true
                    if (teamList[i].isSelected) {
                        sb.append(teamList[i].teamid).append(",")
                        ++j
                    }
                } else {
                    Toast.makeText(mContext,
                        "You can join this contest only with " + maxEntry + "Teams",
                        Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        sb1 = sb
        //        totalJoinTeamCount += j;
        notifyDataSetChanged()
    }

    fun deSelectAll() {
        isSelectAll = false
        teamId = 0
        totalJoinTeamCount = 0
        for (i in teamList.indices) {
            if (teamList[i].is_joined != 1) {
                teamList[i].isSelected = false
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    fun selectedTeamCount(): Int {
        return totalJoinTeamCount
    }

    fun setTotalSelected(totalJoinTeamCount: Int) {
        this.totalJoinTeamCount = totalJoinTeamCount
    }


}