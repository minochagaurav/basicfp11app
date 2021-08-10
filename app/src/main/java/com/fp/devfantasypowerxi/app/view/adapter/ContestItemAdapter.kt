package com.fp.devfantasypowerxi.app.view.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.League
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.UpComingContestActivity
import com.fp.devfantasypowerxi.app.view.listners.OnContestItemClickListener
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.RecyclerItemContestBinding
import java.util.*

// Created on Gaurav Minocha
class ContestItemAdapter(private val mContext: Context,var moreInfoDataList:ArrayList<League> , var listener: OnContestItemClickListener,var isForAllContest:Boolean) :
    RecyclerView.Adapter<ContestItemAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerItemContestBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemContestBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.recycler_item_contest,
                parent, false
            )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.contestData = moreInfoDataList[position]
        holder.itemView.setOnClickListener { v: View? ->
            listener.onContestClick(
                moreInfoDataList[position],
                true
            )
        }
        holder.binding.progressBar.progress =
            moreInfoDataList[position].getjoinedpercentage.toInt()

        if (moreInfoDataList[position].winning_percentage !=""
                && moreInfoDataList[position].winning_percentage !="0"

        ) {
            holder.binding.ivPerctangeLeague.visibility = View.VISIBLE
        } else {
            holder.binding.ivPerctangeLeague.visibility = View.GONE
        }


        if (moreInfoDataList[position].challenge_type == "percentage") {
            holder.binding.txtStartValue.text = moreInfoDataList[position].joinedusers
                .toString() + " teams already entered"
            holder.binding.txtEndValue.text = ""
            holder.binding.progressBar.max = 16
            holder.binding.progressBar.progress = 8
        } else {
            holder.binding.progressBar.max = moreInfoDataList.get(position).maximum_user
            holder.binding.progressBar.progress = moreInfoDataList.get(position).joinedusers
            val left: Int =
                moreInfoDataList.get(position).maximum_user - moreInfoDataList.get(position)
                    .joinedusers
            if (left != 0) holder.binding.txtStartValue.text =
                "$left Spots  left" else holder.binding.txtStartValue.text =
                "Challenge Closed"
            holder.binding.txtEndValue.setText(
                moreInfoDataList.get(position).maximum_user.toString() + " Spots"
            )
        }
        //  android:text="@{contestData.showWinningAmout}"

        //  android:text="@{contestData.showWinningAmout}"
        holder.binding.llWinnerBreakup.setOnClickListener { view ->
            if (moreInfoDataList[position].totalwinners > "0") {
                if (mContext is UpComingContestActivity) (mContext).getWinnerPriceCard(
                    moreInfoDataList[position].id,
                    moreInfoDataList.get(position).win_amount.toString() + ""
                )
               /* if (mContext is AllContestActivity) (mContext).getWinnerPriceCard(
                    moreInfoDataList.get(position).id,
                    moreInfoDataList.get(position).win_amount.toString() + ""
                )*/
            }
        }

        holder.binding.ivGadgetLeague.setOnClickListener { /*Intent intent = new Intent(context, FullImageActivity.class);
                    intent.putExtra(Constants.KEY_IMAGE_URI,moreInfoDataList.get(position).getImage());
                    context.startActivity(intent);*/
            showPopUpImage(moreInfoDataList.get(position).image)
        }

        if (moreInfoDataList[position].image != "") {
            holder.binding.ivGadgetLeague.visibility = View.VISIBLE
            holder.binding.txtTotalWinnings.visibility = View.GONE
            AppUtils.loadPopupImage(
                holder.binding.ivGadgetLeague,
                moreInfoDataList[position].image
            )
            //     app:placeholderImage="@drawable/ic_gadgets_place_holder"

            //      holder.binding.ivGadgetLeague.setImageURI(moreInfoDataList.get(position).getImage());
        } else {
            holder.binding.txtTotalWinnings.visibility = View.VISIBLE
            holder.binding.ivGadgetLeague.visibility = View.GONE
            holder.binding.txtTotalWinnings.text = "₹"+"${moreInfoDataList[position].win_amount}"
        }


        holder.binding.btnJoin.setOnClickListener { view ->
            if (holder.binding.btnJoin.text.toString()=="Invite") {
              //  if (mContext is UpComingContestActivity) (mContext).openShareIntent()
            } else {
                listener.onContestClick(moreInfoDataList[position], false)
            }
        }

        holder.binding.tagC.setOnClickListener {
            AppUtils.showToolTip(
                mContext,
                Constants.TAG_C_TEXT,
                holder.binding.tagC,
                mContext.resources.getColor(R.color.green_color)
            )
        }

        holder.binding.tagB.setOnClickListener {
            val tagB: String =
                moreInfoDataList[position].bonus_percent + " bonus usable"
            AppUtils.showToolTip(
                mContext,
                tagB,
                holder.binding.tagB,
                mContext.resources.getColor(R.color.tooltipColorBonous)
            )
        }

        holder.binding.tagM.setOnClickListener {
            val tagM = "You can join with " + moreInfoDataList[position].max_multi_entry_user
                .toString() + " teams"
            AppUtils.showToolTip(
                mContext,
                tagM,
                holder.binding.tagM,
                mContext.resources.getColor(R.color.colorPrimary)
            )
        }

        holder.binding.executePendingBindings()
    }
    private fun showPopUpImage(imageUrl: String) {
        val dialogue = Dialog(mContext)
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.setContentView(R.layout.popup_gadget_image_dialog)
        dialogue.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogue.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogue.setCancelable(false)
        dialogue.setCanceledOnTouchOutside(false)
        dialogue.setTitle(null)
        val imageView = dialogue.findViewById<ImageView>(R.id.iv_gadget_league)
        AppUtils.loadPopupImage(imageView, imageUrl)
        val img_Close: CardView = dialogue.findViewById(R.id.img_Close)
        img_Close.setOnClickListener { dialogue.dismiss() }
        if (dialogue.isShowing) dialogue.dismiss()
        dialogue.show()
    }
    override fun getItemCount(): Int {
        return moreInfoDataList.size
    }
}