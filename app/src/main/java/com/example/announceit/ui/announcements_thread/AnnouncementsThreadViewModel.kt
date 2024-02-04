package com.example.announceit.ui.announcements_thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.util.Routes
import com.example.announceit.util.UiEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AnnouncementsThreadViewModel @Inject constructor(private val repository: AnnounceItRepository) :
    ViewModel() {

    val userType = repository.userType

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _announcements = repository.getAllAnnouncements()
    val announcements = _searchQuery.combine(_announcements) { text, announcements ->
        if (text.isBlank())
            announcements
        else
            announcements.filter {
                it.announcement.title.lowercase().contains(text)
                        || it.announcement.body.lowercase().contains(text)
                        || it.course?.teacher?.lowercase()?.contains(text) ?: false
                        || it.course?.name?.lowercase()?.contains(text) ?: false
            }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAnnouncementsRemote()?.collect {
                if (it != null) {
                    withContext(Dispatchers.IO) {
                        val announcementDb = repository.getAnnouncementById(it.id)
                        val announcementUpdate =
                            announcementDb?.announcement?.copy(
                                title = it.title,
                                date = it.date,
                                body = it.body,
                                imageUrl = it.imageUrl
                            ) ?: it

                        repository.saveAnnouncement(announcementUpdate)
                        repository.saveAnnouncementAttachments(it.id)
                    }
                }
            }
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val auth = FirebaseAuth.getInstance()

    fun onEvent(event: AnnouncementsThreadEvent) {
        when (event) {
            is AnnouncementsThreadEvent.Logout -> {
                userLogout()
                sendUiEvent(UiEvent.PopBackstack)
            }

            is AnnouncementsThreadEvent.OnItemClicked -> {
                sendUiEvent(UiEvent.Navigate("${Routes.ANNOUNCEMENT_DETAILS}/${event.id}"))
            }

            is AnnouncementsThreadEvent.OnSearchQueryChange -> {
                _searchQuery.value = event.query
            }

            is AnnouncementsThreadEvent.OnItemFavourited -> {
                saveFavourite(event.announcement)
            }

            is AnnouncementsThreadEvent.CreateButtonClick -> {
                sendUiEvent(UiEvent.Navigate("${Routes.ANNOUNCEMENT_EDIT}/-1"))
            }
        }
    }

    private fun userLogout() {
        auth.signOut()
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUserCredentials("", "")
            repository.clearLocalDatabase()
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