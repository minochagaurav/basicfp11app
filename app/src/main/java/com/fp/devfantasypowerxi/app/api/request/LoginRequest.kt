package com.fp.devfantasypowerxi.app.api.request

data class LoginRequest(
    var password: String = "",
    var email: String = "",
    var deviceId: String = "",
    var fcmToken: String = "",
    var login_type: String = ""
)