package com.fp.devfantasypowerxi.app.api.service

import androidx.lifecycle.LiveData
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.request.CreateTeamRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.common.api.ApiResponse
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserRestService {

    @GET("get/match/getlist")
    fun getMatchList(): LiveData<ApiResponse<MatchListResponse>>

    /*@POST("api/auth/leaguedetails")
    fun getContestDetails(@Body contestRequest: ContestRequest): LiveData<ApiResponse<ContestDetailResponse>>*/

    @POST("api/auth/get-challenges-new")
    fun getContest(@Body contestRequest: ContestRequest): LiveData<ApiResponse<ContestResponse>>
    @POST("api/auth/category-leagues")
    fun getContestByCategoryCode(@Body contestRequest: ContestRequest?): CustomCallAdapter.CustomCall<ContestResponse>

    @GET("get/match/{matchid}/players/list")
    fun getPlayerList(@Path("matchid", encoded = true) matchId: String): LiveData<ApiResponse<PlayerListResponse>>


    @POST("post/match/{matchid}/team/create")
    fun createTeam(@Path("matchid", encoded = true) matchId: String,@Body createTeamRequest: CreateTeamRequest): LiveData<ApiResponse<CreateTeamResponse>>

    @GET("get/user/mybalance")
    fun getUsableBalance(): LiveData<ApiResponse<BalanceResponse>>

    @POST("api/auth/joinleague")
    fun joinContest(@Body joinContestRequest: JoinContestRequest): LiveData<ApiResponse<JoinContestResponse>>
}