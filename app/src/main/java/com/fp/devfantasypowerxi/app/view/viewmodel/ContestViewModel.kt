package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.ContestRequest
import com.fp.devfantasypowerxi.app.api.response.ContestResponse
import com.fp.devfantasypowerxi.app.api.response.MatchListResponse
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import java.util.*
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


    /**
     * Expose the LiveData So that UI can observe it.
     */
    fun getContestData(): LiveData<Resource<ContestResponse>> {
        return contestLiveData
    }

    fun loadContestRequest(contestRequest: ContestRequest?) {
        contestRequestMutableLiveData.value = contestRequest
    }


    @Inject
    fun setRepository(repository: MatchRepository?) {
        mRepository = repository!!
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