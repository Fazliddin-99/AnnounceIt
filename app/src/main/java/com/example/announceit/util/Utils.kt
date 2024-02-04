package com.example.announceit.util

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getSimpleDateFormatter(): SimpleDateFormat {
    return SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
}

fun Date.makeString(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    return formatter.format(this)
}

fun String.toDate(): Date? {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    return try {
        formatter.parse(this)
    } catch (e: Exception) {
        null
    }
}

private const val ANIMATION_DURATION = 500
fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInToRight(): EnterTransition {
    return slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(ANIMATION_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInToLeft(): EnterTransition {
    return slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(ANIMATION_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutToRight(): ExitTransition {
    return slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(ANIMATION_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutToLeft(): ExitTransition {
    return slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(ANIMATION_DURATION)
    )
}

fun Uri.getFileName(context: Context): String? {
    var fileName: String? = null

    when {
        "content" == scheme -> {
            // If the scheme is a content
            val cursor = context.contentResolver.query(this, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayName =
                        it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    fileName = displayName
                }
            }
        }

        "file" == scheme -> {
            // If the scheme is a file
            fileName = lastPathSegment
        }

        else -> {
            // For other schemes
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            context.contentResolver.query(this, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    fileName = cursor.getString(columnIndex)
                }
            }
        }
    }

    return fileName
}

fun Uri.isLocalFile(): Boolean {
    return when (scheme) {
        ContentResolver.SCHEME_FILE,
        ContentResolver.SCHEME_CONTENT -> true
        else -> false
    }
}

// OpenableColumns class is used to get the DISPLAY_NAME from the cursor
private object OpenableColumns {
    const val DISPLAY_NAME = "_display_name"
}