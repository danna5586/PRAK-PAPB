//package com.tifd.projectcomposed
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.launch
//
////class ListActivity : ComponentActivity() {
////    @OptIn(ExperimentalMaterial3Api::class)
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContent {
////            ProjectComposeDTheme {
////                Surface(
////                    modifier = Modifier.fillMaxSize(),
////                    color = MaterialTheme.colorScheme.background
////                ) {
////                    DataListScreen(onGitHubButtonClick = {
////                        val intent = Intent(this, ProfileActivity::class.java)
////                        startActivity(intent)
////                    })
////                }
////            }
////        }
////    }
////}
//
//@ExperimentalMaterial3Api
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun DataListScreen(onGitHubButtonClick: () -> Unit) {
//    val db = FirebaseFirestore.getInstance()
//    val context = LocalContext.current
//    var dataList by remember { mutableStateOf(listOf<DataModel>()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var isError by remember { mutableStateOf(false) }
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//
//    // Fetch data on component mount
//    LaunchedEffect(Unit) {
//        loadDataFromFirestore(db, { data ->
//            dataList = data
//            isLoading = false
//        }, {
//            isLoading = false
//            isError = true
//            coroutineScope.launch {
//                snackbarHostState.showSnackbar("Failed to load data.")
//            }
//        })
//    }
//
//    Scaffold(
//        snackbarHost = {
//            SnackbarHost(hostState = snackbarHostState)
//        },
//        topBar = {
//            TopAppBar(
//                title = { Text("ListActivity") },
//                actions = {
//                    IconButton(onClick = onGitHubButtonClick) {
//                        Icon(Icons.Default.AccountCircle, contentDescription = "GitHub Profile")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(innerPadding)) {
//            if (isLoading) {
//                LoadingView()
//            } else if (isError) {
//                ErrorView()
//            } else {
//                DataListView(dataList)
//            }
//        }
//    }
//}
//
//
//
//
