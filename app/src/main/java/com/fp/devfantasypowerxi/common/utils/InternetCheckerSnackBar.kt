package com.fp.devfantasypowerxi.common.utils

import android.view.View
import com.fp.devfantasypowerxi.app.view.listners.ConnectionLostClickListener

import com.google.android.material.snackbar.Snackbar


class InternetCheckerSnackBar(
    val connectionLost: ConnectionLostClickListener,
    val value: String,
    coordinatorLayout: View
) {
    var snackbar: Snackbar? = Snackbar
        .make(coordinatorLayout, "Connection Lost", Snackbar.LENGTH_INDEFINITE)
        .setAction(
            "RETRY"
        ) {
            connectionLost.onConnectLostListener(this.value)
        }


    fun showSnackBar() {
        snackbar!!.show()
    }


}