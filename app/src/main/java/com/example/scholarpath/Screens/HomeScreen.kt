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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.example.compose.gradient2
import com.example.scholarpath.Components.CurrentUser
import com.example.scholarpath.Components.UserData
import com.example.scholarpath.LCViewModel
import com.example.scholarpath.R
import com.example.scholarpath.fetchFirebaseStorageItems
import com.example.scholarpath.getCurrentUserDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf(BottomNavigationMenu.HOME) }
    var courses by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var userData by remember { mutableStateOf<UserData?>(UserData()) }

    LaunchedEffect(Unit) {
        CurrentUser.user = getCurrentUserDetails()
        val (folders, _ , succeed) = fetchFirebaseStorageItems("")
        courses = folders
        isLoading = !succeed
        userData = CurrentUser.user
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(courses.size) { index ->
                    val subject = courses[index]
                    SubjectsCard(
                        subjectName = subject,
                        gradientColor = gradient2,
                        onClick = { navController.navigate("subject_screen/$subject") }
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectsCard(
    modifier: Modifier = Modifier,
    subjectName: String,
    gradientColor: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .clickable { onClick() }
            .background(
                brush = Brush.verticalGradient(gradientColor),
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.book),
                contentDescription = subjectName,
                Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = subjectName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}
