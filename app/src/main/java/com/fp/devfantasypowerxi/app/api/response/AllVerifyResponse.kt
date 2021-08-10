package com.fp.devfantasypowerxi.app.api.response

data class AllVerifyResponse(
    val result: AllVerifyResult = AllVerifyResult(),
    val status: Int = 0,
    val message: String = ""
)

data class AllVerifyResult(
    val bank_verify: Int = 0,
    val email_verify: Int = 0,
    val mobile_verify: Int = 0,
    val pan_verify: Int = 0
)