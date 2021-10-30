package com.fp.devfantasypowerxi.app.api.response

data class NormalResponse(
    var message: String = "",
    var status: Int = 0,
    var code: String = "",
    val is_contact_data: Int = 0
)
