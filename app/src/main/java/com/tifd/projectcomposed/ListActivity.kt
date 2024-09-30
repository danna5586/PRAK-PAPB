package com.tifd.projectcomposed

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.firestore.FirebaseFirestore
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme
import kotlinx.coroutines.launch

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DataListScreen()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DataListScreen() {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var dataList by remember { mutableStateOf(listOf<DataModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Fetch data on component mount
    LaunchedEffect(Unit) {
        loadDataFromFirestore(db, { data ->
            dataList = data
            isLoading = false
        }, {
            isLoading = false
            isError = true
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Failed to load data.")
            }
        })
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        if (isLoading) {
            LoadingView()
        } else if (isError) {
            ErrorView()
        } else {
            DataListView(dataList)
        }
    }
}

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error loading data", color = Color.Red, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DataListView(dataList: List<DataModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dataList) { data ->
            DataCard(data)
        }
    }
}

fun loadDataFromFirestore(
    db: FirebaseFirestore,
    onSuccess: (List<DataModel>) -> Unit,
    onFailure: () -> Unit
) {
    db.collection("jadwal-kuliah")
        .get()
        .addOnSuccessListener { result ->
            val items = result.documents.mapNotNull { document ->
                try {
                    DataModel(
                        mata_kuliah = document.getString("mata_kuliah") ?: "-",
                        hari = Hari.safeValueOf(document.getString("hari")),
                        jam_mulai = document.getString("jam_mulai") ?: "-",
                        jam_selesai = document.getString("jam_selesai") ?: "-",
                        ruang = document.getString("ruang") ?: "-"
                    )
                } catch (e: Exception) {
                    null // Handle potential conversion errors
                }
            }
            onSuccess(items.sortedWith(
                compareBy<DataModel> { it.hari.urutan }
                    .thenBy { it.jam_mulai }
            ))
        }
        .addOnFailureListener {
            onFailure()
        }
}

@Composable
fun DataCard(data: DataModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.mata_kuliah,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                // Menampilkan hari sebagai teks
                Text(
                    text = data.hari.name.capitalize(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${data.jam_mulai} - ${data.jam_selesai}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ruang: ${data.ruang}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


data class DataModel(
    val mata_kuliah: String,
    val hari: Hari,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String
)

enum class Hari(val urutan: Int) {
    SENIN(1),
    SELASA(2),
    RABU(3),
    KAMIS(4),
    JUMAT(5),
    SABTU(6),
    MINGGU(7);

    companion object {
        fun safeValueOf(value: String?): Hari {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: SENIN
        }
    }
}
