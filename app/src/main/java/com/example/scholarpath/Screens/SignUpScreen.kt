package com.example.scholarpath.Screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scholarpath.AlreadySignedIn
import com.example.scholarpath.LCViewModel
import com.example.scholarpath.NavigateTo
import com.example.scholarpath.R

@Composable
fun SignUpScreen(navController: NavController, vm: LCViewModel) {
    AlreadySignedIn(navController = navController, vm = vm)
    val context = LocalContext.current.applicationContext
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val phoneState = remember { mutableStateOf(TextFieldValue()) }
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val passwordMatch = passwordState.value == confirmPasswordState.value

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
            Image(
                painter =
                painterResource(id = R.drawable.scholar_path_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .padding(top = 16.dp)
            )
            Text(
                text = "SignUp",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = nameState.value,
                onValueChange = {
                    nameState.value = it
                },
                label = { Text(text = "Name") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = phoneState.value,
                onValueChange = {
                    phoneState.value = it
                },
                label = { Text(text = "Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                visualTransformation = PasswordVisualTransformation(),
            )
            OutlinedTextField(
                value = confirmPasswordState.value,
                onValueChange = {
                    confirmPasswordState.value = it
                },
                label = { Text(text = "Confirm Password") },
                modifier = Modifier.padding(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                isError = !passwordMatch
            )
            Button(
                onClick = {
                    if(passwordMatch) {
                        vm.SignUp(
                            context = context,
                            name = nameState.value.text,
                            email = emailState.value.text,
                            phone = phoneState.value.text,
                            password = passwordState.value
                        ){
                            NavigateTo(navController, "Home_Screen")
                        }
                        passwordState.value = ""
                        confirmPasswordState.value = ""
                    }
                },
                modifier = Modifier.padding(8.dp),
                enabled = passwordMatch
            ) {
                Text(text = "SignUp")
            }

            Text(text = "Already a user ? Login - >",
                color = Color.Red,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
//                        navController.navigate("login_screen")
                        NavigateTo(navController, "login_screen")
                    }
            )
        }
    }
}