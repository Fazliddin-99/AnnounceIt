package com.example.announceit.ui.announcement_edit

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.db.models.AnnouncementAggregate
import com.example.announceit.data.db.models.Attachment
import com.example.announceit.data.db.models.Course
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.util.UiEvent
import com.example.announceit.util.isLocalFile
import com.example.announceit.util.makeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AnnouncementEditViewModel @Inject constructor(
    private val repository: AnnounceItRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var image by mutableStateOf<String?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var body by mutableStateOf("")
        private set

    var course by mutableStateOf<Course?>(null)
        private set

    var attachments by mutableStateOf<List<Attachment>?>(emptyList())
        private set

    private val attachmentsAdded = mutableListOf<Attachment>()

    private val attachmentsRemoved = mutableListOf<Attachment>()

    private val announcementId = savedStateHandle.get<String>("announcementId")

    val isNewAnnouncement
        get() = announcementId == "-1"

    private val _announcement = MutableLiveData<AnnouncementAggregate?>(null)
    val announcement: LiveData<AnnouncementAggregate?>
        get() = _announcement

    val courses = repository.getCourses()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if (announcementId == "-1") {
            _announcement.value = AnnouncementAggregate(
                announcement = Announcement(
                    id = "",
                    title = "",
                    courseId = "",
                    body = "",
                    date = 0L,
                    isSaved = false
                ),
                course = null,
                attachments = null
            )
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                announcementId?.let {
                    val announcementDb = repository.getAnnouncementById(it)
                    announcementDb?.apply {
                        withContext(Dispatchers.Main) {
                            image = announcement.imageUrl
                            title = announcement.title
                            body = announcement.body
                            this@AnnouncementEditViewModel.attachments = attachments
                        }
                    }

                    _announcement.postValue(announcementDb)
                }
            }
        }
    }

    fun onEvent(event: AnnouncementEditEvent) {
        when (event) {
            is AnnouncementEditEvent.OnBackPressed -> {
                sendUiEvent(UiEvent.PopBackstack)
            }

            is AnnouncementEditEvent.OnTitleTextChanged -> {
                title = event.title
            }

            is AnnouncementEditEvent.OnBodyTextChanged -> {
                body = event.body
            }

            is AnnouncementEditEvent.OnCourseSelected -> {
                course = event.course
            }

            is AnnouncementEditEvent.OnImageChanged -> {
                image = event.imageUri.toString()
            }

            is AnnouncementEditEvent.OnAttachmentAdded -> {
                val uri = event.fileUri
                val newList = attachments?.toMutableList()
                val attachment =
                    Attachment(uri.toString(), event.fileName, "", announcementId ?: "")

                newList?.add(attachment)
                attachments = newList

                attachmentsAdded.add(attachment)
            }

            is AnnouncementEditEvent.OnAttachmentRemoved -> {
                attachments = attachments?.filter {
                    it.url != event.attachment.url
                }

                if (!event.attachment.url.toUri().isLocalFile())
                    attachmentsRemoved.add(event.attachment)
            }

            is AnnouncementEditEvent.OnSaveButtonPressed -> {
                saveTheAnnouncement()
            }

            is AnnouncementEditEvent.DownloadFile -> {
                repository.downloadFile(event.url, event.fileName)
            }

        }
    }

    private fun saveTheAnnouncement() {

        if (image.isNullOrEmpty()) {
            sendUiEvent(UiEvent.ShowSnackbar("Error while uploading the image: Image is not selected!"))
            return
        }

        if (course == null) {
            sendUiEvent(UiEvent.ShowSnackbar("Error while uploading the announcement: course is not selected!"))
            return
        }

        if (title.isEmpty()) {
            sendUiEvent(UiEvent.ShowSnackbar("Error while uploading the announcement: title is empty!"))
            return
        }
        if (body.isEmpty()) {
            sendUiEvent(UiEvent.ShowSnackbar("Error while uploading the announcement: body is empty!"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val imageUri = image?.toUri() ?: return@launch

            if (imageUri.isLocalFile())
                uploadImage(imageUri)
            else
                uploadTheAnnouncement()

            sendUiEvent(UiEvent.PopBackstack)

            if (announcementId != "-1") {
                sendUiEvent(UiEvent.PopBackstack)
            }
        }
    }

    private suspend fun uploadImage(uri: Uri) {
        repository.uploadFileToFirebase(
            uri,
            onSuccess = {
                image = it
                uploadTheAnnouncement()
            },
            onFailure = {
                sendUiEvent(UiEvent.ShowSnackbar("Error while uploading the image: ${it.message}"))
            })
    }

    private fun uploadTheAnnouncement() {
        val courseId = course?.id ?: return
        val imageUri = image ?: return

        val announcementDataFirebase = hashMapOf<String, Any>(
            "courseId" to courseId,
            "body" to body,
            "date" to Date().makeString(),
            "title" to title,
            "image" to imageUri
        )

        val attachmentsAddedFirestore: MutableList<HashMap<String, String>> = mutableListOf()

        attachmentsAdded.forEach {
            attachmentsAddedFirestore.add(hashMapOf("url" to it.url))
        }

        if (isNewAnnouncement) {
            repository.uploadAnnouncement(
                announcementDataFirebase,
                attachmentsAddedFirestore,
                attachmentsRemoved
            )
        } else {
            if (announcementId != null) {
                repository.updateAnnouncement(
                    announcementId,
                    announcementDataFirebase,
                    attachmentsAddedFirestore,
                    attachmentsRemoved
                )
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}