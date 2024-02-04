package com.example.announceit.ui.announcements_by_course

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.announceit.data.db.models.Course
import com.example.announceit.ui.announcements_thread.AnnouncementsThreadItem
import com.example.announceit.util.UiEvent

@Composable
fun AnnouncementsByCourse(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: AnnouncementsByCourseViewModel = hiltViewModel()
) {
    val announcements = viewModel.announcements?.collectAsStateWithLifecycle(initialValue = emptyList())
    val course = viewModel.course?.collectAsStateWithLifecycle(initialValue = Course("", "", ""))

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }

                is UiEvent.PopBackstack -> {
                    onPopBackStack()
                }

                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Row {
                IconButton(onClick = { viewModel.onEvent(AnnouncementsByCourseEvent.OnBackPressed) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "go back")
                }
            }
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = course?.value?.name ?: "",
                    fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

            }
        }

        announcements?.value?.let {
            LazyColumn {
                items(it) { announcementAggregate ->
                    AnnouncementsThreadItem(announcement = announcementAggregate,
                        navigateToDetail = {
                            viewModel.onEvent(AnnouncementsByCourseEvent.OnAnnouncementClick(it))
                        },
                        onFavouriteClicked = {
                            viewModel.onEvent(AnnouncementsByCourseEvent.OnItemFavourited(it))
                        })
                }
            }
        }
    }
}