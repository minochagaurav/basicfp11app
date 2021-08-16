package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.MyTeamRequest
import com.fp.devfantasypowerxi.app.api.response.PlayerListResponse
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject

class GetPlayerDataViewModel: ViewModel() {
    private lateinit var mRepository: MatchRepository
    private val teamRequestMutableLiveData: MutableLiveData<MyTeamRequest> =
        MutableLiveData<MyTeamRequest>()

    private val contestLiveData: LiveData<Resource<PlayerListResponse>> =
        Transformations.switchMap(
            teamRequestMutableLiveData
        ) { input ->
            val resourceLiveData: LiveData<Resource<PlayerListResponse>> =
                mRepository.getPlayerList(
                    input
                )
            val mediator: MediatorLiveData<Resource<PlayerListResponse>> =
                MediatorLiveData<Resource<PlayerListResponse>>()
            mediator.addSource(resourceLiveData) { dataResponse ->
                val resp: PlayerListResponse = dataResponse.data?: PlayerListResponse()
                val response: Resource<PlayerListResponse> = when (dataResponse.status) {
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

    fun getPlayerList(): LiveData<Resource<PlayerListResponse>> {
        return contestLiveData
    }

    fun loadPlayerListRequest(contestRequest: MyTeamRequest) {
        teamRequestMutableLiveData.value = contestRequest

    }

    @Inject
    fun setRepository(mRepository: MatchRepository) {
        this.mRepository = mRepository
    }


    fun create(activity: FragmentActivity): GetPlayerDataViewModel {

        return ViewModelProvider(activity).get(GetPlayerDataViewModel::class.java)
    }

    fun create(fragment: Fragment): GetPlayerDataViewModel {
        return ViewModelProvider(fragment).get(GetPlayerDataViewModel::class.java)

    }
}
