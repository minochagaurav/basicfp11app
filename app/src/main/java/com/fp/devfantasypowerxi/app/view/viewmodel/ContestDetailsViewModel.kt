package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.ContestDetailResponse
import com.fp.devfantasypowerxi.app.api.response.JoinedContestResponse
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject


open class ContestDetailsViewModel : ViewModel() {
    private lateinit var mRepository: MatchRepository
    private val networkInfoObservable: MutableLiveData<BaseRequest> =
        MutableLiveData<BaseRequest>()

    private val contestRequestMutableLiveData: MutableLiveData<ContestRequest> =
        MutableLiveData<ContestRequest>()
    private val contestLiveData: LiveData<Resource<ContestDetailResponse>> =
        Transformations.switchMap(
            contestRequestMutableLiveData
        ) { input ->
            val resourceLiveData: LiveData<Resource<ContestDetailResponse>> =
                mRepository.getContestDetails(input)
            val mediator: MediatorLiveData<Resource<ContestDetailResponse>?> =
                MediatorLiveData<Resource<ContestDetailResponse>?>()
            mediator.addSource(
                resourceLiveData
            ) { arrayListResource ->
                val resp: ContestDetailResponse = arrayListResource.data?: ContestDetailResponse()
                var response: Resource<ContestDetailResponse>? = null
                when (arrayListResource.status) {
                    Resource.Status.LOADING -> response = Resource.loading(null)
                    Resource.Status.SUCCESS -> response = Resource.success(resp)
                    Resource.Status.ERROR -> response = Resource.error(arrayListResource.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }

    private val joinedContestRequestMutableLiveData = MutableLiveData<JoinContestRequest>()
    private val joinedContestLiveData: LiveData<Resource<JoinedContestResponse>> =
        Transformations.switchMap(
            joinedContestRequestMutableLiveData
        ) {input ->
            val resourceLiveData: LiveData<Resource<JoinedContestResponse>> =
                mRepository.joinedContestList(input)
            val mediator: MediatorLiveData<Resource<JoinedContestResponse>> =
                MediatorLiveData<Resource<JoinedContestResponse>>()
            mediator.addSource(
                resourceLiveData
            ) { arrayListResource ->
                val resp: JoinedContestResponse = arrayListResource.data?: JoinedContestResponse()
                var response: Resource<JoinedContestResponse>? = null
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
    /*val contestData: LiveData<Any>
        get() = contestLiveData
*/
    open fun getContestData(): LiveData<Resource<ContestDetailResponse>> {
        return contestLiveData
    }

    fun loadContestRequest(contestRequest: ContestRequest) {
        contestRequestMutableLiveData.value = contestRequest
    }
    open fun getJoinedContestData(): LiveData<Resource<JoinedContestResponse>> {
        return joinedContestLiveData
    }

    open fun loadJoinedContestRequest(joinContestRequest: JoinContestRequest) {
        joinedContestRequestMutableLiveData.value = joinContestRequest
    }


    @Inject
    fun setRepository(repository: MatchRepository) {
        mRepository = repository
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun create(activity: FragmentActivity): ContestDetailsViewModel {

        return ViewModelProvider(activity).get(ContestDetailsViewModel::class.java)
    }

    fun create(fragment: Fragment): ContestDetailsViewModel {
        return ViewModelProvider(fragment).get(ContestDetailsViewModel::class.java)

    }
}