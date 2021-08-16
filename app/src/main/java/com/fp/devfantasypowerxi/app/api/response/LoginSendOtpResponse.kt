package com.fp.devfantasypowerxi.app.api.response

data class LoginSendOtpResponse(
    var result: SendOtpResponse = SendOtpResponse(),
    var message: String = "",
    var status: String = "",
    )
