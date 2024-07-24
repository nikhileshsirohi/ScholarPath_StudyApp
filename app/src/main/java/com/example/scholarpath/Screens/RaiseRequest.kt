package com.example.scholarpath.Screens

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scholarpath.Components.CurrentUser
import com.example.scholarpath.Components.USER_NODE
import com.example.scholarpath.Components.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

data class Query(
    val userName: String,
    val queryText: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseRequest(navController: NavController) {
    var selectedItem by remember { mutableStateOf(BottomNavigationMenu.PROFILE) }
    val context = LocalContext.current
    val userData = CurrentUser.user ?: UserData()
    var text by remember { mutableStateOf("") }
    val isSubmitEnabled by remember {
        derivedStateOf { text.length >= 10 }
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
                                text = "Contact us",
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
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Write your query (min 10 words, max 80 words)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .size(225.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                maxLines = 10
            )
            Button(
                onClick = {
                    userData.userId?.let { userID ->
                        uploadQuery(userId = userID, queryText = text){
                            isSuccess ->
                            if(isSuccess){
                                Toast.makeText(context, "Query posted successfully", Toast.LENGTH_SHORT)
                                    .show()
                                navController.popBackStack()
                            }else{
                                Toast.makeText(context, "Error: Query not posted", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                },
                enabled = isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }

        }
    }
}

fun uploadQuery(
    userId: String,
    queryText: String,
    onComplete: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("QUERIES").document(userId)

    userRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result
            if (document != null && document.exists()) {
                // Document exists, update the Queries array
                userRef.update("Queries", FieldValue.arrayUnion(queryText))
                    .addOnSuccessListener {
                        Log.d(TAG, "Document updated successfully")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.w(TAG, "Error updating document", e)
                        onComplete(false)
                    }
            } else {
                // Document does not exist, create it with the initial query
                val newUserData = hashMapOf(
                    "Queries" to arrayListOf(queryText)
                )
                userRef.set(newUserData)
                    .addOnSuccessListener {
                        Log.d(TAG, "Document created successfully")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.w(TAG, "Error creating document", e)
                        onComplete(false)
                    }
            }
        } else {
            // Handle failure to retrieve the document
            Log.w(TAG, "Error getting document", task.exception)
            onComplete(false)
        }
    }
}
