package com.example.scholarpath

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

data class FirebaseFetchResult(
    val folderNames: List<String>,
    val fileNames: List<String>,
    val isSuccess: Boolean
)
suspend fun fetchFirebaseStorageItems(filePath: String): FirebaseFetchResult  {
    val storage = FirebaseStorage.getInstance()
    val storageRef = if (filePath.isEmpty()) {
        storage.reference // root directory
    } else {
        storage.reference.child(filePath)
    }

    return try {
        val listResult = storageRef.listAll().await()       //fetch all content
        val folderNames = listResult.prefixes.map { it.name } //fetch folders
        val fileNames = listResult.items.map { it.name }    //fetch files

        FirebaseFetchResult(
            folderNames = folderNames,
            fileNames = fileNames,
            isSuccess = true
        )
    } catch (e: Exception) {
        FirebaseFetchResult(
            folderNames = emptyList(),
            fileNames = emptyList(),
            isSuccess = false
        )
    }
}

suspend fun downloadPdfFromFirebase(
    context: Context,
    filePath: String
): Pair<File?, String> {
    return try {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child(filePath)

        val fileName = fileRef.name
        val localFile = withContext(Dispatchers.IO) {
            File(context.cacheDir, fileName).apply {
                createNewFile()
            }
        }
        fileRef.getFile(localFile).await()

        Pair(localFile, "PDF downloaded successfully: ${localFile.absolutePath}")
    } catch (e: Exception) {
        e.printStackTrace()
        Pair(null, "Failed to download PDF file: ${e.message}")
    }
}