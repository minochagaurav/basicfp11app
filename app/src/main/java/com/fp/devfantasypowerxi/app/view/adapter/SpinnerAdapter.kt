package com.fp.devfantasypowerxi.app.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.fp.devfantasypowerxi.R

class SpinnerAdapter(var context: Context, var ar: Array<String?>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return ar.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }


    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        //  val v: View
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.spinner_text, null)
        val spinnerText: TextView = v.findViewById<View>(R.id.spinnerText) as TextView
        spinnerText.text = ar[i]
        return v
    }
}