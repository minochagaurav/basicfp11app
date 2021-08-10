package com.fp.devfantasypowerxi.app.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fp.devfantasypowerxi.app.dataModel.Temp

//Created by Gaurav Minocha on 26,March,2021

// use for database Query
@Dao
interface DaoAccess {
    @Insert
    fun insertTask(data: Temp): Long

    @Query("SELECT * FROM `Temp` ORDER BY created_at desc")
    fun fetchAllTasks(): LiveData<List<Temp>>

    @Query("SELECT * FROM `Temp` WHERE id =:taskId")
    fun getTask(taskId: Int): LiveData<Temp>

    @Update
    fun updateTask(note: Temp)

    @Delete
    fun deleteTask(note: Temp)
}