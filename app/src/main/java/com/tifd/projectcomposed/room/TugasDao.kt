package com.tifd.projectcomposed.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface TugasDao {
    @Insert
    suspend fun insertTugas(tugas: Tugas)

    @Query("SELECT * FROM tugas_table ORDER BY id DESC")
    fun getAllTugas(): LiveData<List<Tugas>>

    @Query("DELETE FROM tugas_table WHERE id = :id")
    suspend fun deleteTugasById(id:Int)
}