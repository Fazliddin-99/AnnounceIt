package com.example.announceit.ui.announcements_thread

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.announceit.R
import com.example.announceit.ui.common.AnnouncementsSearchBar
import com.example.announceit.util.UiEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AnnouncementsThreadScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onBackPressed: () -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: AnnouncementsThreadViewModel = hiltViewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val announcements =
        viewModel.announcements.collectAsStateWithLifecycle(initialValue = emptyList())
    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle(initialValue = "")
    val userType = viewModel.userType.collectAsStateWithLifecycle(initialValue = 0)

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

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Do you really want to logout?") },
            text = { Text("This action cannot be undone") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.onEvent(AnnouncementsThreadEvent.Logout)
                }) {
                    Text("Yes".uppercase())
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel".uppercase())
                }
            },
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 58.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(80.dp),
                    painter = painterResource(id = R.drawable.announceit_logo),
                    contentDescription = "logo",
                    tint = MaterialTheme.colorScheme.primary
                )

                TextButton(onClick = {
                    showLogoutDialog = showLogoutDialog.not()
                }) {
                    Text(text = "Logout")
                }
            }


            AnnouncementsSearchBar(
                modifier = Modifier.padding(8.dp), searchQuery = searchQuery.value
            ) {
                viewModel.onEvent(AnnouncementsThreadEvent.OnSearchQueryChange(it))
            }

            LazyColumn {
                items(announcements.value) { announcementAggregate ->
                    AnnouncementsThreadItem(announcement = announcementAggregate,
                        navigateToDetail = {
                            viewModel.onEvent(AnnouncementsThreadEvent.OnItemClicked(it))
                        },
                        onFavouriteClicked = {
                            viewModel.onEvent(AnnouncementsThreadEvent.OnItemFavourited(it))
                        })
                }
            }
        }

        if (userType.value == 1L) {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AnnouncementsThreadEvent.CreateButtonClick) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 88.dp, end = 8.dp)
            ) {
//                Text(text = "Create")
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add announcement")
            }
        }
    }
}
