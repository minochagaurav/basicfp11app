package com.fp.devfantasypowerxi.app.repository

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.ContestResponse
import com.fp.devfantasypowerxi.app.api.response.MatchListResponse
import com.fp.devfantasypowerxi.app.api.service.UserRestService
import com.fp.devfantasypowerxi.common.api.ApiResponse
import com.fp.devfantasypowerxi.common.api.NetworkBoundResource
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository  @Inject constructor(private val userRestService: UserRestService) {


    private val matchListResponseMutableLiveData: MutableLiveData<MatchListResponse> =
        MutableLiveData<MatchListResponse>()
    private val contestResponseMutableLiveData = MutableLiveData<ContestResponse>()
    fun getHomeDataList(baseRequest: BaseRequest): LiveData<Resource<MatchListResponse>> {
        return object : NetworkBoundResource<MatchListResponse, MatchListResponse>() {
            override fun saveCallResult(@NonNull item: MatchListResponse) {
                matchListResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(@NonNull data: MatchListResponse?): Boolean {
                return if (matchListResponseMutableLiveData.value != null) {
                    true
                } else true
            }

            @NonNull
            override fun loadFromDb(): LiveData<MatchListResponse> {
                matchListResponseMutableLiveData.value = matchListResponseMutableLiveData.value
                return matchListResponseMutableLiveData
            }

            @NonNull
            override fun createCall(): LiveData<ApiResponse<MatchListResponse>> {

                return userRestService.getMatchList(baseRequest)

            }

        }.asLiveData
    }
/*
    fun getContestDetails(contestRequest: ContestRequest): LiveData<Resource<ContestDetailResponse>> {
        return object : NetworkBoundResource<ContestDetailResponse?, ContestDetailResponse?>() {
            override fun saveCallResult(item: ContestDetailResponse) {
                contestDetailsResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(item: ContestDetailResponse?): Boolean {
                return if (contestDetailsResponseMutableLiveData.getValue() != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<ContestDetailResponse?> {
                contestDetailsResponseMutableLiveData.setValue(contestDetailsResponseMutableLiveData.getValue())
                return contestDetailsResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<ContestDetailResponse?>?> {
                return if (contestRequest.isShowLeaderBoard()) userRestService.getLeaderBoard(
                    contestRequest
                ) else userRestService.getContestDetails(contestRequest)
            }
        }.asLiveData
    }*/

    //GetContest
    fun getContestList(contestRequest: ContestRequest): LiveData<Resource<ContestResponse>> {
        return object : NetworkBoundResource<ContestResponse, ContestResponse>() {
            override fun saveCallResult(item: ContestResponse) {
                contestResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(item: ContestResponse?): Boolean {
                return if (contestResponseMutableLiveData.value != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<ContestResponse> {
                contestResponseMutableLiveData.value = contestResponseMutableLiveData.value
                return contestResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<ContestResponse>> {
                return userRestService.getContest(contestRequest)
            }
        }.asLiveData
    }


}