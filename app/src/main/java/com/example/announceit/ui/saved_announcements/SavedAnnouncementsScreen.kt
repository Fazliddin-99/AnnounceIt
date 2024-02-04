package com.example.announceit.ui.saved_announcements

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.announceit.ui.announcements_thread.AnnouncementsThreadItem
import com.example.announceit.util.UiEvent

@Composable
fun SavedAnnouncementsScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onBackPressed: () -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: SavedAnnouncementsViewModel = hiltViewModel()
) {
    BackHandler {
        onBackPressed()
    }
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

    val announcements = viewModel.announcements.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 58.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saved Announcements",
                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn {
            items(announcements.value) { announcementAggregate ->
                AnnouncementsThreadItem(
                    announcement = announcementAggregate,
                    navigateToDetail = {
                        viewModel.onEvent(SavedAnnouncementsEvent.OnItemClick(it))
                    },
                    onFavouriteClicked = {
                        viewModel.onEvent(SavedAnnouncementsEvent.OnItemFavourited(it))
                    })
            }
        }
    }
}