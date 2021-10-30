package com.fp.devfantasypowerxi.app.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.JoinedContestActivity
import com.fp.devfantasypowerxi.app.view.activity.LiveFinishedContestActivity
import com.fp.devfantasypowerxi.app.view.listners.OnMatchItemClickListener
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.RecyclerMyItemMatchBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Created on Gaurav Minocha
class MyMatchItemAdapter(
    private val mContext: Context, var moreInfoDataList: ArrayList<MatchListResult>,
    var listener: OnMatchItemClickListener,
    var sportKey: String,
    var fantasyKey: Int
) : RecyclerView.Adapter<MyMatchItemAdapter.ViewHolder>() {
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

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            mContext.startActivity(Intent(mContext, LiveFinishedContestActivity::class.java))
        }

        holder.binding.moreInfo = moreInfoDataList[position]
        holder.binding.ivTeamFirst.setImageURI(moreInfoDataList[position].team1logo)
        holder.binding.ivTeamSecond.setImageURI(moreInfoDataList[position].team2logo)

        if (moreInfoDataList[position].team1color != ""

        ) holder.binding.tvLeftTriangle.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(moreInfoDataList[position].team1color)) else holder.binding.tvLeftTriangle.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#28006f"))

        if (moreInfoDataList[position].team2color != ""

        ) holder.binding.tvRightTriangle.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(moreInfoDataList[position].team2color)) else holder.binding.tvRightTriangle.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#00a9df"))

        holder.binding.tvMatchInfo.text = moreInfoDataList[position].match_status
        if (moreInfoDataList[position].match_status_key == Constants.KEY_LIVE_MATCH) {
            //holder.binding.tvMatchInfo.setText("In Progress");
        } else if (moreInfoDataList[position].match_status_key == Constants.KEY_FINISHED_MATCH) {
            holder.binding.tvMatchInfo.text = "Completed";
        } else if (moreInfoDataList[position].match_status_key == Constants.KEY_UPCOMING_MATCH) {
            var eDate: String? = "2017-09-10 12:05:00"
            if (moreInfoDataList[position].launch_status == "launched") {
                holder.binding.tvTimer.visibility = View.VISIBLE
                holder.binding.tvMatchInfo.visibility = View.GONE
                val c = Calendar.getInstance()
                val hour = c[Calendar.HOUR_OF_DAY]
                val minute = c[Calendar.MINUTE]
                val sec = c[Calendar.SECOND]
                val mYear1 = c[Calendar.YEAR]
                val mMonth1 = c[Calendar.MONTH]
                val mDay1 = c[Calendar.DAY_OF_MONTH]
                var startDate: Date? = null
                var endDate: Date? = null
                var sDate = "2017-09-08 10:05:00"
                sDate =
                    mYear1.toString() + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec
                eDate = moreInfoDataList[position].time_start
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                try {
                    startDate = dateFormat.parse(sDate)
                    endDate = dateFormat.parse(eDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val diffInMs = endDate!!.time - startDate!!.time
                try {
                    val countDownTimer: CountDownTimer = object : CountDownTimer(diffInMs, 1000) {
                        @SuppressLint("SetTextI18n")
                        override fun onTick(millisUntilFinished: Long) {
                            val seconds = millisUntilFinished / 1000 % 60
                            val minutes = millisUntilFinished / (1000 * 60) % 60
                            val diffHours = millisUntilFinished / (60 * 60 * 1000)
                            /*  if (TimeUnit.MILLISECONDS.toDays(millisUntilFinished) > 0) {
                                holder.binding.tvMatchInfo.setText(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) + "d " + twoDigitString(diffHours) + "h " + twoDigitString(minutes) + "m " + twoDigitString(seconds) + "s ");
                            } else {
                                holder.binding.tvMatchInfo.setText(twoDigitString(diffHours) + "h " + twoDigitString(minutes) + "m " + twoDigitString(seconds) + "s ");
                            }*/

                            /* if (TimeUnit.MILLISECONDS.toDays(millisUntilFinished) > 0) {
                                    holder.binding.tvTimer.setText(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) + "d : " + twoDigitString(diffHours) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(seconds) + "s ");
                                } else {*/holder.binding.tvTimer.text = twoDigitString(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                            ) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(seconds) + "s "

                            //    holder.binding.tvTimer.setText(twoDigitString(diffHours) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(seconds) + "s ");
                            //  }
                        }

                        private fun twoDigitString(number: Long): String {
                            if (number == 0L) {
                                return "00"
                            } else if (number / 10 == 0L) {
                                return "0$number"
                            }
                            return number.toString()
                        }

                        override fun onFinish() {
                            if (position < moreInfoDataList.size) {
                                moreInfoDataList.removeAt(position)
                                notifyDataSetChanged()
                            }
                        }
                    }
                    countDownTimer.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                holder.binding.tvMatchInfo.visibility = View.GONE
                holder.binding.tvTimer.visibility = View.VISIBLE
                holder.binding.tvTimer.text = moreInfoDataList[position].getStartDate()
            }
        }

        holder.itemView.setOnClickListener { v: View? ->
            if (moreInfoDataList[position].match_status_key == Constants.KEY_LIVE_MATCH) {
                listener.onMatchItemClick(
                    moreInfoDataList[position].matchkey,
                    moreInfoDataList[position].team1display
                        .toString() + " Vs " + moreInfoDataList[position].team2display,
                    moreInfoDataList[position].team1color,
                    moreInfoDataList[position].team2logo,
                    moreInfoDataList[position].match_status
                )
            } else if (moreInfoDataList[position]
                    .match_status_key == Constants.KEY_FINISHED_MATCH
            ) {
                listener.onMatchItemClick(
                    moreInfoDataList[position].matchkey,
                    moreInfoDataList[position].team1display
                        .toString() + " Vs " + moreInfoDataList[position].team2display,
                    moreInfoDataList[position].team1logo,
                    moreInfoDataList[position].team2logo,
                    moreInfoDataList[position].match_status
                )
            } else if (moreInfoDataList[position]
                    .match_status_key == Constants.KEY_UPCOMING_MATCH
            ) {
                val intent = Intent(mContext, JoinedContestActivity::class.java)
                intent.putExtra(
                    Constants.KEY_MATCH_KEY,
                    moreInfoDataList[position].matchkey
                )
                intent.putExtra(
                    Constants.KEY_TEAM_VS,
                    moreInfoDataList[position].team1display
                        .toString() + " Vs " + moreInfoDataList[position].team2display
                )
                intent.putExtra(
                    Constants.KEY_TEAM_FIRST_URL,
                    moreInfoDataList[position].team1logo
                )
                intent.putExtra(
                    Constants.KEY_TEAM_SECOND_URL,
                    moreInfoDataList[position].team2logo
                )
                intent.putExtra(
                    Constants.KEY_STATUS_HEADER_TEXT,
                    moreInfoDataList[position].time_start
                )
                intent.putExtra(Constants.KEY_STATUS_IS_TIMER_HEADER, true)
                intent.putExtra(Constants.SPORT_KEY, AppUtils.getSaveSportKey())
                intent.putExtra(Constants.KEY_FANTASY_TYPE, fantasyKey)
                mContext.startActivity(intent)
            }
        }
        holder.binding.executePendingBindings()
    }

    fun updateData(list: ArrayList<MatchListResult>) {
        moreInfoDataList = list
        // searchItemFilteredList = list;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return moreInfoDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


}