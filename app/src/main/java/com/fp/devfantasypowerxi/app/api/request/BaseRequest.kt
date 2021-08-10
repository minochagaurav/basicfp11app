package com.fp.devfantasypowerxi.app.api.request

data class BaseRequest(
    var user_id: String = "",
    var mobile: String = "",
    var file: String = "",
    var amount: String = "",
    var email: String = "",
    var matchkey: String = "",
    var promo: String = "",
    var payment_type: String = "",
    var challenge_id: String = "",
    var sport_key: String = "",
    var banner_type: String = "",
    var fantasy_type: String = "",
    var otp: String = "",
    var device_ip: String = ""
)
