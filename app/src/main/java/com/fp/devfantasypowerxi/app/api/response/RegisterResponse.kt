package com.fp.devfantasypowerxi.app.api.response

data class RegisterResponse(
    val message: String = "",
    val result: RegisterResult = RegisterResult(),
    val status: Int = 0
)

data class RegisterResult(

    val bank_verify: Int = 0,
    val custom_user_token: String = "",
  //  val device_id: String = "",
  //  val dob: String = "",
    // val city: String = "",
    //val address: String = "",
    //val fcmToken: String = "",
    // val gender: String = "",
    // val otp: Int = 0,
    //  val pincode: String = "",
    //  val team: String = "",
    //  val user_profile_image: String = "",
    val email: String = "",
    val email_verify: Int = 0,
    val jwt_token: String = "",
    val mobile: String = "",
    val mobile_verify: Int = 0,
    val pan_verify: Int = 0,
    val refercode: String = "",
    val user_id: Int = 0,
    val username: String = ""
)