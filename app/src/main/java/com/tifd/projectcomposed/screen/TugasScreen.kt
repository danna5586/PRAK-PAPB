package com.tifd.projectcomposed.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tifd.projectcomposed.room.Tugas
import com.tifd.projectcomposed.room.TugasViewModel

@Composable
fun TugasScreen(
    navController: NavHostController,
    tugasViewModel: TugasViewModel,
    modifier: Modifier = Modifier
) {
    var namaMatkul by remember { mutableStateOf("") }
    var tugasDetail by remember { mutableStateOf("") }

    // Observe the list of Tugas from the ViewModel
    val allTugas by tugasViewModel.allTugas.observeAsState(listOf())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = namaMatkul,
            onValueChange = { namaMatkul = it },
            label = { Text("Nama Matkul") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tugasDetail,
            onValueChange = { tugasDetail = it },
            label = { Text("Tugas") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            if (namaMatkul.isNotBlank() && tugasDetail.isNotBlank()) {
                val newTugas = Tugas(
                    matkul = namaMatkul,
                    detailTugas = tugasDetail
                )
                tugasViewModel.insert(newTugas)
                namaMatkul = ""
                tugasDetail = ""
            }
        }) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allTugas) { tugasItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "ID: ${tugasItem.id}")
                        Text(text = "Matkul: ${tugasItem.matkul}")
                        Text(text = "Tugas: ${tugasItem.detailTugas}")
                    }
                }
            }
        }
    }
}
//fun TugasScreen (){
//}