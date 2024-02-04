package com.example.announceit.ui.announcement_details

import com.example.announceit.data.db.models.Announcement

sealed class AnnouncementDetailsEvent {
    data class DownloadFile(val url: String, val fileName: String) : AnnouncementDetailsEvent()
    object OnBackPressed : AnnouncementDetailsEvent()
    object EditButtonClick : AnnouncementDetailsEvent()
    data class OnAnnouncmentFavourited(val announcement: Announcement) : AnnouncementDetailsEvent()

}
