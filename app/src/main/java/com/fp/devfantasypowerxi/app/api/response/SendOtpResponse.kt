package com.fp.devfantasypowerxi.app.api.response

data class SendOtpResponse(
    var otp :Int =0,
    var message :String ="",
    var status :String ="",
    var user_id: Int = 0
)
