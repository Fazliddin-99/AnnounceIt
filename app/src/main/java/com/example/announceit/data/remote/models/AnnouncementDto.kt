package com.example.announceit.data.remote.models

import com.example.announceit.util.getSimpleDateFormatter
import java.util.Date

data class AnnouncementDto(
    val id: String,
    val title: String,
    val course: CourseDto,
    val teacher: User,
    val body: String,
    val isFavourited: Boolean = false,
    val attachmentUrls: List<String> = listOf(),
    val imageUrl: String = "",
    val date: String = getSimpleDateFormatter().format(Date()),
    val isEdited: Boolean = false
)
