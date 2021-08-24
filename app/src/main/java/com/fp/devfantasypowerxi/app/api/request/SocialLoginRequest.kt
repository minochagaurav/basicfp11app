package com.fp.devfantasypowerxi.app.api.request

import com.google.gson.annotations.SerializedName

data class SocialLoginRequest(

     var email: String = "",
     var deviceId: String = "",
     var fcmToken: String = "",
     var name: String = "",
     var imageUrl: String = "",
     var idToken: String = "",
     var socialLoginType: String = ""


)
