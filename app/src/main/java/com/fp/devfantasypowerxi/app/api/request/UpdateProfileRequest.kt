package com.fp.devfantasypowerxi.app.api.request

data class UpdateProfileRequest(
    var address: String = "",
    var city: String = "",
    var dob: String = "",
    var gender: String = "",
    var pincode: String = "",
    var state: String = "",
    var user_id: String = "",
    var country: String = "",
    var username: String = ""
)