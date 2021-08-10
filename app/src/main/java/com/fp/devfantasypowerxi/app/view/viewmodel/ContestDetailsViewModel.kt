package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.arch.core.util.Function
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import javax.inject.Inject


class ContestDetailsViewModel : ViewModel() {
    private lateinit var mRepository: MatchRepository
    private val networkInfoObservable: MutableLiveData<BaseRequest> =
        MutableLiveData<BaseRequest>()
/*

    private val contestRequestMutableLiveData: MutableLiveData<ContestRequest> =
        MutableLiveData<ContestRequest>()
    private val contestLiveData: LiveData<Resource<ContestDetailResponse>> =
        Transformations.switchMap(contestRequestMutableLiveData,
            Function<Any?, LiveData<Any?>> { input ->
                val resourceLiveData: LiveData<Resource<ContestDetailResponse>> =
                    mRepository.getContestDetails(input)
                val mediator: MediatorLiveData<Resource<ContestDetailResponse>?> =
                    MediatorLiveData<Resource<ContestDetailResponse>?>()
                mediator.addSource(resourceLiveData,
                    Observer<Any> { arrayListResource ->
                        val resp: ContestDetailResponse = arrayListResource.getData()
                        var response: Resource<ContestDetailResponse>? = null
                        when (arrayListResource.getStatus()) {
                            LOADING -> response = Resource.loading(null)
                            SUCCESS -> response = Resource.success(resp)
                            ERROR -> response =
                                Resource.error(arrayListResource.getException(), null)
                        }
                        mediator.setValue(response)
                    })
                mediator
            })
*/

    /**
     * Expose the LiveData So that UI can observe it.
     */
 /*   val contestData: LiveData<Any>
        get() = contestLiveData

    fun loadContestRequest(contestRequest: ContestRequest) {
        contestRequestMutableLiveData.setValue(contestRequest)
    }
*/

    @Inject
    fun setRepository(repository: MatchRepository) {
        mRepository = repository
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun create(activity: FragmentActivity): UpComingMatchListViewModel {

        return ViewModelProvider(activity).get(UpComingMatchListViewModel::class.java)
    }

    fun create(fragment: Fragment): UpComingMatchListViewModel {
        return ViewModelProvider(fragment).get(UpComingMatchListViewModel::class.java)

    }
}