package com.example.scholarpath.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scholarpath.R
import com.example.scholarpath.fetchFirebaseStorageItems
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectureNotesScreen(navController: NavController, path: String) {
    var isLoading by remember { mutableStateOf(true) }
    var lectureNotes by remember { mutableStateOf(emptyList<String>()) }
    var file by remember { mutableStateOf(emptyList<String>()) }
    LaunchedEffect(Unit) {
        val (folders, files, succeed) = fetchFirebaseStorageItems(path)
        lectureNotes = folders
        file = files
        isLoading = !succeed
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.Blue),
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
                onItemSelected = {  }
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
                if(lectureNotes.isNotEmpty()) {
                    items(lectureNotes.size) { index ->
                        val subject = lectureNotes[index]
                        val encodedPath =
                            URLEncoder.encode("$path/$subject", StandardCharsets.UTF_8.toString())
                        LectureNotesList(
                            folder = subject,
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
                if(file.isNotEmpty()) {
                    items(file.size) { index ->
                        val fileName = file[index]
                        val encodedPath =
                            URLEncoder.encode("$path/$fileName", StandardCharsets.UTF_8.toString())
                        if (fileName.endsWith(".mp4") || fileName.endsWith(".mkv")) {
                            LectureNotesList(
                                folder = "",
                                file = fileName,
                                onItemClick = { navController.navigate("videoPlayer_screen/$encodedPath") }
                            )
                        }else {
                            LectureNotesList(
                                folder = "",
                                file = fileName,
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
fun LectureNotesList(
    folder: String,
    file: String,
    onItemClick: () -> Unit
) {
    if (folder.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onItemClick() }
        ) {
            Image(
                painter = painterResource(R.drawable.folder),
                contentDescription = "Folder Icon",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = folder, style = MaterialTheme.typography.titleLarge)
        }
    }
    if (file.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onItemClick() }
        ) {
            val iconResId = when {
                file.endsWith(".jpg") -> R.drawable.picture
                file.endsWith(".png") -> R.drawable.picture
                file.endsWith(".jpeg") -> R.drawable.picture
                file.endsWith(".pdf") -> R.drawable.pdf
                file.endsWith(".mp4") -> R.drawable.video
                file.endsWith(".pptx") -> R.drawable.ppt
                file.endsWith(".ppt") -> R.drawable.ppt
                file.endsWith(".xlsx") -> R.drawable.excel
                file.endsWith(".xls") -> R.drawable.excel

                else -> R.drawable.txt
            }

            Image(
                painter = painterResource(iconResId),
                contentDescription = "File Icon",
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = file, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
