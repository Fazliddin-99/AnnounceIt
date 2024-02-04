package com.example.announceit.ui.announcement_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.db.models.AnnouncementAggregate
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.util.Routes
import com.example.announceit.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AnnouncementDetailsViewModel @Inject constructor(
    private val repository: AnnounceItRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    val userType = repository.userType

    private val orderId = savedStateHandle.get<String>("announcementId")

    private val _announcement = MutableLiveData<AnnouncementAggregate?>(null)
    val announcement: LiveData<AnnouncementAggregate?>
        get() = _announcement

    private var _isSaved = mutableStateOf(false)
    val isSaved: State<Boolean>
        get() = _isSaved

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AnnouncementDetailsEvent) {
        when (event) {
            is AnnouncementDetailsEvent.OnAnnouncmentFavourited -> {
                saveFavourite(event.announcement)
            }

            is AnnouncementDetailsEvent.OnBackPressed -> {
                sendUiEvent(UiEvent.PopBackstack)
            }

            is AnnouncementDetailsEvent.EditButtonClick -> {
                sendUiEvent(UiEvent.Navigate("${Routes.ANNOUNCEMENT_EDIT}/${announcement.value?.announcement?.id}"))
            }

            is AnnouncementDetailsEvent.DownloadFile -> {
                repository.downloadFile(event.url, event.fileName)
            }
        }
    }

    private fun saveFavourite(announcement: Announcement) {
        _isSaved.value = !_isSaved.value

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAnnouncement(announcement.copy(isSaved = !announcement.isSaved))
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun fetchAnnouncement() {
        viewModelScope.launch(Dispatchers.IO) {
            orderId?.let {
                _announcement.postValue(repository.getAnnouncementById(it))

                withContext(Dispatchers.Main) {
                    _isSaved.value = _announcement.value?.announcement?.isSaved == true
                }
            }
        }
    }
}