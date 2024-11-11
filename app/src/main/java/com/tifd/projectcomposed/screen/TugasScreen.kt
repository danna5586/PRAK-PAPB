package com.tifd.projectcomposed.screen

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
    var previewImage by remember { mutableStateOf(false) }
    val tugasList by viewModel.allTugas.observeAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) previewImage = true }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (previewImage) {
            // Fullscreen Camera Preview
            CameraPreviewView(
                context = context,
                imageCapture = imageCapture,
                cameraExecutor = cameraExecutor,
                onClose = { previewImage = false }
            )
        } else {
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Input Fields
                TextField(
                    value = namaMatkul,
                    onValueChange = { namaMatkul = it },
                    label = { Text("Nama Matkul") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = detailTugas,
                    onValueChange = { detailTugas = it },
                    label = { Text("Detail Tugas") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Placeholder for Camera Preview
                // Placeholder for Camera Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewImage) {
                        CameraPreviewView(
                            context = context,
                            imageCapture = imageCapture,
                            cameraExecutor = cameraExecutor
                        )
                    } else {
                        Text("Preview Gambar")
                    }
                }


                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
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

                // Tugas List
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
    }
}

@Composable
fun CameraPreviewView(
    context: Context,
    imageCapture: ImageCapture,
    cameraExecutor: ExecutorService,
    onClose: (() -> Unit)? = null // Menjadikan onClose opsional
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Take Picture and Close Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                takePicture(context, imageCapture)
            }) {
                Text("Take Picture")
            }
            if (onClose != null) {
                Button(
                    onClick = onClose
                ) {
                    Text("Close")
                }
            }
        }
    }
}


fun takePicture(context: Context, imageCapture: ImageCapture) {
    // Prepare file metadata
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    // Output options to save captured image
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    // Take picture and handle callbacks
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Picture saved!", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Failed to save picture: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun TugasItem(tugas: Tugas, onDeleteClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentSize(Alignment.CenterStart)
    ) {
        Text(text = "Nama Matkul: ${tugas.matkul}")
        Text(text = "Detail Tugas: ${tugas.detailTugas}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onDeleteClick) {
            Text("Delete")
        }
    }
}
