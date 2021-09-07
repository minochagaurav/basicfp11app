package com.fp.devfantasypowerxi.app.api.service

import androidx.lifecycle.LiveData
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.request.CreateTeamRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.common.api.ApiResponse
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import retrofit2.http.*

interface UserRestService {

    @GET("get/match/getlist")
    fun getMatchList(@Query("sport_key") sport_key: String): LiveData<ApiResponse<MatchListResponse>>

    /*@POST("api/auth/leaguedetails")
    fun getContestDetails(@Body contestRequest: ContestRequest): LiveData<ApiResponse<ContestDetailResponse>>*/
//  http://52.66.253.117/fp11_practice_app/api/v1/get/match/{matchkey}/challenge/new
    @GET("get/match/{matchkey}/challenge/new")
    fun getContest(
        @Path("matchkey", encoded = true) matchId: String,
        @Query("sport_key") sport_key: String,
        @Query("fantasy_type") fantasy_type: String,
        @Query("contest_size") contest_size: String,
        @Query("contest_type") contest_type: String,
        @Query("winning") winning: String,
        @Query("entryfee") entryfee: String
    ): LiveData<ApiResponse<ContestResponse>>

    @POST("api/auth/category-leagues")
    fun getContestByCategoryCode(@Body contestRequest: ContestRequest?): CustomCallAdapter.CustomCall<ContestResponse>

    @GET("get/match/{matchid}/players/list")
    fun getPlayerList(
        @Path(
            "matchid",
            encoded = true
        ) matchId: String,
        @Query("sport_key") sport_key: String,
    ): LiveData<ApiResponse<PlayerListResponse>>

    @POST("post/userteam/match/{matchid}/team/create")
    fun createTeam(
        @Path("matchid", encoded = true) matchId: String,
        @Body createTeamRequest: CreateTeamRequest,
        @Query("sport_key") sport_key: String,
    ): LiveData<ApiResponse<CreateTeamResponse>>

    @GET("get/user/mybalance")
    fun getUsableBalance(): LiveData<ApiResponse<BalanceResponse>>

    @POST("post/match/{matchid}/challenge/{challengeid}/joinleague")
    fun joinContest(
        @Path("matchid", encoded = true) matchId: String,
        @Path("challengeid", encoded = true) challengeId: String,
        @Body joinContestRequest: JoinContestRequest
    ): LiveData<ApiResponse<JoinContestResponse>>

    @GET("get/userteam/match/{matchid}/challenge/{challengeid}/myjointeam")
    fun getMyTeams(
        @Path("matchid", encoded = true) matchId: String,
        @Path("challengeid", encoded = true) challengeId: String,
        @Query("sport_key") sport_key: String
    ): LiveData<ApiResponse<MyTeamResponse>>

    @POST("get/user/match/{matchkey}/challengeid/{challengeid}/leaguedetails")
    fun getContestDetails(
        @Path("matchkey", encoded = true) matchKey: String,
        @Path("challengeid", encoded = true) challengeId: String,
    ): LiveData<ApiResponse<ContestDetailResponse>>

    @GET("get/match/{matchid}/challenge/{challengeid}/leaderboard/{page}")
    fun getLeaderBoard(
        @Path("matchid", encoded = true) matchId: String,
        @Path("challengeid", encoded = true) challengeId: String,
        @Path(
            "page",
            encoded = true
        ) page: String,
        @Query("sport_key") sport_key: String,
    ): LiveData<ApiResponse<ContestDetailResponse>>

    @GET("get/user/match/{matchid}/myjoinedleauges")
    fun joinedContestList(
        @Path(
            "matchid",
            encoded = true
        ) matchId: String,
        @Query("sport_key") sport_key: String,
    ): LiveData<ApiResponse<JoinedContestResponse>>

    @GET("get/user/myjoinedmatches")

    fun getMyMatchList(
        @Query("challenge_id") challenge_id: String,
        @Query("fantasy_type") fantasy_type: String,
        @Query("sport_key") sport_key: String,

    ): LiveData<ApiResponse<MatchListResponse>>

    //   http://52.66.253.117/fp11_practice_app/api/v1/get/user/match/49756/refreshscore?sport_key=cricket&fantasy_type=0
    //use this api on live and fished match result
    @GET("get/user/match/{matchid}/refreshscore")
    fun refreshScore(
        @Path("matchid", encoded = true) matchId: String,
        @Query("sport_key") sport_key: String,
        @Query("fantasy_type") fantasy_type: String
    ): LiveData<ApiResponse<RefreshScoreResponse>>


    //use this api to get player points
    @GET("get/user/match/{matchid}/playerpoints")
    fun getPlayerPoints(
        @Path("matchid", encoded = true) matchId: String,
        @Query("sport_key", encoded = true) sport_key: String,
    ): LiveData<ApiResponse<PlayerPointsResponse>>
}