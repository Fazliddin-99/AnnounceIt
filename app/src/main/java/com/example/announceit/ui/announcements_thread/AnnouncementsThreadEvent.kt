package com.example.announceit.ui.announcements_thread

import com.example.announceit.data.db.models.Announcement

sealed class AnnouncementsThreadEvent {
    object Logout : AnnouncementsThreadEvent()
    data class OnSearchQueryChange(val query: String) : AnnouncementsThreadEvent()
    data class OnItemClicked(val id: String): AnnouncementsThreadEvent()
    data class OnItemFavourited(val announcement: Announcement): AnnouncementsThreadEvent()
    object CreateButtonClick: AnnouncementsThreadEvent()
}