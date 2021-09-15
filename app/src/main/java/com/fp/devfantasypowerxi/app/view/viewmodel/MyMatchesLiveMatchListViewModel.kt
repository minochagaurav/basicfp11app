package com.fp.devfantasypowerxi.app.view.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.MatchListResponse
import com.fp.devfantasypowerxi.app.api.response.MatchListResult
import com.fp.devfantasypowerxi.app.repository.MatchRepository
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import java.util.ArrayList
import javax.inject.Inject

class MyMatchesLiveMatchListViewModel: ViewModel() {
    private lateinit var mRepository: MatchRepository
    private val searchKey: String? = null
    private val networkInfoObservable: MutableLiveData<BaseRequest> =
        MutableLiveData<BaseRequest>()

    /**
     * Expose the LiveData So that UI can observe it.
     */
    val searchData: LiveData<Resource<MatchListResponse>> =
        Transformations.switchMap(networkInfoObservable)
        { input ->
            val resourceLiveData: LiveData<Resource<MatchListResponse>> =
                mRepository.getMyMatchesList(input)
            val mediator: MediatorLiveData<Resource<MatchListResponse>?> =
                MediatorLiveData<Resource<MatchListResponse>?>()
            mediator.addSource(resourceLiveData
            ) { arrayListResource ->
                val resp: MatchListResponse = arrayListResource.data?: MatchListResponse()
                val response: Resource<MatchListResponse> = when (arrayListResource.status) {
                    Resource.Status.LOADING -> Resource.loading(null)
                    Resource.Status.SUCCESS -> {
                        val matchListResponse: MatchListResponse = transform(resp)
                        Resource.success(matchListResponse)
                    }
                    Resource.Status.ERROR -> Resource.error(arrayListResource.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }

    // Transform stockist order into flexible items.
    private fun transform(response: MatchListResponse): MatchListResponse {
        val matchListResponse = MatchListResponse()
        matchListResponse.status=response.status
        matchListResponse.message=response.message
        val matches: ArrayList<MatchListResult> = ArrayList<MatchListResult>()
        for (match in response.result) {
            if (match.match_status_key == Constants.KEY_LIVE_MATCH) matches.add(match)
        }
        matchListResponse.result=matches
        return matchListResponse
    }

    @Inject
    fun setRepository(repository: MatchRepository) {
        mRepository = repository
    }

    fun load(request: BaseRequest?) {
        networkInfoObservable.value = request
    }

    fun clear() {
        networkInfoObservable.value = null
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun create(activity: FragmentActivity): MyMatchesLiveMatchListViewModel {

        return ViewModelProvider(activity).get(MyMatchesLiveMatchListViewModel::class.java)
    }

    fun create(fragment: Fragment): MyMatchesLiveMatchListViewModel {
        return ViewModelProvider(fragment).get(MyMatchesLiveMatchListViewModel::class.java)

    }
}