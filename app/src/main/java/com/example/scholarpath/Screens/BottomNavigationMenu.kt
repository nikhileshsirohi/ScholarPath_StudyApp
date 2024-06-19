package com.example.scholarpath.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scholarpath.R

/*
enum class BottomNavigationMenu(val icon: Int, val navDestination: DestinationScreen) {
    HOME(R.drawable.home, DestinationScreen.Home),
    COURSE(R.drawable.homework, DestinationScreen.MyCourse),
    PROFILE(R.drawable.user, DestinationScreen.Profile)
}

 */
enum class BottomNavigationMenu(val icon: Int, val screen: String) {
    HOME(R.drawable.home, "Home_Screen"),
    MyCOURSE(R.drawable.homework, "MYCourse_Screen"),
    PROFILE(R.drawable.user, "Profile_Screen")
}

@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavigationMenu,
    navController: NavController,
    onItemSelected: (BottomNavigationMenu) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 2.dp),
//            .background(Color.White),
        verticalAlignment = Alignment.Bottom,
    ) {
        BottomNavigationMenu.entries.forEach { item ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (item == selectedItem) Color.Gray else Color.Transparent)
                    .padding(4.dp)
                    .clickable {
                        onItemSelected(item)
                        navController.navigate(item.screen) {
                            popUpTo(0)
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        colorFilter = if (item == selectedItem) {
                            ColorFilter.tint(color = Color.Blue)
                        } else {
                            ColorFilter.tint(color = Color.Gray)
                        }
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}
