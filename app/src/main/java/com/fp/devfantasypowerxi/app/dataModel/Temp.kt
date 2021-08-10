package com.fp.devfantasypowerxi.app.dataModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


//Created by Gaurav Minocha on 26,March,2021

// model class for database data
@Entity
data class Temp(
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0,

    private var title: String = "",
    private var description: String = "",
    @ColumnInfo(name = "created_at")
    private var createdAt: Date? = null,
    @ColumnInfo(name = "modified_at")
    private var modifiedAt: Date? = null


)
