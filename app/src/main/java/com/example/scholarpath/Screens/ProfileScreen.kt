package com.example.scholarpath.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scholarpath.Components.CurrentUser
import com.example.scholarpath.Components.UserData
import com.example.scholarpath.LCViewModel
import com.example.scholarpath.NavigateTo
import com.example.scholarpath.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, vm: LCViewModel){
    var selectedItem by remember { mutableStateOf(BottomNavigationMenu.PROFILE) }
    var isLoading by remember { mutableStateOf(true) }
    var userData by remember { mutableStateOf<UserData?>(UserData()) }
    var myCourse by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        userData = CurrentUser.user
        myCourse = userData?.myCourse ?: emptyList()
        isLoading = false
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(modifier = Modifier
                            .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                text = "ScholarPath",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit",
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable {
                                        navController.navigate("Update_Screen")
                                    })
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
    ){ paddingValue ->
        if (isLoading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Icon",
                        tint = Color.LightGray,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = {
                        vm.logOut()
                        NavigateTo(navController, "signup_screen")
                    })
                    {
                        Text(text = "Sign Out")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            userData?.name?.let { ProfileItem(label = "Name:", value = it) }
                            userData?.email?.let { ProfileItem(label = "Email:", value = it) }
                            userData?.phone?.let { ProfileItem(label = "Phone:", value = it) }
                            userData?.myCourse?.let { ProfileItem(label = "Courses:", value = it.joinToString(", ")) }
                            userData?.dob?.let { ProfileItem(label = "D.O.B:", value = it) }
                            userData?.gender?.let { ProfileItem(label = "Gender:", value = it) }
                            userData?.collegeName?.let { ProfileItem(label = "College:", value = it) }
                            userData?.address?.let { ProfileItem(label = "Address:", value = it) }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Icon(painter = painterResource(id = R.drawable.contact_us),
                        contentDescription = "Contact Us",
                        modifier = Modifier.clickable {
                            navController.navigate("Raise_Request_Screen")
                        }
                            .size(100.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 16.sp, textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth())
    }
    Divider(
            color = Color.Gray,
    thickness = 1.5.dp,
    modifier = Modifier.padding(bottom = 8.dp)
    )
}
