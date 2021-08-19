package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.request.MyTeamRequest
import com.fp.devfantasypowerxi.app.api.response.BalanceResponse
import com.fp.devfantasypowerxi.app.api.response.JoinContestResponse
import com.fp.devfantasypowerxi.app.api.response.MyTeamResponse
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject

class TeamViewModel : ViewModel() {

    private lateinit var mRepository: MatchRepository


    private val teamRequestMutableLiveData = MutableLiveData<MyTeamRequest>()
    private val contestLiveData: LiveData<Resource<MyTeamResponse>> = Transformations.switchMap(
        teamRequestMutableLiveData
    ) { input ->
        val resourceLiveData: LiveData<Resource<MyTeamResponse>> = mRepository.getTeams(input!!)
        val mediator: MediatorLiveData<Resource<MyTeamResponse>?> =
            MediatorLiveData<Resource<MyTeamResponse>?>()
        mediator.addSource(
            resourceLiveData
        ) { arrayListResource ->
            val resp: MyTeamResponse = arrayListResource.data ?: MyTeamResponse()
            var response: Resource<MyTeamResponse>? = null
            when (arrayListResource.status) {
                Resource.Status.LOADING -> response = Resource.loading(null)
                Resource.Status.SUCCESS -> response = Resource.success(resp)
                Resource.Status.ERROR -> response =
                    Resource.error(arrayListResource.exception, null)
            }
            mediator.setValue(response)
        }
        mediator
    }


    private val getBalanceRequestMutableLiveData: MutableLiveData<JoinContestRequest> =
        MutableLiveData<JoinContestRequest>()

    private val balanceLiveData: LiveData<Resource<BalanceResponse>> =
        Transformations.switchMap(
            getBalanceRequestMutableLiveData
        ) {
            val resourceLiveData: LiveData<Resource<BalanceResponse>> =
                mRepository.getUsableBalance(
                )
            val mediator: MediatorLiveData<Resource<BalanceResponse>> =
                MediatorLiveData<Resource<BalanceResponse>>()
            mediator.addSource(resourceLiveData) { dataResponse ->
                val resp: BalanceResponse = dataResponse.data ?: BalanceResponse()
                val response: Resource<BalanceResponse> = when (dataResponse.status) {
                    Resource.Status.LOADING -> Resource.loading(null)
                    Resource.Status.SUCCESS -> Resource.success(resp)
                    Resource.Status.ERROR -> Resource.error(dataResponse.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }


    private val getJoinContestRequestMutableLiveData = MutableLiveData<JoinContestRequest>()
    private val joinContestResponseLiveData: LiveData<Resource<JoinContestResponse>> =
        Transformations.switchMap(
            getJoinContestRequestMutableLiveData
        ) { input ->
            val resourceLiveData: LiveData<Resource<JoinContestResponse>> =
                mRepository.joinContest(input)
            val mediator: MediatorLiveData<Resource<JoinContestResponse>> =
                MediatorLiveData<Resource<JoinContestResponse>>()
            mediator.addSource(
                resourceLiveData
            ) { arrayListResource ->
                val resp: JoinContestResponse = arrayListResource.data ?: JoinContestResponse()
                var response: Resource<JoinContestResponse>? = null
                when (arrayListResource.status) {
                    Resource.Status.LOADING -> response = Resource.loading(null)
                    Resource.Status.SUCCESS -> response = Resource.success(resp)
                    Resource.Status.ERROR -> response =
                        Resource.error(arrayListResource.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }


    fun getBalanceData(): LiveData<Resource<BalanceResponse>> {
        return balanceLiveData
    }

    fun loadBalanceRequest(joinContestRequest: JoinContestRequest?) {
        getBalanceRequestMutableLiveData.value = joinContestRequest
    }


    @Inject
    fun setRepository(repository: MatchRepository?) {
        mRepository = repository!!
    }


    override fun onCleared() {
        super.onCleared()
    }

    fun joinContest(): LiveData<Resource<JoinContestResponse>> {
        return joinContestResponseLiveData
    }

    fun loadJoinContestRequest(joinContestRequest: JoinContestRequest?) {
        getJoinContestRequestMutableLiveData.value = joinContestRequest
    }

    fun getContestData(): LiveData<Resource<MyTeamResponse>> {
        return contestLiveData
    }

    fun loadMyTeamRequest(contestRequest: MyTeamRequest) {
        teamRequestMutableLiveData.setValue(contestRequest)
    }

    fun create(activity: FragmentActivity): TeamViewModel {

        return ViewModelProvider(activity).get(TeamViewModel::class.java)
    }

    fun create(fragment: Fragment): TeamViewModel {
        return ViewModelProvider(fragment).get(TeamViewModel::class.java)

    }

}