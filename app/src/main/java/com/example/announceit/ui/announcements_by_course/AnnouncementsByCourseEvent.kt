package com.example.announceit.ui.announcements_by_course

import com.example.announceit.data.db.models.Announcement

sealed class AnnouncementsByCourseEvent {
    data class OnAnnouncementClick(val id: String) :
        AnnouncementsByCourseEvent()

    data class OnItemFavourited(val announcement: Announcement) : AnnouncementsByCourseEvent()

    object OnBackPressed : AnnouncementsByCourseEvent()
}