package com.fp.devfantasypowerxi.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.Html
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.GetWinnerScoreCardResult
import com.fp.devfantasypowerxi.app.view.ExpandableHeightListView
import com.fp.devfantasypowerxi.app.view.adapter.PriceItemAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skydoves.balloon.ArrowConstraints
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// Created on Gaurav Minocha
object AppUtils {
    private var fantasyType = 0

    fun getWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        return width
    }

    fun showError(appCompatActivity: AppCompatActivity, msg: String?) {
        val flashbar: Flashbar = Flashbar.Builder(appCompatActivity)
            .gravity(Flashbar.Gravity.TOP)
            .title(appCompatActivity.resources.getString(R.string.app_name))
            .message(msg!!)
            .backgroundDrawable(R.drawable.bg_gradient)
            .showIcon()
            .icon(R.mipmap.logo_without_name)
            .iconAnimation(
                FlashAnim.with(appCompatActivity)
                    .animateIcon()
                    .pulse()
                    .alpha()
                    .duration(750)
                    .accelerate()
            )
            .build()
        flashbar.show()
        Handler().postDelayed({ flashbar.dismiss() }, 2000)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun eventDateMileSecond(eventDate: String?): Long {
        val eventDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eventDate)
        val ctime = eventDate.time - System.currentTimeMillis()
        val seconds = ctime / 1000 % 60
        val minutes = ctime / (1000 * 60) % 60
        val diffHours = ctime / (60 * 60 * 1000)
        return ctime
    }

    fun saveSportsKey(sportKey: String?) {
        MyApplication.preferenceDB!!.putString(Constants.SF_SPORT_KEY, sportKey)
    }

    @JvmName("getSaveSportKey1")
    fun getSaveSportKey(): String {
        return MyApplication.preferenceDB!!.getString(Constants.SF_SPORT_KEY)!!
    }


    @JvmName("setFantasyType1")
    fun setFantasyType(fantKey: Int) {
        fantasyType = fantKey
    }

    @JvmName("getFantasyType1")
    fun getFantasyType(): Int {
        return fantasyType
    }

    fun setStatusBarColor(activity: AppCompatActivity, color: Int) {
        val window = activity.window
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    val saveSportKey: String
        get() = MyApplication.preferenceDB!!.getString(Constants.SF_SPORT_KEY)!!

    @SuppressLint("SetTextI18n")
    fun showWinningPopup(
        context: Context?,
        priceCardlist: ArrayList<GetWinnerScoreCardResult>,
        s: String
    ) {
        val priceCard: ExpandableHeightListView?
        val totalWinnersAmount: TextView?
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(R.layout.price_card_dialog)
        priceCard = dialog.findViewById(R.id.priceCard)
        totalWinnersAmount = dialog.findViewById(R.id.totalWinnersAmount)
        totalWinnersAmount!!.text = "FC $s"
        priceCard!!.isExpanded = true
        priceCard.adapter = PriceItemAdapter(context, priceCardlist)
        dialog.show()
    }

    fun loadPopupImage(imageView: ImageView?, url: String?) {
        //  Log.e("url",url);
        if (url != null && url != "") {
            Picasso.get()
                .load(url.replace(" ", "%20"))
                .placeholder(R.drawable.ic_gadgets_place_holder)
                .error(R.drawable.ic_gadgets_place_holder)
                .into(imageView)
        }
    }

    fun showToolTip(context: Context?, msg: String, viewGroup: LinearLayout?, colorCode: Int) {
        var width = 0.0f
        width = if (msg.contains("join")) {
            0.6f
        } else {
            0.5f
        }
        val balloon: Balloon = Balloon.Builder(context!!)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setArrowVisible(true)
            .setWidthRatio(width)
            .setHeight(45)
            .setTextSize(13f)
            .setCornerRadius(3f)
            .setAlpha(0.8f)
            .setText(msg)
            .setPadding(2)
            .setTextColor(ContextCompat.getColor(context, R.color.white))
            .setTextIsHtml(true)
            .setBackgroundColor(colorCode)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .build()

        // balloon.show(viewGroup);
        balloon.showAlignBottom(viewGroup!!)
    }

    @SuppressLint("SetTextI18n")
    fun showFantasyRulePopup(context: Context, title: String, htmlText: String, imgUrl: String) {
        val tvTitle: TextView?
        val tvClose: TextView?
        val tvHtml: TextView?
        val imageView: ImageView?
        val dialog = BottomSheetDialog(context, R.style.SheetDialog)
        dialog.setContentView(R.layout.fantasy_rule_layout)
        tvTitle = dialog.findViewById(R.id.tv_title)
        tvClose = dialog.findViewById(R.id.tv_close)
        tvHtml = dialog.findViewById(R.id.tv_html)
        imageView = dialog.findViewById(R.id.iv_banner)

        if (title.equals(Constants.TAG_FANTASY_TYPE_BATTING, ignoreCase = true)) {
            imageView!!.setImageResource(R.drawable.banner_batting)
        } else if (title.equals(Constants.TAG_FANTASY_TYPE_BOWLING, ignoreCase = true)) {
            imageView!!.setImageResource(R.drawable.banner_bowling)
        } else {
            imageView!!.setImageResource(R.drawable.banner_classic_premium)
        }

        if (!imgUrl.equals("", ignoreCase = true)) AppUtils.loadImageBanner(imageView, imgUrl)

        tvTitle!!.text = "$title Fantasy"
        tvClose!!.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        tvHtml!!.text = Html.fromHtml(htmlText)
        dialog.show()
    }
    fun loadImageBanner(imageView: ImageView?, url: String?) {
        if (url != null && url != "") {
            Picasso.get().load(url.replace(" ", "%20")).placeholder(R.drawable.banner_placeholder)
                .error(R.drawable.banner_placeholder).into(imageView)
        }
    }

}