package com.fp.devfantasypowerxi.app.api.response

data class RegisterResponse(
    val message: String = "",
    val result: RegisterResult = RegisterResult(),
    val status: Int = 0
)

data class RegisterResult(
    val address: String = "",
    val bank_verify: Int = 0,
    val city: String = "",
    val custom_user_token: String = "",
    val device_id: String = "",
    val dob: String = "",
    val email: String = "",
    val email_verify: Int = 0,
    val fcmToken: String = "",
    val gender: String = "",
    val jwt_token: String = "",
    val mobile: String = "",
    val mobile_verify: Int = 0,
    val otp: Int = 0,
    val pan_verify: Int = 0,
    val pincode: String = "",
    val refercode: String = "",
    val team: String = "",
    val user_id: Int = 0,
    val user_profile_image: String = "",
    val username: String = ""
)