package com.example.announceit.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.announceit.util.getFileName
import com.google.firebase.FirebaseException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class FirebaseStorageManager(private val context: Context) {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    fun uploadFile(
        fileUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Create a reference to the file you want to upload
        val fileReference = storageReference.child(
            fileUri.getFileName(context) ?: throw FirebaseException("Could not upload the image!")
        )

        // Upload the file
        fileReference.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                fileReference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        onSuccess(downloadUrl)
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun uploadAttachments(
        attachments: List<HashMap<String, String>>,
        onSuccess: (HashMap<String, String>, String) -> Unit
    ) {
        attachments.forEach {
            it["url"]?.let { url ->
                uploadFile(
                    fileUri = url.toUri(),
                    onSuccess = { downloadUrl ->
                        onSuccess(it, downloadUrl)
                    },
                    onFailure = { e -> throw e })
            }
        }
    }
}