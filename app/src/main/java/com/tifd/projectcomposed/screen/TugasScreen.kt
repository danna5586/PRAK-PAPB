package com.tifd.projectcomposed.screen

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tifd.projectcomposed.room.Tugas
import com.tifd.projectcomposed.room.TugasViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun TugasScreen(viewModel: TugasViewModel = viewModel()) {
    var namaMatkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showCameraPreview by remember { mutableStateOf(false) }

    val tugasList by viewModel.allTugas.observeAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Executor for CameraX
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            showCameraPreview = false
            imageUri?.let { uri ->
                // You can load the image into a preview or save it as needed
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = namaMatkul,
            onValueChange = { namaMatkul = it },
            label = { Text("Nama Matkul") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = detailTugas,
            onValueChange = { detailTugas = it },
            label = { Text("Detail Tugas") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                val uri = createImageFileUri(context)
                imageUri = uri
                uri?.let { cameraLauncher.launch(it) }
                showCameraPreview = true // Show camera preview when the Camera button is pressed
            }) {
                Text("Camera")
            }

            Button(onClick = {
                coroutineScope.launch {
                    val tugas = Tugas(
                        matkul = namaMatkul,
                        detailTugas = detailTugas
                    )
                    viewModel.insert(tugas)
                    namaMatkul = ""
                    detailTugas = ""
                }
            }) {
                Text("Add")
            }
        }

        // Display the live camera preview below "Detail Tugas"
        if (showCameraPreview) {
            CameraPreviewView(context = context, cameraExecutor = cameraExecutor)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tugasList) { tugas ->
                TugasItem(tugas = tugas, onDeleteClick = {
                    coroutineScope.launch {
                        viewModel.deleteTugasById(tugas.id)
                    }
                })
            }
        }
    }
}

@Composable
fun CameraPreviewView(context: Context, cameraExecutor: ExecutorService) {
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, cameraExecutor)

            previewView
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Adjust height as needed
    )
}

fun createImageFileUri(context: Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun TugasItem(tugas: Tugas, onDeleteClick: () -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Nama Matkul: ${tugas.matkul}")
        Text(text = "Detail Tugas: ${tugas.detailTugas}")
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onDeleteClick) {
            Text("Delete")
        }
    }
}

//fun TugasScreen (){
//}