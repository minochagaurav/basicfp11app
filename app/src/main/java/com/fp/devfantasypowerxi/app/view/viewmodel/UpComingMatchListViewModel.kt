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
import java.util.*
import javax.inject.Inject

class UpComingMatchListViewModel : ViewModel() {
    private lateinit var mRepository: MatchRepository

    private val networkInfoObservable: MutableLiveData<BaseRequest> =
        MutableLiveData<BaseRequest>()

  private  val matchListData: LiveData<Resource<MatchListResponse>> =
        Transformations.switchMap(
            networkInfoObservable
        ) { input ->
            val resourceLiveData: LiveData<Resource<MatchListResponse>> =
                mRepository.getHomeDataList(
                )
            val mediator: MediatorLiveData<Resource<MatchListResponse>> =
                MediatorLiveData<Resource<MatchListResponse>>()
            mediator.addSource(resourceLiveData) { dataResponse ->
                val resp: MatchListResponse = dataResponse.data?:MatchListResponse()
                val response: Resource<MatchListResponse> = when (dataResponse.status) {
                    Resource.Status.LOADING -> Resource.loading(null)
                    Resource.Status.SUCCESS -> {
                        val matchListResponse = transform(resp)
                        Resource.success(matchListResponse)
                    }
                    Resource.Status.ERROR -> Resource.error(dataResponse.exception, null)
                }
                mediator.setValue(response)
            }
            mediator
        }

    @JvmName("getMatchListData1")
    fun getMatchListData(): LiveData<Resource<MatchListResponse>> {
        return matchListData
    }

    fun loadMatchListRequest(baseRequest: BaseRequest) {
        networkInfoObservable.value = baseRequest

    }

    @Inject
    fun setRepository(mRepository: MatchRepository) {
        this.mRepository = mRepository
    }


    fun clear() {
        networkInfoObservable.value = null
    }

    // Transform stockist order into flexible items.
    private fun transform(response: MatchListResponse): MatchListResponse {
        val matchListResponse = MatchListResponse()
        matchListResponse.status = response.status
        matchListResponse.message = response.message
        val matches: ArrayList<MatchListResult> = ArrayList<MatchListResult>()
        for (match in response.result) {
            if (match.match_status_key == Constants.KEY_UPCOMING_MATCH) matches.add(match)
        }
        matchListResponse.result = matches
        return matchListResponse
    }

    fun create(activity: FragmentActivity): UpComingMatchListViewModel {

        return ViewModelProvider(activity).get(UpComingMatchListViewModel::class.java)
    }

    fun create(fragment: Fragment): UpComingMatchListViewModel {
        return ViewModelProvider(fragment).get(UpComingMatchListViewModel::class.java)

    }
}