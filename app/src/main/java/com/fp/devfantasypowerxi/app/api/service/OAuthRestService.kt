package com.fp.devfantasypowerxi.app.api.service

import com.fp.devfantasypowerxi.app.api.request.*
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import retrofit2.http.Body
import retrofit2.http.POST

interface OAuthRestService {
    @POST("api/auth/login")
    fun userLogin(@Body loginRequest: LoginRequest): CustomCallAdapter.CustomCall<LoginResponse>

    @POST("api/auth/register")
    fun userRegister(@Body registerRequest: RegisterRequest): CustomCallAdapter.CustomCall<RegisterResponse>

    @POST("api/auth/logout")
    fun logout(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("api/auth/send_new_otp")
    fun sendOTP(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<SendOtpResponse>

    @POST("api/auth/verify_otp")
    fun otpVerify(@Body otpVerfiyRequest: OtpVerfiyRequest): CustomCallAdapter.CustomCall<RegisterResponse>

    @POST("api/auth/forget-password")
    fun forgotPassword(@Body teamNameUpdateRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>


    @POST("api/auth/sendOtp")
    fun sendLoginOTP(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @POST("api/auth/change-password")
    fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("api/auth/user-full-details")
    fun getUserFullDetails(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<GetUserFullDetailsResponse>

    @POST("api/auth/edit-profile")
    fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): CustomCallAdapter.CustomCall<UpdateProfileResponse>

    @POST("api/auth/mybalance")
    fun getUserBalance(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<MyBalanceResponse>

    @POST("api/auth/my-play-history")
    fun getMyPlayingHistory(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<PlayingHistoryResponse>

    @POST("api/auth/find-scratch-card")
    fun findScratchCard(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<FindScratchResponse>

    @POST("api/auth/get-banners")
    fun getBannerList(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<BannerListResponse>


    @POST("api/auth/send-new-mail")
    fun verifyEmailByOtp(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("api/auth/send_new_otp")
    fun verifyByMobile(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>


    @POST("api/auth/mobile-update")
    fun mobileUpdate(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @POST("api/auth/email-update")
    fun emailUpdate(@Body baseRequest: BaseRequest?): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @POST("api/auth/get-challenges-by-category")
    fun getContestByCategory(@Body contestRequest: ContestRequest): CustomCallAdapter.CustomCall<CategoryByContestResponse>


    @POST("api/auth/getscorecards")
    fun getWinnersPriceCard(@Body contestRequest: ContestRequest): CustomCallAdapter.CustomCall<GetWinnerScoreCardResponse>

    @POST("api/auth/category-leagues")
    fun getContestByCategoryCode(@Body contestRequest: ContestRequest?): CustomCallAdapter.CustomCall<ContestResponse>

    @POST("api/auth/get-fantasy-rules")
    fun getFantasyRule(@Body contestRequest: ContestRequest): CustomCallAdapter.CustomCall<FantasyRuleResponse>


    @POST("api/auth/all-verify")
    fun allVerify(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<AllVerifyResponse>
}