package com.fp.devfantasypowerxi.app.api.request

data class OtpVerfiyRequest(
    var otp :String ="",
    var mobile :String ="",
    var user_id :String ="",
)
