package com.fp.devfantasypowerxi.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.response.MyTransactionHistoryData
import com.fp.devfantasypowerxi.app.api.response.MyTransactionHistoryResponse
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.utils.MyDividerItemDecoration
import com.fp.devfantasypowerxi.app.view.adapter.TransactionItemAdapter
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentTransactionsBinding
import retrofit2.Response
import javax.inject.Inject

// Created by Gaurav Minocha
class TransactionsFragment : Fragment() {
    @Inject
    lateinit var oAuthRestService: OAuthRestService
    lateinit var mainBinding: FragmentTransactionsBinding
    lateinit var mAdapter: TransactionItemAdapter
    private var transactionItems: ArrayList<MyTransactionHistoryData> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_transactions, container, false)
        // return inflater.inflate(R.layout.fragment_transactions, container, false)
        setupRecyclerView()
        return mainBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.getAppComponent()!!.inject(this@TransactionsFragment)
    }

    private fun setupRecyclerView() {
        getTransaction()
        mAdapter = TransactionItemAdapter(transactionItems)
        mainBinding.rvTransaction.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mainBinding.rvTransaction.setLayoutManager(mLayoutManager)
        // adding custom divider line with padding 3dp
        mainBinding.rvTransaction.addItemDecoration(
            MyDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                1
            )
        )
        mainBinding.rvTransaction.itemAnimator = DefaultItemAnimator()
        mainBinding.rvTransaction.adapter = mAdapter
    }

    private fun getTransaction() {
        mainBinding.refreshing = true
        val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<MyTransactionHistoryResponse> =
            oAuthRestService.getMyTransaction()
        myBalanceResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<MyTransactionHistoryResponse> {
            override fun success(response: Response<MyTransactionHistoryResponse>) {
                mainBinding.refreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val playingHistoryResponse: MyTransactionHistoryResponse =
                        response.body() ?: MyTransactionHistoryResponse()
                    if (playingHistoryResponse.status == 1 && playingHistoryResponse.result != null) {
                        if (playingHistoryResponse.result.data
                                .size > 0
                        ) {
                            transactionItems =
                                playingHistoryResponse.result.data
                            mainBinding.tvNoTransaction.visibility = View.GONE
                            mAdapter.updateData(transactionItems)
                        } else {
                            mainBinding.tvNoTransaction.setVisibility(View.VISIBLE)
                        }
                    } else {
                        mainBinding.tvNoTransaction.setVisibility(View.VISIBLE)
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response!= null) {
                    if (e.response.code() in 400..403) {
                        logout()
                    }
                }
            }
        })
    }

    fun logout() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.logout(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    logout()
                } else {
                    AppUtils.showError(
                        context as AppCompatActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

}