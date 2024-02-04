package com.example.announceit.ui.announcement_edit

import android.net.Uri
import com.example.announceit.data.db.models.Attachment
import com.example.announceit.data.db.models.Course

sealed class AnnouncementEditEvent {
    object OnBackPressed : AnnouncementEditEvent()
    data class OnTitleTextChanged(val title: String) : AnnouncementEditEvent()
    data class OnBodyTextChanged(val body: String) : AnnouncementEditEvent()
    data class OnImageChanged(val imageUri: Uri) : AnnouncementEditEvent()
    data class OnCourseSelected(val course: Course) : AnnouncementEditEvent()
    data class OnAttachmentAdded(val fileUri: Uri, val fileName: String) : AnnouncementEditEvent()
    data class OnAttachmentRemoved(val attachment: Attachment) : AnnouncementEditEvent()
    object OnSaveButtonPressed : AnnouncementEditEvent()
    data class DownloadFile(val url: String, val fileName: String) : AnnouncementEditEvent()

}
