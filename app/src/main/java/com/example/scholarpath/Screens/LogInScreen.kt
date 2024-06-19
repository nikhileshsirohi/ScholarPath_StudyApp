package com.example.scholarpath.Screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scholarpath.Components.USER_NODE
import com.example.scholarpath.LCViewModel
import com.example.scholarpath.NavigateTo
import com.example.scholarpath.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LogInScreen(navController: NavController, vm: LCViewModel) {
    val context = LocalContext.current.applicationContext
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emailState = remember {
                mutableStateOf(TextFieldValue())
            }
            val passwordState = remember {
                mutableStateOf(TextFieldValue())
            }
            val focus = LocalFocusManager.current
            Image(
                painter =
                painterResource(id = R.drawable.scholar_path_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Sign In",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = emailState.value,
                onValueChange = {
                    emailState.value = it
                },
                label = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                },
                label = { Text(text = "Password") },
                modifier = Modifier.padding(8.dp),
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    vm.logIn(
                        navController,
                        context,
                        emailState.value.text,
                        passwordState.value.text
                    )
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "LogIn")
            }

            Text(text = "New user ? SignUp - >",
                color = Color.Red,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        NavigateTo(navController, "signup_screen")
                    }
            )
            Text(text = "Forget Password",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        if (emailState.value.text.isNotEmpty()) {
                            vm.resetPassword(email = emailState.value.text) { result ->
                                Toast
                                    .makeText(
                                        context,
                                        result,
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "Please Enter the email",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
            )
        }
    }
}

