package com.example.announceit.ui.course_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.util.Routes
import com.example.announceit.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseListViewModel @Inject constructor(private val repository: AnnounceItRepository) :
    ViewModel() {
    val courses = repository.getAllCourses()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: CourseListEvent) {
        when (event) {
            is CourseListEvent.OnCourseItemClick -> {
                sendUiEvent(UiEvent.Navigate("${Routes.ANNOUNCEMENTS_BY_COURSE}/${event.course.id}"))
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}