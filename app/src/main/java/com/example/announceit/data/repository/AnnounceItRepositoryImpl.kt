package com.example.announceit.data.repository

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.example.announceit.data.FirebaseStorageManager
import com.example.announceit.data.datastore.AnnounceDataStore
import com.example.announceit.data.db.AnnounceItDatabase
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.db.models.AnnouncementAggregate
import com.example.announceit.data.db.models.Attachment
import com.example.announceit.data.db.models.Course
import com.example.announceit.util.toDate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class AnnounceItRepositoryImpl(
    private val db: AnnounceItDatabase,
    private val dataStore: AnnounceDataStore,
    private val dowloadManager: DownloadManager,
    private val firebaseStorageManager: FirebaseStorageManager
) :
    AnnounceItRepository {

    private val dao = db.dao

    private val firestore = FirebaseFirestore.getInstance()

    override val userType: Flow<Long?>
        get() = dataStore.userType

    override fun getAllAnnouncements(): Flow<List<AnnouncementAggregate>> {
        return dao.getAllAnnouncements()
    }

    override fun getAnnouncementsSearch(searchQuery: String): List<AnnouncementAggregate> {
        return dao.getAnnouncementsSearch(searchQuery)
    }

    override fun getAllCourses(): Flow<List<Course>> {
        return dao.getAllCourses()
    }

    override fun getSavedAnnouncements(): Flow<List<AnnouncementAggregate>> {
        return dao.getSavedAnnouncements()
    }

    override suspend fun fetchAnnouncementsRemote(): Flow<Announcement?>? {
        val userId = dataStore.user.first() ?: ""

        val coursesSnap =
            firestore.collection("users").document(userId).collection("courses").get().await()

        val courseList = mutableListOf<String>()

        coursesSnap?.documents?.forEach {
            if (it.data?.containsKey("course") == true) {
                val courseId = it?.data?.get("course").toString()
                courseList.add(courseId)

                val course = firestore.collection("courses").document(courseId).get().await()

                val teacherId = course.data?.get("teacherId")
                val courseName = course.data?.get("name").toString()

                teacherId?.toString()?.let { id ->
                    val teacherSnap = firestore.collection("users").document(id).get().await()

                    teacherSnap.data?.apply {

                        dao.insertCourse(
                            Course(
                                id = courseId,
                                name = courseName,
                                teacher = "${get("name").toString()} ${get("surname").toString()}"
                            )
                        )
                    }
                }
            }
        }

        if (courseList.isNotEmpty()) {
            return callbackFlow {

                val subscription = firestore.collection("announcements")
                    .whereIn("courseId", courseList)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            e.printStackTrace()
                            return@addSnapshotListener
                        }

                        snapshot?.documents?.forEach {
                            it.data?.let { announcementMap ->

                                val announcement =
                                    Announcement(
                                        id = it.id,
                                        title = announcementMap["title"].toString(),
                                        courseId = announcementMap["courseId"].toString(),
                                        body = announcementMap["body"].toString(),
                                        date = announcementMap["date"].toString()
                                            .toDate()?.time ?: 0,
                                        imageUrl = announcementMap["image"].toString()
                                    )

                                trySend(announcement).isSuccess
                            }
                        }
                    }
                awaitClose { subscription.remove() }
            }
        }

        return null
    }

    override fun getAnnouncementsByCourse(courseId: String): Flow<List<AnnouncementAggregate>> {
        return dao.getAnnouncementsByCourse(courseId)
    }

    override suspend fun saveUserCredentials(user: String, password: String, type: Long?) {
        dataStore.saveUserData(user, password)

        if (type != null)
            dataStore.saveUserType(type)
    }

    override suspend fun saveAnnouncementAttachments(announcementId: String) {
        val attSnap = firestore.collection("announcements").document(announcementId)
            .collection("attachments")
            .get()
            .await()
        attSnap.documents.forEach { attItemSnap ->
            if (attItemSnap.data?.containsKey("url") == true) {
                val url =
                    attItemSnap?.data?.get("url").toString()
                val httpsReference =
                    FirebaseStorage.getInstance()
                        .getReferenceFromUrl(url)
                val fileName = httpsReference.name

                dao.insertAttachment(
                    Attachment(
                        url = url,
                        name = fileName,
                        id = attItemSnap.id,
                        announcementId = announcementId
                    )
                )
            }
        }
    }

    override suspend fun saveAnnouncement(announcement: Announcement) {
        dao.insertAnnouncement(announcement)
    }

    override suspend fun getAnnouncementById(id: String): AnnouncementAggregate? {
        return dao.getAnnouncement(id)
    }

    override fun getCourse(id: String): Flow<Course?> {
        return dao.getCourse(id)
    }

    override fun getCourses(): Flow<List<Course>> {
        return dao.getCourses()
    }

    override suspend fun fetchUserType(userId: String) {
        val userSnap = firestore.collection("users").document(userId).get().await()
        val userType = userSnap.data?.get("type") as Long?

        userType?.let {
            dataStore.saveUserType(userType)
        }
    }

    override fun downloadFile(url: String, fileName: String): Long {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        val type = if (extension != null) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        } else {
            null
        }
        val request = DownloadManager.Request(url.toUri()).apply {
            if (type != null)
                setMimeType(type)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setTitle(fileName)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        return dowloadManager.enqueue(request)
    }

    override suspend fun clearLocalDatabase() {
        db.clearAllTables()
    }

    override suspend fun uploadFileToFirebase(
        fileUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseStorageManager.uploadFile(fileUri, onSuccess, onFailure)
    }

    override fun uploadAnnouncement(
        announcementFirestore: HashMap<String, Any>,
        attachmentsAdded: List<HashMap<String, String>>,
        attachmentsRemoved: List<Attachment>
    ) {
        firestore.collection("announcements")
            .add(announcementFirestore)
            .addOnSuccessListener { documentReference ->
                val mainDocumentId = documentReference.id

                uploadAttachments(mainDocumentId, attachmentsAdded)
                removeAttachments(mainDocumentId, attachmentsRemoved)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    override fun updateAnnouncement(
        announcementId: String,
        announcementFirestore: HashMap<String, Any>,
        attachmentsAdded: List<HashMap<String, String>>,
        attachmentsRemoved: List<Attachment>
    ) {

        firestore.collection("announcements")
            .document(announcementId)
            .update(announcementFirestore)
            .addOnSuccessListener {
                uploadAttachments(announcementId, attachmentsAdded)
                removeAttachments(announcementId, attachmentsRemoved)
            }
            .addOnFailureListener { e ->
                throw e
            }
    }

    private fun uploadAttachments(
        announcementId: String,
        attachments: List<HashMap<String, String>>
    ) {
        firebaseStorageManager.uploadAttachments(attachments) { attachment, downloadUrl ->
            attachment["url"] = downloadUrl

            firestore.collection("announcements")
                .document(announcementId)
                .collection("attachments")
                .add(attachment)
                .addOnSuccessListener {

                }
                .addOnFailureListener { e ->
                    throw e
                }
        }
    }

    private fun removeAttachments(
        announcementId: String,
        attachments: List<Attachment>
    ) {
        attachments.forEach {
            firestore.collection("announcements")
                .document(announcementId)
                .collection("attachments")
                .document(it.id)
                .delete()
                .addOnSuccessListener {

                }
                .addOnFailureListener { e ->
                    throw e
                }
        }
    }
}