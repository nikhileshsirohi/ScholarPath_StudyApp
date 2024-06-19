package com.example.scholarpath.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scholarpath.fetchFirebaseStorageItems
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(navController: NavController, path: String) {
    var contentFile by remember { mutableStateOf(emptyList<String>()) }
    var contentFolder by remember { mutableStateOf(emptyList<String>()) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        val (folders, files, succeed) = fetchFirebaseStorageItems(path)
        contentFolder = folders
        contentFile = files
        isLoading = !succeed
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back",
                                modifier = Modifier.clickable { navController.popBackStack() })
                            Text(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                text = path,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                        Divider(
                            color = Color.Gray,
                            thickness = 1.5.dp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationMenu.MyCOURSE,
                navController = navController,
                onItemSelected = {}
            )
            Divider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    ) { paddingValue ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (contentFolder.isNotEmpty()) {
                    items(contentFolder.size) { index ->
                        val folder = contentFolder[index]
                        val encodedPath =
                            URLEncoder.encode("$path/$folder", StandardCharsets.UTF_8.toString())
                        LectureNotesList(
                            folder = folder,
                            file = "",
                            onItemClick = { navController.navigate("content_screen/$encodedPath") }
                        )
                        Divider(
                            color = Color.Gray,
                            thickness = 0.25.dp,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                }
                if (contentFile.isNotEmpty()) {
                    items(contentFile.size) { index ->
                        val file = contentFile[index]
                        val encodedPath =
                            URLEncoder.encode("$path/$file", StandardCharsets.UTF_8.toString())
                        if (file.endsWith(".mp4") || file.endsWith(".mkv")) {
                            LectureNotesList(
                                folder = "",
                                file = file,
                                onItemClick = { navController.navigate("videoPlayer_screen/$encodedPath") }
                            )
                        }else {
                            LectureNotesList(
                                folder = "",
                                file = file,
                                onItemClick = { navController.navigate("pdfView_screen/$encodedPath") }
                            )
                        }
                        Divider(
                            color = Color.Gray,
                            thickness = 0.25.dp,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContentListItem(content: String, subjectDirectory: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .clickable { }
    ) {
        Text(text = content, style = MaterialTheme.typography.bodyLarge)
        Divider(modifier = Modifier.padding(top = 8.dp, end = 8.dp))
    }
}