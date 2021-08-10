package com.fp.devfantasypowerxi.app.api.request

data class RegisterRequest(
    var deviceId: String = "",
    var dob: String = "",
    var email: String = "",
    var fcmToken: String = "",
    var mobile: String = "",
    var password: String = "",
    var refer_code: String = ""
)