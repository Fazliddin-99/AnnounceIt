package com.example.announceit.ui.course_list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.announceit.ui.theme.seed
import com.example.announceit.util.UiEvent

@Composable
fun CourseListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onBackPressed: () -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: CourseListViewModel = hiltViewModel()
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


    val courses = viewModel.courses.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(modifier = Modifier.padding(bottom = 58.dp).fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your Courses",
                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        LazyColumn {
            items(courses.value) {
                Card(modifier = Modifier
                    .padding(vertical = 1.dp)
                    .clickable { viewModel.onEvent(CourseListEvent.OnCourseItemClick(it)) }
                    .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        Text(
                            text = it.name,
                            fontStyle = MaterialTheme.typography.headlineSmall.fontStyle
                        )
                    }
                }
            }
        }
    }
}