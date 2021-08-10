package com.fp.devfantasypowerxi.app.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Created on Gaurav Minocha

@Parcelize
data class Contest(
    private var totalwinners: Int? = null,
    private var entryfee: String? = null,
    private var challenge_type: String? = null,
    private var winning_percentage: String? = null,
    private var getjoinedpercentage: String? = null,
    private var joinedusers: String? = null,
    private var isjoined: Boolean? = null,
    private var isselected: Boolean? = null,
    private var matchkey: String? = null,
    private var multiEntry: Int? = null,
    private var isselectedid: String? = null,
    private var isRunning: Int? = null,
    private var confirmedChallenge: Int? = null,
    private var isBonus: Int? = null,
    private var refercode: String? = null,
    private var name: String? = null,
    private var id: Int? = null,
    private var winAmount: Int? = null,
    private var maximumUser: Int? = null,
    private var status: Int? = null,
    private var pdf: String? = null,
    private var image: String? = null,
    private var imageDescription: String? = null,
    private var firstprize: String? = null,
    private var winningpercentage: String? = null,
    private var maxMultiEntryUser: Int? = null

):Parcelable



