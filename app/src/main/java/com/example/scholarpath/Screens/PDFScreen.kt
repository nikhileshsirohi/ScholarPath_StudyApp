package com.example.scholarpath.Screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.scholarpath.downloadPdfFromFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale


@Composable
fun PdfScreen(navController: NavController, pdfFilePath: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var FirebaseMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pdfFilePath) {
        coroutineScope.launch(Dispatchers.IO) {
            val (downloadedFile, firebaseMsg) = downloadPdfFromFirebase(context, pdfFilePath)
            FirebaseMessage = firebaseMsg
            withContext(Dispatchers.Main) {
                if (downloadedFile != null) {
                    pdfFile = downloadedFile
//                    Message = openPdfFile(context, downloadedFile)
                } else {
                    errorMessage = "Failed to download PDF file."
                }
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Text(text = "Downloading file...", modifier = Modifier.align(Alignment.Center))
            }

            pdfFile != null -> {
                Column {
                    Row {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back",
                            modifier = Modifier.clickable { navController.popBackStack() })
                        Text(
                            modifier = Modifier.padding(horizontal = 40.dp),
                            text = pdfFilePath,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                    Divider()
                    OpenFile(pdfFile.toString())
                }
            }
            else -> {
                Text(text = errorMessage ?: "An error occurred.")
            }
        }
    }

}

@Composable
fun OpenFile(filePath: String) {
    val context = LocalContext.current
    val file = File(filePath)
    when (file.extension.toLowerCase(Locale.ROOT)) {
        "pdf" -> openPdfFile(context, file)
        "pptx", "ppt" -> openPptxFile(context, file)
        "jpg", "jpeg", "png" -> openImageFile(context, file)
        "txt" -> openTextFile(context, file)
        "xlsx", "xls" -> openExcelFile(context, file)
        "mkv", "mp4" -> openVideoFile(context, file)

        else -> {
            // Handle other file types or show an error message
            Toast.makeText(context, "Unsupported file type", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openPdfFile(context: Context, file: File): String? {
    val msg = "PdfScreen: Opening PDF file: ${file.absolutePath}"
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    return try {
        context.startActivity(intent)
        msg
    } catch (e: Exception) {
        e.printStackTrace()
        "Error opening PDF file: ${e.message}"
    }
}

private fun openPptxFile(context: Context, file: File): String? {
    val msg = "Opening PPTX file: ${file.absolutePath}"
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.openxmlformats-officedocument.presentationml.presentation")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    return try {
        context.startActivity(intent)
        msg
    } catch (e: Exception) {
        e.printStackTrace()
        "Error opening PPTX file: ${e.message}"
    }
}

private fun openImageFile(context: Context, file: File): String? {
    val msg = "Opening image file: ${file.absolutePath}"
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    return try {
        context.startActivity(intent)
        msg
    } catch (e: Exception) {
        e.printStackTrace()
        "Error opening image file: ${e.message}"
    }
}

private fun openTextFile(context: Context, file: File): String? {
    val msg = "Opening text file: ${file.absolutePath}"
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "text/plain")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    return try {
        context.startActivity(intent)
        msg
    } catch (e: Exception) {
        e.printStackTrace()
        "Error opening text file: ${e.message}"
    }
}

private fun openExcelFile(context: Context, file: File): String? {
    val msg = "Opening Excel file: ${file.absolutePath}"
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    return try {
        context.startActivity(intent)
        msg
    } catch (e: Exception) {
        e.printStackTrace()
        "Error opening Excel file: ${e.message}"
    }
}

private fun openVideoFile(context: Context, file: File): String? {
    val msg = "Opening video file: ${file.absolutePath}"
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "video/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    return try {
        context.startActivity(intent)
        msg
    } catch (e: Exception) {
        e.printStackTrace()
        "Error opening video file: ${e.message}"
    }
}


//not usable
//@Composable
//private fun PdfViewer(pdfFile: File) {
//    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
//    var pageBitmaps by remember { mutableStateOf<List<Bitmap>?>(null) }
//    var scale by remember { mutableStateOf(1f) }
//    LaunchedEffect(Unit) {
//        try {
//            withContext(Dispatchers.IO) {
//                val parcelFileDescriptor =
//                    ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
//                pdfRenderer = PdfRenderer(parcelFileDescriptor)
//
//                pdfRenderer?.let { renderer ->
//                    val bitmaps = mutableListOf<Bitmap>()
//                    for (i in 0 until renderer.pageCount) {
//                        val page = renderer.openPage(i)
//                        val bitmap = Bitmap.createBitmap(
//                            page.width * 2,
//                            page.height * 2,
//                            Bitmap.Config.ARGB_8888
//                        )
//                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//                        bitmaps.add(bitmap)
//                        page.close()
//                    }
//                    pageBitmaps = bitmaps
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
////            Toast.makeText(LocalContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        pageBitmaps?.let { bitmaps ->
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .pointerInput(Unit) {
//                        detectTransformGestures { _, pan, zoom, _ ->
//                            scale *= zoom
//                        }
//                    }
//            ) {
//                itemsIndexed(bitmaps) { index , bitmap ->
////                    val bitmap = bitmaps[index]
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(Color.White)
//                    ) {
//                        Image(
//                            bitmap = bitmap.asImageBitmap(),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Max) // Adjust height as needed
//                                .clipToBounds()
//                                .scale(scale),
//                            contentScale = ContentScale.FillWidth
//                        )
//                        Text(
//                            text = "${index + 1} / ${bitmaps.size}",
//                            modifier = Modifier.align(Alignment.CenterHorizontally),
//                            style = MaterialTheme.typography.bodySmall,
//                            color = Color.Black,
//                        )
//                        Divider(thickness = 2.dp)
//                    }
//                }
//            }
//        }
//    }
//}