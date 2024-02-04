package com.example.announceit.ui.announcement_details

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.announceit.R
import com.example.announceit.util.UiEvent
import com.example.announceit.util.makeString
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDetailsScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: AnnouncementDetailsViewModel = hiltViewModel()
) {
    val announcement = viewModel.announcement.observeAsState()
    val userType = viewModel.userType.collectAsStateWithLifecycle(initialValue = 0)

    LaunchedEffect(key1 = true) {
        viewModel.fetchAnnouncement()
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


    Scaffold(floatingActionButton = {
        if (userType.value == 1L) {
            FloatingActionButton(onClick = { viewModel.onEvent(AnnouncementDetailsEvent.EditButtonClick) }) {
                Text(text = "Edit")
            }
        }
    }) {
        LazyColumn {
            item {

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
                            IconButton(onClick = { viewModel.onEvent(AnnouncementDetailsEvent.OnBackPressed) }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "go back")
                            }
                        }
                        Row(modifier = Modifier.padding(top = 10.dp)) {
                            val courseName = announcement.value?.course?.name ?: ""
                            val courseTeacher = announcement.value?.course?.teacher ?: ""

                            Text(
                                text = "$courseName - $courseTeacher",
                                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    AsyncImage(
                        model = announcement.value?.announcement?.imageUrl,
                        placeholder = painterResource(id = R.drawable.camera),
                        contentDescription = "image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.FillHeight
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = announcement.value?.announcement?.title ?: "",
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 24.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val date =
                                if (announcement.value == null) "" else announcement.value?.let {
                                    Date(
                                        it.announcement.date
                                    ).makeString()
                                }
                            Text(
                                text = date ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                announcement.value?.announcement?.let {
                                    viewModel.onEvent(
                                        AnnouncementDetailsEvent.OnAnnouncmentFavourited(
                                            it
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                tint = if (viewModel.isSaved.value)
                                    Color.Yellow else LocalContentColor.current,
                                contentDescription = "Favourite"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = announcement.value?.announcement?.body ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }

            announcement.value?.attachments?.let {
                items(it) { attachment ->
                    TextButton(onClick = {
                        viewModel.onEvent(
                            AnnouncementDetailsEvent.DownloadFile(
                                attachment.url,
                                attachment.name
                            )
                        )
                    }) {
                        Text(text = attachment.name)
                    }
                }
            }
        }
    }
}