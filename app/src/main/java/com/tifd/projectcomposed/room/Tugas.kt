package com.tifd.projectcomposed.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tugas_table")
data class Tugas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val matkul: String,
    val detailTugas:String
)