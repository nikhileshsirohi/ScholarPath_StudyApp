package com.example.scholarpath

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.compose.Green
import com.example.compose.Orange
import com.example.compose.Red
import com.example.scholarpath.Components.USER_NODE
import com.example.scholarpath.Components.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(val title: String, val color: Color, val value: Int) {
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: MEDIUM
    }
}

fun Long?.changeMillisToDateString(): String {
    val date: LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

fun NavigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(0)
        launchSingleTop = true
    }
}

@Composable
fun AlreadySignedIn(navController: NavController, vm: LCViewModel) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val context = LocalContext.current.applicationContext
    if (firebaseAuth.currentUser != null) {
        vm.ShowToast(context, "Logged In")
//        CurrentUser.user = getCurrentUserDetails()
        NavigateTo(navController, "Home_Screen")
    }
}

suspend fun getCurrentUserDetails(): UserData? {
    val currentUser = FirebaseAuth.getInstance().currentUser
    return if (currentUser != null) {
        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(USER_NODE).document(userId)

        try {
            val documentSnapshot = userRef.get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(UserData::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle exception
            null
        }
    } else {
        null
    }
}
