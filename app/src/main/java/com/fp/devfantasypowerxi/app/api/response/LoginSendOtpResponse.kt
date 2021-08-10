package com.fp.devfantasypowerxi.app.api.response

data class LoginSendOtpResponse(

    var sendOtpResponse: SendOtpResponse = SendOtpResponse(),
    var message:String ="",
    var status:String ="",
    var userId:String =""
)
