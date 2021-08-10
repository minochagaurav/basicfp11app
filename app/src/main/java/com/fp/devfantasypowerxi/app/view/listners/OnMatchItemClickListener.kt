package com.fp.devfantasypowerxi.app.view.listners

interface OnMatchItemClickListener {
    fun onMatchItemClick(
        matchKey: String,
        teamVsName: String,
        teamFirstUrl: String,
        teamSecondUrl: String,
        date: String?
    )
}