package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.ContestResponse
import com.fp.devfantasypowerxi.app.api.response.PlayerPointsResponse
import com.fp.devfantasypowerxi.app.api.response.RefreshScoreResponse
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject

class ContestViewModel : ViewModel() {
    private lateinit var mRepository: MatchRepository

    private val networkInfoObservable: MutableLiveData<String> =
        MutableLiveData<String>()
    private val contestRequestMutableLiveData: MutableLiveData<ContestRequest> =
        MutableLiveData<ContestRequest>()

    private val contestLiveData: LiveData<Resource<ContestResponse>> =
        Transformations.switchMap(
            contestRequestMutableLiveData
        ) { input ->
            val resourceLiveData: LiveData<Resource<ContestResponse>> =
                mRepository.getContestList(
                    input
                )
            val mediator: MediatorLiveData<Resource<ContestResponse>> =
                MediatorLiveData<Resource<ContestResponse>>()
            mediator.addSource(resourceLiveData) { dataResponse ->
                val resp: ContestResponse = dataResponse.data?:ContestResponse()
                val response: Resource<ContestResponse> = when (dataResponse.status) {
                    Resource.Status.LOADING -> Resource.loading(null)
                    Resource.Status.SUCCESS -> Resource.success(resp)
                    Resource.Status.ERROR -> Resource.error(dataResponse.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }


    private val refreshScoreRequestMutableLiveData = MutableLiveData<ContestRequest>()
    private val refreshScoreLiveData: LiveData<Resource<RefreshScoreResponse>> =
        Transformations.switchMap(
            refreshScoreRequestMutableLiveData
        ) { input ->
            val resourceLiveData: LiveData<Resource<RefreshScoreResponse>> =
                mRepository.getRefreshScore(input)
            val mediator: MediatorLiveData<Resource<RefreshScoreResponse>> =
                MediatorLiveData<Resource<RefreshScoreResponse>>()
            mediator.addSource(
                resourceLiveData
            ) { arrayListResource ->
                val resp: RefreshScoreResponse = arrayListResource.data?: RefreshScoreResponse()
                val response: Resource<RefreshScoreResponse> = when (arrayListResource.status) {
                    Resource.Status.LOADING -> Resource.loading(null)
                    Resource.Status.SUCCESS -> Resource.success(resp)
                    Resource.Status.ERROR -> Resource.error(arrayListResource.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }


    private val playerPointsRequestMutableLiveData = MutableLiveData<ContestRequest>()
    private val playerPointsLiveData: LiveData<Resource<PlayerPointsResponse>> =
        Transformations.switchMap(
            playerPointsRequestMutableLiveData
        ) { input ->
            val resourceLiveData: LiveData<Resource<PlayerPointsResponse>> =
                mRepository.getPlayerPoints(input)
            val mediator: MediatorLiveData<Resource<PlayerPointsResponse>?> =
                MediatorLiveData<Resource<PlayerPointsResponse>?>()
            mediator.addSource(
                resourceLiveData
            ) { arrayListResource ->
                val resp: PlayerPointsResponse = arrayListResource.data?: PlayerPointsResponse()
                var response: Resource<PlayerPointsResponse>? = null
                when (arrayListResource.status) {
                    Resource.Status.LOADING -> response = Resource.loading(null)
                    Resource.Status.SUCCESS -> response = Resource.success(resp)
                    Resource.Status.ERROR -> response = Resource.error(arrayListResource.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }

    /**
     * Expose the LiveData So that UI can observe it.
     */
    fun getContestData(): LiveData<Resource<ContestResponse>> {
        return contestLiveData
    }
    fun getRefreshScore(): LiveData<Resource<RefreshScoreResponse>> {
        return refreshScoreLiveData
    }
    fun getPlayerPoints(): LiveData<Resource<PlayerPointsResponse>> {
        return playerPointsLiveData
    }

    fun loadContestRequest(contestRequest: ContestRequest?) {
        contestRequestMutableLiveData.value = contestRequest
    }
    fun loadPlayerPointRequest(contestRequest: ContestRequest) {
        playerPointsRequestMutableLiveData.setValue(contestRequest)
    }


    @Inject
    fun setRepository(repository: MatchRepository?) {
        mRepository = repository!!
    }

    fun loadRefreshScore(contestRequest: ContestRequest?) {
        refreshScoreRequestMutableLiveData.setValue(contestRequest)
    }

    fun clear() {
        networkInfoObservable.value = null
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun create(activity: FragmentActivity): ContestViewModel {

        return ViewModelProvider(activity).get(ContestViewModel::class.java)
    }

    fun create(fragment: Fragment): ContestViewModel {
        return ViewModelProvider(fragment).get(ContestViewModel::class.java)

    }


}