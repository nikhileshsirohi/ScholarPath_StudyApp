package com.example.scholarpath.Screens

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.gradient1
import com.example.scholarpath.Components.CurrentUser
import com.example.scholarpath.Components.USER_NODE
import com.example.scholarpath.Components.UserData
import com.example.scholarpath.fetchFirebaseStorageItems
import com.example.scholarpath.getCurrentUserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(navController: NavController, course: String) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var subjects by remember { mutableStateOf(emptyList<String>()) }
    var userData by remember { mutableStateOf<UserData?>(null) }

    LaunchedEffect(Unit) {
        userData = CurrentUser.user
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
                selectedItem = BottomNavigationMenu.MyCOURSE,
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
                val myCourse = userData?.myCourse ?: emptyList()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subjects.size) { index ->
                        val subject = subjects[index]
                        val isCourseAdded = course in myCourse
                        SubjectsCard(
                            subjectName = subject,
                            gradientColor = gradient1,
                            onClick = {
                                if (isCourseAdded) {
                                    Toast.makeText(context, "Go to MyCourse", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please Add to Course",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        )
                    }
                }
                if (course in myCourse) {
                    Text(
                        text = "Already Added",
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp, bottom = 5.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            addCourseToFirestore(course) {
                                navController.navigate("MYCourse_Screen")
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(top = 10.dp, bottom = 5.dp)
                    ) {
                        Text(text = "Add to Course")
                    }
                }
            }
        }
    }
}

fun addCourseToFirestore(coursePath: String, onSuccess: () -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(USER_NODE).document(userId)

        userRef.update("myCourse", FieldValue.arrayUnion(coursePath))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.w(TAG, "Error updating document", e)
            }
    }
}


/*
@Composable
private fun SubjectsCardSection(
    navController: NavController,
    modifier: Modifier,
    subjectList: List<String>,
    emptyListText: String = "You don't have any subjects.\nClick the + button to add new subject.",
    course: String,
    onAddItemClicked: () -> Unit
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = onAddItemClicked) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subjects")
            }

        }
        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.book),
                contentDescription = emptyListText
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) { subject ->
//                SubjectsCard(
//                    subjectName = subject,
//                    gradientColors = gradient1,
//                    onClick = {
//                        val route = "lecture_notes_screen/$course/$subject"
//                        navController.navigate(route)
//                    }
//                )
            }
        }
    }
}
*/