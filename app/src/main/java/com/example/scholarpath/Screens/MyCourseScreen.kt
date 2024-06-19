package com.example.scholarpath.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.gradient3
import com.example.compose.gradient5
import com.example.scholarpath.Components.CurrentUser
import com.example.scholarpath.Components.UserData
import com.example.scholarpath.fetchFirebaseStorageItems
import com.example.scholarpath.getCurrentUserDetails
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCourseScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf(BottomNavigationMenu.MyCOURSE) }
    var isLoading by remember { mutableStateOf(true) }
    var userData by remember { mutableStateOf<UserData?>(UserData()) }
    var myCourse by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        CurrentUser.user = getCurrentUserDetails()
        userData = CurrentUser.user
        myCourse = userData?.myCourse ?: emptyList()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.Blue),
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hi! ${userData?.name?.split(' ')?.get(0)}",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                        Divider(
                            color = Color.Gray,
                            thickness = 1.5.dp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Your Courses =>",
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                navController = navController,
                onItemSelected = { item ->
                    selectedItem = item
                }
            )
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
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
            if(myCourse.isEmpty()){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You have no courses. Please go to home to purchase.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }else {
                Text(
                    text = "Purchased Courses",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValue),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(myCourse.size) { index ->
                        val course = myCourse[index]
                        SubjectsCard(
                            subjectName = course,
                            gradientColor = gradient3,
                            onClick = { navController.navigate("Purchased_Course_screen/$course") }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasedSubjectScreen(navController: NavController, course: String) {
    var isLoading by remember { mutableStateOf(true) }
    var subjects by remember { mutableStateOf(emptyList<String>()) }
    LaunchedEffect(Unit) {
        val (folders, _, succeed) = fetchFirebaseStorageItems(course)
        subjects = folders
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
                                text = course,
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
                selectedItem = BottomNavigationMenu.HOME,
                navController = navController,
                onItemSelected = { }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subjects.size) { index ->
                        val subject = subjects[index]
                        val encodedPath =
                            URLEncoder.encode("$course/$subject", StandardCharsets.UTF_8.toString())
                        SubjectsCard(
                            subjectName = subject,
                            gradientColor = gradient5,
                            onClick = {
                                navController.navigate("lecture_note_screen/$encodedPath")
                            }
                        )
                    }
                }
            }
        }
    }
}