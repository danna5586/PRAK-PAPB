package com.tifd.projectcomposed.room

import androidx.lifecycle.LiveData

class TugasRepository(private val tugasDao: TugasDao) {
    val allTugas: LiveData<List<Tugas>> = tugasDao.getAllTugas()

    suspend fun insert(tugas: Tugas) {
        tugasDao.insertTugas(tugas)
    }

    suspend fun deleteTugasById(id: Int) {
        tugasDao.deleteTugasById(id)
    }
}
