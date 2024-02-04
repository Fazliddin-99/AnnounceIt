package com.example.announceit.ui.announcement_edit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.announceit.R
import com.example.announceit.data.db.models.Attachment
import com.example.announceit.util.UiEvent
import com.example.announceit.util.getFileName

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementEditScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: AnnouncementEditViewModel = hiltViewModel()
) {
    val coursesList = viewModel.courses.collectAsStateWithLifecycle(initialValue = emptyList())
    var expanded by remember { mutableStateOf(false) }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(
                AnnouncementEditEvent.OnImageChanged(it)
            )
        }
    }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                viewModel.onEvent(
                    AnnouncementEditEvent.OnAttachmentAdded(
                        it,
                        uri.getFileName(context) ?: ""
                    )
                )
            }
        }
    }

    if (coursesList.value.size == 1) {
        viewModel.onEvent(AnnouncementEditEvent.OnCourseSelected(coursesList.value.first()))
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

    Scaffold(
        floatingActionButton = {
            DoubleFloatingActionButton(onClickFirst = {
                launcher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*" // Accept any file type
                })
            }, onClickSecond = {
                viewModel.onEvent(AnnouncementEditEvent.OnSaveButtonPressed)
            }, textFirst = "Add attachment", textSecond = "Save")
        }
    ) {
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
                            IconButton(onClick = { viewModel.onEvent(AnnouncementEditEvent.OnBackPressed) }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "go back")
                            }
                        }
                        Row(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "${if (viewModel.isNewAnnouncement) "Create" else "Edit"} Announcement",
                                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    AsyncImage(
                        model = viewModel.image,
                        placeholder = painterResource(id = R.drawable.camera),
                        contentDescription = "image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentScale = ContentScale.FillHeight
                    )

                    TextField(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                        value = viewModel.title,
                        label = { Text(text = "Title") },
                        onValueChange = {
                            viewModel.onEvent(
                                AnnouncementEditEvent.OnTitleTextChanged(it)
                            )
                        })

                    TextField(modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                        value = viewModel.body,
                        label = { Text(text = "Body") },
                        maxLines = 6,
                        onValueChange = {
                            viewModel.onEvent(
                                AnnouncementEditEvent.OnBodyTextChanged(it)
                            )
                        })
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
                        expanded = !expanded
                    }) {
                        TextField(
                            value = viewModel.course?.name ?: "",
                            label = { Text(text = "Course") },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            placeholder = {
                                Text(text = "Please select the course")
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(modifier = Modifier.fillMaxWidth(),
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }) {
                            coursesList.value.forEach {
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    viewModel.onEvent(AnnouncementEditEvent.OnCourseSelected(it))
                                }, text = { Text(text = it.name) })
                            }
                        }
                    }
                }
            }
            viewModel.attachments?.let {
                items(it) { attachment ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = {
                            viewModel.onEvent(
                                AnnouncementEditEvent.DownloadFile(
                                    attachment.url, attachment.name
                                )
                            )
                        }) {
                            Text(text = attachment.name)
                        }

                        IconButton(onClick = {
                            viewModel.onEvent(AnnouncementEditEvent.OnAttachmentRemoved(attachment))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Remove attachment",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoubleFloatingActionButton(
    modifier: Modifier = Modifier,
    onClickFirst: () -> Unit,
    onClickSecond: () -> Unit,
    textFirst: String,
    textSecond: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(
            onClick = onClickFirst, modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = textFirst)
        }

        FloatingActionButton(
            onClick = onClickSecond, modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = textSecond)
        }
    }
}