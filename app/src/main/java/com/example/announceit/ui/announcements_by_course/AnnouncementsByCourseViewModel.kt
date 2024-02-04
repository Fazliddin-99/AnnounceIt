package com.example.announceit.ui.announcements_by_course

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.util.Routes
import com.example.announceit.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementsByCourseViewModel @Inject constructor(
    private val repository: AnnounceItRepository, savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val courseId = savedStateHandle.get<String>("courseId")

    val announcements = courseId?.let { repository.getAnnouncementsByCourse(it) }
    var course = courseId?.let { repository.getCourse(courseId) }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AnnouncementsByCourseEvent) {
        when (event) {
            is AnnouncementsByCourseEvent.OnAnnouncementClick -> {
                sendUiEvent(UiEvent.Navigate("${Routes.ANNOUNCEMENT_DETAILS}/${event.id}"))
            }
            is AnnouncementsByCourseEvent.OnItemFavourited -> {
                saveFavourite(event.announcement)
            }

            is AnnouncementsByCourseEvent.OnBackPressed -> {
                sendUiEvent(UiEvent.PopBackstack)
            }
        }
    }

    private fun saveFavourite(announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAnnouncement(announcement.copy(isSaved = !announcement.isSaved))
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}