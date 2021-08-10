package com.fp.devfantasypowerxi.app.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.Banner
import com.fp.devfantasypowerxi.app.view.activity.AddBalanceActivity
import com.fp.devfantasypowerxi.app.view.activity.InviteFriendActivity
import com.fp.devfantasypowerxi.databinding.LayoutSliderImageNewBinding

class SliderBannerAdapterNew(
    var context: Context,
    var urls: ArrayList<Banner>,
    var isForAddCash: Boolean
) :
    PagerAdapter() {
    private val inflater: LayoutInflater
    override fun getCount(): Int {
        return urls.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutSliderImageBinding: LayoutSliderImageNewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_slider_image_new,
            container,
            false
        )
        layoutSliderImageBinding.ivImage.setImageURI(Uri.parse(urls[position].image))
        layoutSliderImageBinding.ivImage.setOnClickListener {
            if (isForAddCash) {
                context.startActivity(Intent(context, AddBalanceActivity::class.java))
            } else {
                if (urls[position].link != "") {
                    openUrl(urls[position].link)
                } else {
                    context.startActivity(Intent(context, InviteFriendActivity::class.java))
                }
            }
        }
        container.addView(layoutSliderImageBinding.getRoot())
        return layoutSliderImageBinding.getRoot()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
/*
    private val widthInPix: Int
        get() {
            val displayMetrics = DisplayMetrics()
            (context as HomeActivity).windowManager.defaultDisplay
                .getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }*/

    /*   public getHeightinPix() {
       DisplayMetrics displayMetrics = new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
       int height = displayMetrics.heightPixels;
       int width = displayMetrics.widthPixels;
   }*/
    private fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

 /*   companion object {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            )
                .toInt()
        }
    }
*/
    init {
        inflater = LayoutInflater.from(context)
    }
}