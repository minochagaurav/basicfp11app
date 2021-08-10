package com.fp.devfantasypowerxi.app.api.request

data class ChangePasswordRequest(
    var user_id: String = "",
    var oldpassword: String = "",
    var newpassword: String = ""
)
