package com.fp.devfantasypowerxi.app.api.service

import androidx.lifecycle.LiveData
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.ContestResponse
import com.fp.devfantasypowerxi.app.api.response.MatchListResponse
import com.fp.devfantasypowerxi.common.api.ApiResponse
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import retrofit2.http.Body
import retrofit2.http.POST

interface UserRestService {

    @POST("api/auth/getmatchlist")
    fun getMatchList(@Body baseRequest: BaseRequest): LiveData<ApiResponse<MatchListResponse>>

    /*@POST("api/auth/leaguedetails")
    fun getContestDetails(@Body contestRequest: ContestRequest): LiveData<ApiResponse<ContestDetailResponse>>*/

    @POST("api/auth/get-challenges-new")
    fun getContest(@Body contestRequest: ContestRequest): LiveData<ApiResponse<ContestResponse>>
    @POST("api/auth/category-leagues")
    fun getContestByCategoryCode(@Body contestRequest: ContestRequest?): CustomCallAdapter.CustomCall<ContestResponse>
}