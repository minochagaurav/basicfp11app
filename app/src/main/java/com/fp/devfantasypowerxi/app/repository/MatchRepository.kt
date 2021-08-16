package com.fp.devfantasypowerxi.app.repository

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.request.CreateTeamRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.request.MyTeamRequest
import com.fp.devfantasypowerxi.app.api.response.*
import com.fp.devfantasypowerxi.app.api.service.UserRestService
import com.fp.devfantasypowerxi.common.api.ApiResponse
import com.fp.devfantasypowerxi.common.api.NetworkBoundResource
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(private val userRestService: UserRestService) {


    private val matchListResponseMutableLiveData: MutableLiveData<MatchListResponse> =
        MutableLiveData<MatchListResponse>()
    private val contestResponseMutableLiveData = MutableLiveData<ContestResponse>()
    fun getHomeDataList(): LiveData<Resource<MatchListResponse>> {
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

                return userRestService.getMatchList()

            }

        }.asLiveData
    }

    //GetPlayerList
    private val playerListResponseMutableLiveData: MutableLiveData<PlayerListResponse> =
        MutableLiveData<PlayerListResponse>()

    fun getPlayerList(teamRequest: MyTeamRequest): LiveData<Resource<PlayerListResponse>> {
        return object : NetworkBoundResource<PlayerListResponse, PlayerListResponse>() {
            override fun saveCallResult(item: PlayerListResponse) {
                playerListResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: PlayerListResponse?): Boolean {
                return if (playerListResponseMutableLiveData.getValue() != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<PlayerListResponse> {
                playerListResponseMutableLiveData.value =
                    playerListResponseMutableLiveData.getValue()
                return playerListResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<PlayerListResponse>> {
                return userRestService.getPlayerList(teamRequest.matchkey)
            }
        }.asLiveData
    }


    //GetContest
    fun getContestList(contestRequest: ContestRequest): LiveData<Resource<ContestResponse>> {
        return object : NetworkBoundResource<ContestResponse, ContestResponse>() {
            override fun saveCallResult(item: ContestResponse) {
                contestResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: ContestResponse?): Boolean {
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

// Create Team


    private val createTeamResponseMutableLiveData: MutableLiveData<CreateTeamResponse> =
        MutableLiveData<CreateTeamResponse>()

    fun createTeam(createTeamRequest: CreateTeamRequest): LiveData<Resource<CreateTeamResponse>> {
        return object : NetworkBoundResource<CreateTeamResponse, CreateTeamResponse>() {
            override fun saveCallResult(item: CreateTeamResponse) {
                createTeamResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: CreateTeamResponse?): Boolean {
                return if (createTeamResponseMutableLiveData.value != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<CreateTeamResponse> {
                createTeamResponseMutableLiveData.value = createTeamResponseMutableLiveData.value
                return createTeamResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<CreateTeamResponse>> {
                return userRestService.createTeam(createTeamRequest.matchkey,createTeamRequest)
            }
        }.asLiveData
    }

    private val contestDetailsResponseMutableLiveData: MutableLiveData<BalanceResponse> =
        MutableLiveData<BalanceResponse>()
    private val balanceResponseMutableLiveData: MutableLiveData<BalanceResponse> =
        MutableLiveData<BalanceResponse>()

    fun getUsableBalance(): LiveData<Resource<BalanceResponse>> {
        return object : NetworkBoundResource<BalanceResponse, BalanceResponse>() {
            override fun saveCallResult(item: BalanceResponse) {
                balanceResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: BalanceResponse?): Boolean {
                return if (contestDetailsResponseMutableLiveData.getValue() != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<BalanceResponse> {
                balanceResponseMutableLiveData.setValue(balanceResponseMutableLiveData.getValue())
                return balanceResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<BalanceResponse>> {
                return userRestService.getUsableBalance()
            }
        }.asLiveData
    }

    private val joinContestResponseMutableLiveData = MutableLiveData<JoinContestResponse>()
    fun joinContest(joinContestRequest: JoinContestRequest): LiveData<Resource<JoinContestResponse>> {
        return object : NetworkBoundResource<JoinContestResponse, JoinContestResponse>() {
            override fun saveCallResult(item: JoinContestResponse) {
                joinContestResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: JoinContestResponse?): Boolean {
                return if (joinContestResponseMutableLiveData.value != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<JoinContestResponse> {
                joinContestResponseMutableLiveData.setValue(joinContestResponseMutableLiveData.value)
                return joinContestResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<JoinContestResponse>> {
                return userRestService.joinContest(joinContestRequest)
            }
        }.asLiveData
    }

}