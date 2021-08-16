package com.fp.devfantasypowerxi.app.api.service

import com.fp.devfantasypowerxi.app.api.request.*
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import retrofit2.http.*

interface OAuthRestService {
    @POST("post/auth/user/login")
    fun userLogin(@Body loginRequest: LoginRequest): CustomCallAdapter.CustomCall<LoginResponse>

    @POST("post/auth/user/register")
    fun userRegister(@Body registerRequest: RegisterRequest): CustomCallAdapter.CustomCall<RegisterResponse>

    @POST("post/auth/user/logout")
    fun logout(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("post/auth/user/sendnewOtp")
    fun sendOTP(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<SendOtpResponse>

    @POST("post/auth/user/verify_otp")
    fun otpVerify(@Body otpVerifyRequest: OtpVerfiyRequest): CustomCallAdapter.CustomCall<RegisterResponse>

    @POST("api/auth/forget-password")
    fun forgotPassword(@Body teamNameUpdateRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("post/auth/user/sendOtp")
    fun sendLoginOTP(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @PUT("put/user/changepassword")
    fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @GET("get/user/profile")
    fun getUserFullDetails(): CustomCallAdapter.CustomCall<GetUserFullDetailsResponse>

    @PUT("put/user/editprofile")
    fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): CustomCallAdapter.CustomCall<UpdateProfileResponse>

    @POST("api/auth/mybalance")
    fun getUserBalance(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<MyBalanceResponse>

    @POST("api/auth/my-play-history")
    fun getMyPlayingHistory(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<PlayingHistoryResponse>

    @POST("api/auth/find-scratch-card")
    fun findScratchCard(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<FindScratchResponse>

    @GET("get/content/get-banners")
    fun getBannerList(): CustomCallAdapter.CustomCall<BannerListResponse>


    @POST("api/auth/send-new-mail")
    fun verifyEmailByOtp(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("api/auth/send_new_otp")
    fun verifyByMobile(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>


    @POST("api/auth/mobile-update")
    fun mobileUpdate(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @POST("api/auth/email-update")
    fun emailUpdate(@Body baseRequest: BaseRequest?): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @GET("get/match/{matchid}/contests/list")
    fun getContestByCategory(@Path("matchid", encoded = true) matchId: String): CustomCallAdapter.CustomCall<CategoryByContestResponse>


    @GET("get/match/{matchid}/contests/{contestid}/scorecard")
    fun getWinnersPriceCard(@Path("matchid", encoded = true) matchId: String,@Path("contestid", encoded = true) contestId: String): CustomCallAdapter.CustomCall<GetWinnerScoreCardResponse>

    @POST("api/auth/category-leagues")
    fun getContestByCategoryCode(@Body contestRequest: ContestRequest?): CustomCallAdapter.CustomCall<ContestResponse>

    @POST("api/auth/get-fantasy-rules")
    fun getFantasyRule(@Body contestRequest: ContestRequest): CustomCallAdapter.CustomCall<FantasyRuleResponse>


    @POST("api/auth/all-verify")
    fun allVerify(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<AllVerifyResponse>
}