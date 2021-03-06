package com.fp.devfantasypowerxi.app.api.service

import com.fp.devfantasypowerxi.app.api.request.*
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @POST("post/forget-password")
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

    @POST("post/content/user-promo-contacts")
    fun setContactData(@Body baseRequest: UserData): CustomCallAdapter.CustomCall<NormalResponse>

    @GET("get/user/playing_history")
    fun getMyPlayingHistory(): CustomCallAdapter.CustomCall<PlayingHistoryResponse>

  @GET("get/refer-code")
    fun getReferCode(): CustomCallAdapter.CustomCall<NormalResponse>

    @GET("get/content/get-banners")
    fun getBannerList(): CustomCallAdapter.CustomCall<BannerListResponse>


    @POST("api/auth/send-new-mail")
    fun verifyEmailByOtp(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("post/user/mobile-update")
    fun mobileUpdate(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @POST("api/auth/email-update")
    fun emailUpdate(@Body baseRequest: BaseRequest?): CustomCallAdapter.CustomCall<LoginSendOtpResponse>

    @GET("get/match/{matchid}/challenge/list")
    fun getContestByCategory(
        @Path("matchid", encoded = true) matchId: String,
        @Query("sport_key") sport_key: String,
    ): CustomCallAdapter.CustomCall<CategoryByContestResponse>


    @GET("get/match/{matchid}/challenge/{challengeid}/scorecard")
    fun getWinnersPriceCard(
        @Path("matchid", encoded = true) matchId: String,
        @Path("challengeid", encoded = true) challengeId: String,
        @Query("sport_key") sport_key: String,
    ): CustomCallAdapter.CustomCall<GetWinnerScoreCardResponse>

    @POST("api/auth/category-leagues")
    fun getContestByCategoryCode(@Body contestRequest: ContestRequest?): CustomCallAdapter.CustomCall<ContestResponse>

    @POST("api/auth/get-fantasy-rules")
    fun getFantasyRule(@Body contestRequest: ContestRequest): CustomCallAdapter.CustomCall<FantasyRuleResponse>


    @POST("api/auth/all-verify")
    fun allVerify(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<AllVerifyResponse>


    @GET("get/user/mybalance")
    fun getUsableBalance(): CustomCallAdapter.CustomCall<BalanceResponse>

    @Multipart
    @POST("api/auth/update-profile-image")
    fun uploadUserImage(
        @Part("user_id") userId: RequestBody?,
        @Part file: MultipartBody.Part?,
    ): CustomCallAdapter.CustomCall<ImageUploadResponse>


    @GET("get/user/mytransaction")
    fun getMyTransaction(): CustomCallAdapter.CustomCall<MyTransactionHistoryResponse>


    @POST("api/auth/updateteamleauge")
    fun switchTeam(@Body switchTeamRequest: SwitchTeamRequest): CustomCallAdapter.CustomCall<SwitchTeamResponse>

    @POST("post/match/getcode/{getcode}/join-by-code")
    fun joinByContestCode(
        @Path(
            "getcode",
            encoded = true
        ) getCode: String,
    ): CustomCallAdapter.CustomCall<JoinByContestCodeResponse>

    @POST("api/auth/find-join-team")
    fun findJoinTeam(@Body baseRequest: BaseRequest): CustomCallAdapter.CustomCall<FindJoinTeamResponse>

    @GET("get/userteam/getteamtoshow")
    fun getPreviewPoints(
        @Query("sport_key", encoded = true) sport_key: String,
        @Query("joinid", encoded = true) joinId: String,
        @Query("teamid", encoded = true) teamId: String,
    ): CustomCallAdapter.CustomCall<TeamPointPreviewResponse>

    @POST("post/auth/user/social-login")
    fun userLoginSocial(@Body socialLoginRequest: SocialLoginRequest): CustomCallAdapter.CustomCall<RegisterResponse>

    @GET("get/userteam/playerfullinfo")
    fun getPlayerInfo(
        @Query("matchkey", encoded = true) matchkey: String,
        @Query("playerid", encoded = true) playerid: String,
        @Query("sport_key", encoded = true) sport_key: String,
    ): CustomCallAdapter.CustomCall<PlayerInfoResponse>

    @POST("post/user/add-refercode")
    fun addReferCode(
        @Body referRequest: ReferRequest,
    ): CustomCallAdapter.CustomCall<NormalResponse>

    @POST("post/user/share-app")
    fun shareApp(
    ): CustomCallAdapter.CustomCall<NormalResponse>

}