package com.tifd.projectcomposed.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TugasViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TugasRepository
    val allTugas: LiveData<List<Tugas>>

    init {
        val tugasDao = TugasDatabase.getDatabase(application).tugasDao()
        repository = TugasRepository(tugasDao)
        allTugas = repository.allTugas
    }

    fun insert(tugas: Tugas) = viewModelScope.launch {
        repository.insert(tugas)
    }

    fun deleteTugasById(id: Int) = viewModelScope.launch {
        repository.deleteTugasById(id)
    }
}
