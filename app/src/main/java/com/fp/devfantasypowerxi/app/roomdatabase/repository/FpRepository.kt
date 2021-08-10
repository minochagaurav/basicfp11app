package com.fp.devfantasypowerxi.app.roomdatabase.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.fp.devfantasypowerxi.app.dataModel.Temp
import com.fp.devfantasypowerxi.app.roomdatabase.db.FpDatabase


//Created by Gaurav Minocha on 26,March,2021
// repository data
class FpRepository(context: Context) {

    private var dbName: String = "fp_db"
    private var fpDatabase =Room.databaseBuilder(context, FpDatabase::class.java, dbName).build()

    fun getTasks(): LiveData<List<Temp>> {
        return fpDatabase.daoAccess().fetchAllTasks()
    }
}
