package com.example.scholarpath.Components

import androidx.compose.ui.graphics.Color
import com.example.compose.gradient1
import com.example.compose.gradient2
import com.example.compose.gradient3
import com.example.compose.gradient4
import com.example.compose.gradient5

data class UserData(
    var userId: String? = "",
    var name: String? = "",
    var phone: String? = "",
    var email: String? = "",
    var dob: String? = "",
    var gender: String? = "",
    var myCourse:  List<String> = emptyList(),
    var collegeName: String? = "",
    var address: String? = ""
)

object CurrentUser {
    var user: UserData? = null
}

data class Subject(
    val name: String,
    val goalHour: Float,
    val color: List<Color>,
    val subjectId: Int
) {
    companion object {
        val subjectCardColors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}

data class Task(
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: Int,
    val relatedToSubject: String,
    val isComplete: Boolean,
    val taskSubjectId: Int,
    val taskId: Int
)

data class Session(
    val sessionSubjectId: Int,
    val relatedToSubject: String,
    val date: Long,
    val duration: Long,
    val sessionId: Int
)