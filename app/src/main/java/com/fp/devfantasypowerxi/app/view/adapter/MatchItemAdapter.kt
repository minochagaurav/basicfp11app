package com.fp.devfantasypowerxi.app.view.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.listners.OnMatchItemClickListener
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.RecyclerItemMatchBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Created on Gaurav Minocha
class MatchItemAdapter(
    var listener: OnMatchItemClickListener,
    var activity: AppCompatActivity,
    var moreInfoDataList: ArrayList<MatchListResult>,
) : RecyclerView.Adapter<MatchItemAdapter.ViewHolder>() {
    /* class ViewHolder(val binding: RecyclerItemMatchBinding) : RecyclerView.ViewHolder(
         binding.root
     )
 */
    class ViewHolder(val binding: RecyclerItemMatchBinding) : RecyclerView.ViewHolder(
        binding.root
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemMatchBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_match,
                parent, false
            )
        return ViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat", "ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.moreInfo = moreInfoDataList[position]
        holder.binding.ivTeamFirst.setImageURI(moreInfoDataList[position].team1logo)
        holder.binding.ivTeamSecond.setImageURI(moreInfoDataList[position].team2logo)
        holder.binding.team1Name.text= moreInfoDataList[position].team1fdisplay
        holder.binding.team2Name.text= moreInfoDataList[position].team2fdisplay
        if (moreInfoDataList[position].team1color != ""
        ) holder.binding.tvLeftTriangle.setBackgroundColor(Color.parseColor(moreInfoDataList[position].team1color))
        else holder.binding.tvLeftTriangle.setBackgroundColor(Color.parseColor("#28006f"))
        if (moreInfoDataList[position].team2color != ""
        ) holder.binding.tvRightTriangle.setBackgroundColor(Color.parseColor(moreInfoDataList[position].team2color))
        else holder.binding.tvRightTriangle.setBackgroundColor(Color.parseColor("#00a9df"))
        holder.binding.tvMatchInfo.text = moreInfoDataList[position].match_status



        if (!TextUtils.isEmpty(moreInfoDataList[position].announcement) || moreInfoDataList[position].is_mega_text_show == 1) {
            holder.binding.llMegaAnnouncement.visibility = View.VISIBLE
            if (moreInfoDataList[position].is_mega_text_show == 1) {
                holder.binding.llMega.visibility = View.VISIBLE
                holder.binding.tvMegaText.text = moreInfoDataList[position].mega_league_text
            } else {
                holder.binding.llMega.visibility = View.GONE
            }
            if (TextUtils.isEmpty(moreInfoDataList[position].announcement)) {
                holder.binding.tvAnnouncement.clearAnimation()
                holder.binding.tvAnnouncement.visibility = View.GONE
            } else {
                holder.binding.tvAnnouncement.visibility = View.VISIBLE
                holder.binding.tvAnnouncement.text = moreInfoDataList[position].announcement
                val animationToLeft: Animation = TranslateAnimation(500f, -400f, 0f, 0f)
                animationToLeft.duration = 15000
                animationToLeft.repeatMode = Animation.RESTART
                animationToLeft.repeatCount = Animation.INFINITE
                holder.binding.tvAnnouncement.animation = animationToLeft
            }
        } else {
            holder.binding.llMegaAnnouncement.visibility = View.GONE
            holder.binding.tvAnnouncement.clearAnimation()
            holder.binding.tvAnnouncement.visibility = View.GONE
            holder.binding.viewLine.visibility = View.GONE
        }



        if (moreInfoDataList[position].team1color != ""
        )
            holder.binding.cardTeam1.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(moreInfoDataList[position].team1color)) else holder.binding.cardTeam1.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#28006f"))
        if (moreInfoDataList[position].team2color != ""
        )
            holder.binding.cardTeam2.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(moreInfoDataList[position].team2color)) else holder.binding.cardTeam2.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#28006f"))
        if (moreInfoDataList[position].lineup == 1) {
           // holder.binding.tvLinupOut.visibility = View.VISIBLE
            holder.binding.llLinup.setBackgroundResource(R.drawable.linup_res)
            //   timeLeft.setTextColor(context.getResources().getColor(R.color.color_green));
            //    tvLinup.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvLinupOut.visibility = View.GONE
            holder.binding.llLinup.setBackgroundResource(R.drawable.linup_res_white)
            //  timeLeft.setTextColor(context.getResources().getColor(R.color.color_red));
            //  tvLinup.setVisibility(View.GONE);
        }
        if (moreInfoDataList[position].match_status_key == Constants.KEY_LIVE_MATCH) {
            // holder.binding.tvMatchInfo.setText("In Progress");
        } else if (moreInfoDataList[position].match_status_key == Constants.KEY_FINISHED_MATCH) {
            //  holder.binding.tvMatchInfo.setText("Winner Declared");
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
                if (holder.binding.tvTimer.text.toString().trim() == "") {
                    try {
                        val countDownTimer: CountDownTimer =
                            object : CountDownTimer(diffInMs, 1000) {
                                @SuppressLint("SetTextI18n")
                                override fun onTick(millisUntilFinished: Long) {
                                    val seconds = millisUntilFinished / 1000 % 60
                                    val minutes = millisUntilFinished / (1000 * 60) % 60
                                    val diffHours = millisUntilFinished / (60 * 60 * 1000)
                                    /* if (TimeUnit.MILLISECONDS.toDays(millisUntilFinished) > 0) {
                                    holder.binding.tvTimer.setText(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) + "d : " + twoDigitString(diffHours) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(seconds) + "s ");
                                } else {*/
                                    holder.binding.tvTimer.text = twoDigitString(
                                        TimeUnit.MILLISECONDS.toHours(
                                            millisUntilFinished
                                        )
                                    ) + "h : " + twoDigitString(minutes) + "m : " + twoDigitString(
                                        seconds
                                    ) + "s "
                                    //  }

/*                                holder.binding.tvTimer.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));*/
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
                                        holder.binding.tvTimer.text = ""
                                        notifyDataSetChanged()
                                    }
                                }
                            }
                        countDownTimer.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                holder.binding.tvMatchInfo.visibility = View.GONE
                holder.binding.tvTimer.visibility = View.VISIBLE
                holder.binding.tvTimer.setText(moreInfoDataList[position].getStartDate())
            }
        }
        holder.itemView.setOnClickListener {
            if (moreInfoDataList[position].match_status_key == Constants.KEY_LIVE_MATCH) {
                listener.onMatchItemClick(
                    moreInfoDataList[position].matchkey,
                    moreInfoDataList[position].team1display
                        .toString() + " Vs " + moreInfoDataList[position].team2display,
                    moreInfoDataList[position].team1logo,
                    moreInfoDataList[position].team2logo,
                    moreInfoDataList[position].match_status
                )
            } else if (moreInfoDataList[position].match_status_key == Constants.KEY_FINISHED_MATCH) {
                listener.onMatchItemClick(
                    moreInfoDataList[position].matchkey,
                    moreInfoDataList[position].team1display
                        .toString() + " Vs " + moreInfoDataList[position].team2display,
                    moreInfoDataList[position].team1logo,
                    moreInfoDataList[position].team2logo,
                    moreInfoDataList[position].match_status
                )
            } else if (moreInfoDataList[position].match_status_key == Constants.KEY_UPCOMING_MATCH) {
                if (moreInfoDataList[position].launch_status == "launched") {
                    listener.onMatchItemClick(
                        moreInfoDataList[position].matchkey,
                        moreInfoDataList[position].team1display + " Vs " + moreInfoDataList[position].team2display,
                        moreInfoDataList[position].team1logo,
                        moreInfoDataList[position].team2logo,
                        moreInfoDataList[position].time_start
                    )
                } else {
                    AppUtils.showError(activity, "To be Available Soon")
                }
            }
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return moreInfoDataList.size
    }


    fun updateData(list: ArrayList<MatchListResult>) {
        moreInfoDataList = list
        // searchItemFilteredList = list;
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}