package com.fp.devfantasypowerxi.app.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.TransactionRequest
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
import com.fp.devfantasypowerxi.databinding.ActivityTransactionsBinding
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class TransactionsActivity : AppCompatActivity() {
    var mainBinding: ActivityTransactionsBinding? = null

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    var mAdapter: TransactionItemAdapter? = null
    var transactionItems: ArrayList<MyTransactionHistoryData> =
        ArrayList<MyTransactionHistoryData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_transactions)
        MyApplication.getAppComponent()!!.inject(this@TransactionsActivity)
        initialize()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        transaction
        mAdapter = TransactionItemAdapter(transactionItems)
        mainBinding!!.rvTransaction.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@TransactionsActivity)
        mainBinding!!.rvTransaction.layoutManager = mLayoutManager
        // adding custom divider line with padding 3dp
        mainBinding!!.rvTransaction.addItemDecoration(MyDividerItemDecoration(this@TransactionsActivity,
            LinearLayoutManager.VERTICAL,
            1))
        mainBinding!!.rvTransaction.itemAnimator = DefaultItemAnimator()
        mainBinding!!.rvTransaction.adapter = mAdapter
    }

    private val transaction: Unit
        private get() {
            mainBinding!!.refreshing = true
            val baseRequest = TransactionRequest()
            baseRequest.page = 1
            baseRequest.user_id =
                MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)
                    .toString()
            val myBalanceResponseCustomCall: CustomCallAdapter.CustomCall<MyTransactionHistoryResponse> =
                oAuthRestService.getMyTransaction()
            myBalanceResponseCustomCall.enqueue(object :
                CustomCallAdapter.CustomCallback<MyTransactionHistoryResponse> {
                override fun success(response: Response<MyTransactionHistoryResponse>) {
                    mainBinding!!.refreshing = false
                    if (response.isSuccessful() && response.body() != null) {
                        val playingHistoryResponse: MyTransactionHistoryResponse? = response.body()
                        if (playingHistoryResponse!!.status == 1 && playingHistoryResponse.result != null) {
                            if (playingHistoryResponse.result.data
                                    .size > 0
                            ) {
                                transactionItems =
                                    playingHistoryResponse.result.data
                                mainBinding!!.tvNoTransaction.visibility = View.GONE
                                mAdapter!!.updateData(transactionItems)
                            } else {
                                mainBinding!!.tvNoTransaction.visibility = View.VISIBLE
                            }
                        } else {
                            mainBinding!!.tvNoTransaction.visibility = View.VISIBLE
                        }
                    }
                }

                override fun failure(e: ApiException?) {
                    mainBinding!!.refreshing = false
                    e!!.printStackTrace()
                    if (e.response != null && e.response.code() >= 400 && e.response
                            .code() < 404
                    ) {
                        logout()
                    }
                }
            })
        }


    fun logout() {
        mainBinding!!.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.logout(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding!!.refreshing = false
                val updateProfileResponse: NormalResponse = response.body()!!
                if (updateProfileResponse.status == 1) {
                    logout()
                } else {
                    AppUtils.showError(
                        this@TransactionsActivity,
                        updateProfileResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding!!.refreshing = false
            }
        })
    }

    private fun initialize() {
        setSupportActionBar(mainBinding!!.mytoolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Transactions"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}