package com.fp.devfantasypowerxi.app.repository

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fp.devfantasypowerxi.app.api.request.*
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
                return userRestService.getContest(
                    contestRequest.matchkey,
                    contestRequest.sport_key,
                    contestRequest.fantasy_type.toString(),
                    contestRequest.contest_size,
                    contestRequest.contest_type,
                    contestRequest.winning,
                    contestRequest.entryfee
                )
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
                return userRestService.createTeam(createTeamRequest.matchkey, createTeamRequest)
            }
        }.asLiveData
    }

    private val contestDetailsResponseMutableLiveData: MutableLiveData<ContestDetailResponse> =
        MutableLiveData<ContestDetailResponse>()
    private val balanceResponseMutableLiveData: MutableLiveData<BalanceResponse> =
        MutableLiveData<BalanceResponse>()

    fun getUsableBalance(): LiveData<Resource<BalanceResponse>> {
        return object : NetworkBoundResource<BalanceResponse, BalanceResponse>() {
            override fun saveCallResult(item: BalanceResponse) {
                balanceResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: BalanceResponse?): Boolean {
                return if (contestDetailsResponseMutableLiveData.value != null) {
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
                joinContestResponseMutableLiveData.value = joinContestResponseMutableLiveData.value
                return joinContestResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<JoinContestResponse>> {
                return userRestService.joinContest(
                    joinContestRequest.matchkey,
                    joinContestRequest.challengeid,
                    joinContestRequest
                )
            }
        }.asLiveData
    }

    //GetMyTeams
    private val myTeamResponseMutableLiveData: MutableLiveData<MyTeamResponse> =
        MutableLiveData<MyTeamResponse>()

    fun getTeams(teamRequest: MyTeamRequest): LiveData<Resource<MyTeamResponse>> {
        return object : NetworkBoundResource<MyTeamResponse, MyTeamResponse>() {
            override fun saveCallResult(item: MyTeamResponse) {
                myTeamResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: MyTeamResponse?): Boolean {
                return if (myTeamResponseMutableLiveData.value != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<MyTeamResponse> {
                myTeamResponseMutableLiveData.setValue(myTeamResponseMutableLiveData.getValue())
                return myTeamResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<MyTeamResponse>> {
                return userRestService.getMyTeams(
                    teamRequest.matchkey,
                    teamRequest.challenge_id,
                    teamRequest.sport_key
                )
            }
        }.asLiveData
    }


    fun getContestDetails(contestRequest: ContestRequest): LiveData<Resource<ContestDetailResponse>> {
        return object : NetworkBoundResource<ContestDetailResponse, ContestDetailResponse>() {
            protected override fun saveCallResult(item: ContestDetailResponse) {
                contestDetailsResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: ContestDetailResponse?): Boolean {
                return if (contestDetailsResponseMutableLiveData.value != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<ContestDetailResponse> {
                contestDetailsResponseMutableLiveData.setValue(contestDetailsResponseMutableLiveData.value)
                return contestDetailsResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<ContestDetailResponse>> {
                return if (contestRequest.showLeaderBoard) userRestService.getLeaderBoard(
                    contestRequest.matchkey, contestRequest.challenge_id, contestRequest.page
                ) else userRestService.getContestDetails(contestRequest.matchkey,contestRequest.challenge_id)
            }
        }.asLiveData
    }

    private val joinedContestResponseMutableLiveData: MutableLiveData<JoinedContestResponse> =
        MutableLiveData<JoinedContestResponse>()

    fun joinedContestList(joinContestRequest: JoinContestRequest): LiveData<Resource<JoinedContestResponse>> {
        return object : NetworkBoundResource<JoinedContestResponse, JoinedContestResponse>() {
            override fun saveCallResult(item: JoinedContestResponse) {
                joinedContestResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: JoinedContestResponse?): Boolean {
                return if (joinedContestResponseMutableLiveData.value != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<JoinedContestResponse> {
                joinedContestResponseMutableLiveData.setValue(joinedContestResponseMutableLiveData.getValue())
                return joinedContestResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<JoinedContestResponse>> {
                return userRestService.joinedContestList(joinContestRequest.matchkey)
            }
        }.asLiveData
    }

    private val myMatchListResponseMutableLiveData = MutableLiveData<MatchListResponse>()
    fun getMyMatchesList(baseRequest: BaseRequest): LiveData<Resource<MatchListResponse>> {
        return object : NetworkBoundResource<MatchListResponse, MatchListResponse>() {
            override fun saveCallResult(item: MatchListResponse) {
                myMatchListResponseMutableLiveData.postValue(item)
            }

            override fun shouldFetch(data: MatchListResponse?): Boolean {
                return if (myMatchListResponseMutableLiveData.getValue() != null) {
                    true
                } else true
            }

            override fun loadFromDb(): LiveData<MatchListResponse> {
                myMatchListResponseMutableLiveData.setValue(myMatchListResponseMutableLiveData.getValue())
                return myMatchListResponseMutableLiveData
            }

            override fun createCall(): LiveData<ApiResponse<MatchListResponse>> {
                return userRestService.getMyMatchList(
                    baseRequest.challenge_id,
                    baseRequest.fantasy_type,
                    baseRequest.sport_key,
                    baseRequest.user_id
                )
            }
        }.asLiveData
    }
}