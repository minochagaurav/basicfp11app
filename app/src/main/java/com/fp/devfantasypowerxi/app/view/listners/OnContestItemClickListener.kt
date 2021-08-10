package com.fp.devfantasypowerxi.app.view.listners

import com.fp.devfantasypowerxi.app.api.response.League


interface OnContestItemClickListener {

    fun onContestClick(contest: League?, isForDetail: Boolean)
}