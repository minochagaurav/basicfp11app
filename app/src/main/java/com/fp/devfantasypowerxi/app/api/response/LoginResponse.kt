package com.fp.devfantasypowerxi.app.api.response

data class LoginResponse(
    val status: Int = 0,
    val message: String = "",
    val result: Result = Result()

)

data class Result(
    val pincode: String = "",
    val address: String = "",
    val device_id: String = "",
    val gender: String = "",
    val city: String = "",
    val custom_user_token: String = "",
    val mobile: String = "",
    val bank_verify: Int = 0,
    val mobile_verify: Int = 0,
    val user_id: Int = 0,
    val dob: String = "",
    val refercode: String = "",
    val email_verify: Int = 0,
    val pan_verify: Int = 0,
    val fcmToken: String = "",
    val email: String = "",
    val username: String = "",
    val jwt_token: String = "",
    val team: String = "",
    val user_profile_image: String = ""
)
