package com.example.scholarpath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme
import com.example.scholarpath.Screens.ContentScreen
import com.example.scholarpath.Screens.HomeScreen
import com.example.scholarpath.Screens.LectureNotesScreen
import com.example.scholarpath.Screens.LogInScreen
import com.example.scholarpath.Screens.MyCourseScreen
import com.example.scholarpath.Screens.PdfScreen
import com.example.scholarpath.Screens.ProfileScreen
import com.example.scholarpath.Screens.PurchasedSubjectScreen
import com.example.scholarpath.Screens.RaiseRequest
import com.example.scholarpath.Screens.SignUpScreen
import com.example.scholarpath.Screens.SubjectScreen
import com.example.scholarpath.Screens.UpdateProfile
import com.example.scholarpath.Screens.VideoPlayerScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val vm: LCViewModel = viewModel()
        NavHost(
            navController = navController,
            startDestination = "signup_screen",
            modifier = Modifier.padding(8.dp)
        ) {
            composable("Home_Screen") {
                HomeScreen(navController = navController)
            }
            composable("MYCourse_Screen") {
                MyCourseScreen(navController = navController)
            }
            composable("Profile_Screen") {
                ProfileScreen(navController = navController, vm = vm)
            }
            composable("Raise_Request_Screen") {
                RaiseRequest(navController = navController)
            }
            composable("signup_screen") {
                SignUpScreen(navController, vm)
            }
            composable("login_screen") {
                LogInScreen(navController, vm)
            }
            composable(route = "Update_Screen") {
                UpdateProfile(navController = navController)
            }

            composable(
                route = "Purchased_Course_screen/{course}",
                arguments = listOf(navArgument("course") { type = NavType.StringType })
            ) { backStackEntry ->
                val course = backStackEntry.arguments?.getString("course") ?: ""
                PurchasedSubjectScreen(navController, course)
            }
            composable(
                route = "subject_screen/{course}",
                arguments = listOf(navArgument("course") { type = NavType.StringType })
            ) { backStackEntry ->
                val course = backStackEntry.arguments?.getString("course") ?: ""
                SubjectScreen(navController, course)
            }
            composable(
                route = "lecture_note_screen/{path}",
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val decodedPath = URLDecoder.decode(backStackEntry.arguments?.getString("path"), StandardCharsets.UTF_8.toString()) ?: ""
                LectureNotesScreen(navController, decodedPath)
            }
            composable(
                route = "content_screen/{path}",
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val decodedPath = URLDecoder.decode(backStackEntry.arguments?.getString("path"), StandardCharsets.UTF_8.toString()) ?: ""
                ContentScreen(navController, decodedPath)
            }
            composable(
                route = "pdfView_screen/{path}",
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val decodedPath = URLDecoder.decode(backStackEntry.arguments?.getString("path"), StandardCharsets.UTF_8.toString()) ?: ""
                PdfScreen(navController, decodedPath)
            }
            composable(
                route = "videoPlayer_screen/{path}",
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val decodedPath = URLDecoder.decode(backStackEntry.arguments?.getString("path"), StandardCharsets.UTF_8.toString()) ?: ""
                VideoPlayerScreen(navController, decodedPath)
            }
        }
    }
}

