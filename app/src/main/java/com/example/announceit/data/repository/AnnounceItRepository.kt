package com.example.announceit.data.repository

import android.net.Uri
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.db.models.AnnouncementAggregate
import com.example.announceit.data.db.models.Attachment
import com.example.announceit.data.db.models.Course
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

interface AnnounceItRepository {
    val userType: Flow<Long?>

    fun getAllAnnouncements(): Flow<List<AnnouncementAggregate>>

    fun getAllCourses(): Flow<List<Course>>

    fun getSavedAnnouncements(): Flow<List<AnnouncementAggregate>>

    fun getAnnouncementsByCourse(courseId: String): Flow<List<AnnouncementAggregate>>

    suspend fun getAnnouncementById(id: String): AnnouncementAggregate?

    fun getCourse(id: String): Flow<Course?>

    fun getCourses(): Flow<List<Course>>

    suspend fun fetchAnnouncementsRemote(): Flow<Announcement?>?

    suspend fun saveUserCredentials(user: String, password: String, type: Long? = null)

    suspend fun saveAnnouncementAttachments(announcementId: String)

    suspend fun saveAnnouncement(announcement: Announcement)

    fun getAnnouncementsSearch(searchQuery: String): List<AnnouncementAggregate>

    suspend fun fetchUserType(userId: String)

    fun downloadFile(url: String, fileName: String): Long

    suspend fun clearLocalDatabase()

    suspend fun uploadFileToFirebase(
        fileUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun uploadAnnouncement(
        announcementFirestore: HashMap<String, Any>,
        attachmentsAdded: List<HashMap<String, String>>,
        attachmentsRemoved: List<Attachment>
    )

    fun updateAnnouncement(
        announcementId: String,
        announcementFirestore: HashMap<String, Any>,
        attachmentsAdded: List<HashMap<String, String>>,
        attachmentsRemoved: List<Attachment>
    )


}