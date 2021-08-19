package com.fp.devfantasypowerxi.app.api.response

import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R

data class MyTransactionHistoryResponse(
    val result: MyTransactionHistoryResult? = MyTransactionHistoryResult(),
    val status: Int = 0
)

data class MyTransactionHistoryResult(
    val current_page: Int = 0,
    val `data`: ArrayList<MyTransactionHistoryData> = ArrayList(),
    val per_page: Int = 0,
    val total: Int = 0
)

data class MyTransactionHistoryData(
    val add_amount: String = "",
    val amount: String = "",
    val available: String = "",
    val challengename: String = "",
    val created: String = "",
    val date: String = "",
    val deduct_amount: String = "",
    val id: Int = 0,
    val matchname: String = "",
    val paymentstatus: String = "",
    val teamname: String = "",
    val tour: String = "",
    val transaction_by: String = "",
    val transaction_id: String = "",
    val transaction_type: String = "",
    val username: String = ""
){
    fun getTypeTransactionAmount(): String {
        return if (deduct_amount.toDouble() > 0) {
            "- ₹$deduct_amount"
        } else {
            "+ ₹$add_amount"
        }
    }

    fun getColorCode(): Int {
        return if (deduct_amount.toDouble() > 0) {
            MyApplication.appContext!!.resources.getColor(R.color.green_color)
        } else {
            MyApplication.appContext!!.resources.getColor(R.color.colorPrimary)
        }
    }

}