package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.CreateTeamRequest
import com.fp.devfantasypowerxi.app.api.response.CreateTeamResponse
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject

class CreateTeamViewModel: ViewModel() {

    private lateinit var mRepository: MatchRepository
    private val teamRequestMutableLiveData: MutableLiveData<CreateTeamRequest> =
        MutableLiveData<CreateTeamRequest>()

    private val createTeamLiveData: LiveData<Resource<CreateTeamResponse>> =
        Transformations.switchMap(
            teamRequestMutableLiveData
        ) { input ->
            val resourceLiveData: LiveData<Resource<CreateTeamResponse>> =
                mRepository.createTeam(
                    input
                )
            val mediator: MediatorLiveData<Resource<CreateTeamResponse>> =
                MediatorLiveData<Resource<CreateTeamResponse>>()
            mediator.addSource(resourceLiveData) { dataResponse ->
                val resp: CreateTeamResponse = dataResponse.data?: CreateTeamResponse()
                val response: Resource<CreateTeamResponse> = when (dataResponse.status) {
                    Resource.Status.LOADING -> Resource.loading(null)
                    Resource.Status.SUCCESS -> Resource.success(resp)
                    Resource.Status.ERROR -> Resource.error(dataResponse.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }

    override fun onCleared() {
        super.onCleared()
    }
    fun createTeam(): LiveData<Resource<CreateTeamResponse>> {
        return createTeamLiveData
    }

    fun loadCreateTeamRequest(createTeamRequest: CreateTeamRequest) {
        teamRequestMutableLiveData.value = createTeamRequest
    }


    @Inject
    fun setRepository(repository: MatchRepository) {
        mRepository = repository
    }


    fun create(activity: FragmentActivity): CreateTeamViewModel {

        return ViewModelProvider(activity).get(CreateTeamViewModel::class.java)
    }

    fun create(fragment: Fragment): CreateTeamViewModel {
        return ViewModelProvider(fragment).get(CreateTeamViewModel::class.java)

    }
}