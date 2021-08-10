package com.fp.devfantasypowerxi.app.view.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.response.GetWinnerScoreCardResult
import com.fp.devfantasypowerxi.app.utils.AppUtils
import java.util.*

class PriceItemAdapter(var context: Context, var list: ArrayList<GetWinnerScoreCardResult>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val v: View?
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = inflater.inflate(R.layout.recycler_item_price_card, null)
        val rank: TextView = v.findViewById(R.id.rank)
        val price: TextView = v.findViewById(R.id.price)
        val imageDescription: TextView = v.findViewById(R.id.tv_imagedes_for_league)
        val swIv: ImageView = v.findViewById(R.id.iv_breakup)
        rank.text = "Rank " + list[i].start_position
        price.text = "₹ " + list[i].price
        if (list[i].image!="") {
            imageDescription.visibility = View.VISIBLE
            imageDescription.setText(list[i].image_description)
            swIv.visibility = View.VISIBLE
            price.visibility = View.GONE
            AppUtils.loadPopupImage(swIv, list[i].image)

        } else {
            imageDescription.visibility = View.GONE
            swIv.visibility = View.GONE
            price.visibility = View.VISIBLE
        }
        swIv.setOnClickListener {
            showPopUpImage(list[i].image)
        }
        return v
    }

    private fun showPopUpImage(imageUrl: String) {
        val dialogue = Dialog(context)
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

}