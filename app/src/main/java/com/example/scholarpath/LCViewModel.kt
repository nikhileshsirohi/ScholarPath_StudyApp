package com.example.scholarpath

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.scholarpath.Components.CurrentUser
import com.example.scholarpath.Components.Event
import com.example.scholarpath.Components.USER_NODE
import com.example.scholarpath.Components.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LCViewModel() : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    val inProgress = mutableStateOf(false)
    val eventMutabletate = mutableStateOf<Event<String>?>(null)
    val signIn = mutableStateOf(true)

    fun ShowToast(context: Context, message: String) {
        viewModelScope.launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun SignUp(
        context: Context,
        name: String,
        email: String,
        phone: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        inProgress.value = true;
        if (name.isEmpty() or email.isEmpty() or phone.isEmpty() or password.isEmpty()) {
            ShowToast(context, "Please fill all fields")
            return
        }
        db.collection(USER_NODE).whereEqualTo("email", email).get().addOnSuccessListener {
            if (it.isEmpty) {
                db.collection(USER_NODE).whereEqualTo("phone", phone).get().addOnSuccessListener {
                    if (it.isEmpty) {
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            if (it.isSuccessful) {
                                createProfile(name, email, phone)
                                onSuccess()
                                ShowToast(context, "Sign-up Successfully")
                            } else {
                                handleException(it.exception, customMessage = "Sign-up failed")
                                ShowToast(context, "Sign-up failed")
                            }
                        }
                    } else {
                        handleException(customMessage = "Phone Already Exist ")
                        ShowToast(context, "Phone already exist")
                        inProgress.value = false
                    }
                }
            } else {
                handleException(customMessage = "Email Already Exist ")
                ShowToast(context, "Email Already exist")
                inProgress.value = false
            }
        }
    }

    fun logIn(navController: NavController, context: Context, email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            ShowToast(context, "Please fill all the fields")
        } else {
            db.collection(USER_NODE).whereEqualTo("email", email).get().addOnSuccessListener {
                if (it.isEmpty) {
                    handleException(customMessage = "Email Not Exist ")
                    ShowToast(context, "Email Not exist")
                    inProgress.value = false
                } else {
                    inProgress.value = true
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            signIn.value = true
                            inProgress.value = false
                            val auth = FirebaseAuth.getInstance()
                            auth.currentUser?.uid?.let {
                                ShowToast(context, "Login Successful")
                                // Fetch user data
                                viewModelScope.launch {
                                    CurrentUser.user = getCurrentUserDetails()
//                            NavigateTo(navController, DestinationScreen.Home.screen)
                                    CurrentUser.user = getCurrentUserDetails()
                                    NavigateTo(navController, "Home_Screen")
                                }
                            }
                        } else {
                            inProgress.value = false
                            handleException(
                                exception = it.exception,
                                customMessage = "Login Failed"
                            )
                            ShowToast(context, "Login Failed")
                        }
                    }
                }
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("Chatting Clone App", "Chatting Clone Exception: ", exception)
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMessage else customMessage

        eventMutabletate.value = Event(message)
        inProgress.value = false

    }

    fun createProfile(name: String, email: String, phone: String) {
        inProgress.value = true
        val uid = auth.currentUser?.uid
        if (uid == null) {
            handleException(Exception("User not logged in"), "Can not retrieve user")
            inProgress.value = false
            return
        }

        val userData = UserData(
            userId = uid,
            name = name,
            phone = phone,
            email = email,
        )
        uid.let {
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // User already exists, do not update or create
                    handleException(Exception("User profile already exists"), "User profile already exists")
                    inProgress.value = false
                } else {
                    // User does not exist, create new profile
                    db.collection(USER_NODE).document(uid).set(userData)
                        .addOnSuccessListener {
                            inProgress.value = false
                        }
                        .addOnFailureListener { e ->
                            handleException(e, "Failed to create user profile")
                            inProgress.value = false
                        }
                }
            }.addOnFailureListener {
                handleException(it, "Can not retrieve user")
            }
        }
    }

    fun logOut() {
        auth.signOut()
        signIn.value = false
        eventMutabletate.value = Event("Logged Out")
    }

    fun resetPassword(email: String, onComplete: (String) -> Unit) {
        db.collection(USER_NODE).whereEqualTo("email", email).get().addOnSuccessListener {
            if (it.isEmpty) {
                handleException(customMessage = "Email Not Exist ")
                onComplete("Email does not exist")
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete("Check mail to reset password")
                        } else {
                            onComplete("Password reset failed")
                        }
                    }
            }
        }
    }
}
