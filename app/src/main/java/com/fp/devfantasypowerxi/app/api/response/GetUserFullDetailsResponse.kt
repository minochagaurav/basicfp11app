package com.fp.devfantasypowerxi.app.api.response

data class GetUserFullDetailsResponse(
    val result: GetUserFullDetailsResult = GetUserFullDetailsResult(),
    val status: Int = 0,
    val message:String=""
)

data class GetUserFullDetailsResult(
    val value: GetUserFullDetailsValue = GetUserFullDetailsValue()
)

data class GetUserFullDetailsValue(
    //val DayOfBirth: String = "",
    //val MonthOfBirth: String = "",
   // val YearOfBirth: String = "",
  //  val activation_status: String = "",
    val address: String = "",
    val city: String = "",
  //  val country: Any? = Any(),
    val dob: String = "",
    val dobfreeze: Int = 0,
    val email: String = "",
    val gender: String = "",
    //val id: Int = 0,
    //val image: String = "",
    val mobile: Long = 0,
   // val mobilefreeze: Int = 0,
 //   val namefreeze: Int = 0,
    val pincode: String = "",
//    val provider: String = "",
  //  val refercode: String = "",
    val state: String = "",
    val statefreeze: Int = 0,
   // val team: String = "",
   // val teamfreeze: Int = 0,
   // val totalchallenges: Int = 0,
   // val totalwon: String = "",
    val username: String = "",
   // val verified: Int = 0,
 //   val walletamaount: Int = 0
)