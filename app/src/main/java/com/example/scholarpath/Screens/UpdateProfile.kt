package com.example.scholarpath.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scholarpath.Components.CurrentUser
import com.example.scholarpath.Components.USER_NODE
import com.example.scholarpath.Components.UserData
import com.example.scholarpath.getCurrentUserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfile(navController: NavController) {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf(BottomNavigationMenu.PROFILE) }
    var userData = CurrentUser.user ?: UserData()
    var name = remember { mutableStateOf(userData.name ?: "") }
    var phone = remember { mutableStateOf(userData.phone ?: "") }
    var email = remember { mutableStateOf(userData.email ?: "") }
    var DOB = remember { mutableStateOf(userData.dob ?: "") }
    var gender = remember { mutableStateOf(userData.gender ?: "") }
    var college = remember { mutableStateOf(userData.collegeName ?: "") }
    var address = remember { mutableStateOf(userData.address ?: "") }

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
                                text = "Update Profile",
                                style = MaterialTheme.typography.headlineMedium
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            readOnly = true,
                            label = { Text("Name:") }
                        )
                        TextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            readOnly = true,
                            label = { Text("Email:") }
                        )
                        TextField(
                            value = phone.value,
                            onValueChange = { phone.value = it },
                            readOnly = true,
                            label = { Text("Phone:") }
                        )
                        TextField(
                            value = DOB.value,
                            onValueChange = { newDOB ->
                                val (formattedText, newCursorPosition) = formatDOB(newDOB)
                                DOB.value = formattedText
                            },
                            label = { Text("D.O.B:") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        TextField(
                            value = gender.value,
                            onValueChange = {
                                gender.value = it
                            },
                            label = { Text(text = "Gender: ") }
                        )
                        TextField(
                            value = college.value,
                            onValueChange = { college.value = it },
                            label = { Text("College:") }
                        )
                        TextField(
                            value = address.value,
                            onValueChange = { address.value = it },
                            label = { Text("Address:") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        userData.dob = DOB.value
                        userData.gender = gender.value
                        userData.collegeName = college.value
                        userData.address = address.value
                        userData.let {
                            updateProfileInFirestore(it,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Update Successfully!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    navController.popBackStack()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.popBackStack()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

fun formatDOB(text: String): Pair<String, Int> {
    val cleaned = text.filter { it.isDigit() }
    val result = StringBuilder()
    var cursorPosition = cleaned.length
    for (i in cleaned.indices) {
        if (i == 2 || i == 4) {
            result.append('/')
            if (i <= cursorPosition) cursorPosition++
        }
        result.append(cleaned[i])
    }
    return Pair(result.toString(), cursorPosition)
}


fun updateProfileInFirestore(
    userData: UserData,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(USER_NODE).document(userId)

        // Prepare data to update
        val updates = mutableMapOf<String, Any?>()
        updates["dob"] = userData.dob
        updates["gender"] = userData.gender
        updates["collegeName"] = userData.collegeName
        updates["address"] = userData.address

        userRef.update(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    } else {
        onFailure(Exception("Current user is null"))
    }
}