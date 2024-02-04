package com.example.announceit.ui.saved_announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.ui.course_list.CourseListEvent
import com.example.announceit.util.Routes
import com.example.announceit.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedAnnouncementsViewModel @Inject constructor(private val repository: AnnounceItRepository) :
    ViewModel() {
    val announcements = repository.getSavedAnnouncements()

    private fun saveFavourite(announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAnnouncement(announcement.copy(isSaved = !announcement.isSaved))
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SavedAnnouncementsEvent) {
        when (event) {
            is SavedAnnouncementsEvent.OnItemClick -> {
                sendUiEvent(UiEvent.Navigate("${Routes.ANNOUNCEMENT_DETAILS}/${event.id}"))
            }
            is SavedAnnouncementsEvent.OnItemFavourited -> {
                saveFavourite(event.announcement)
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}