package com.fp.devfantasypowerxi.app.roomdatabase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fp.devfantasypowerxi.app.dataModel.Temp
import com.fp.devfantasypowerxi.app.roomdatabase.dao.DaoAccess

//Created by Gaurav Minocha on 26,March,2021

// create roomDataBase
@Database(entities = [Temp::class], version = 1, exportSchema = false)
public abstract class FpDatabase: RoomDatabase() {
    abstract fun daoAccess(): DaoAccess
}