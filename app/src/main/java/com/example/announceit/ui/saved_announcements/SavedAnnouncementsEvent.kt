package com.example.announceit.ui.saved_announcements

import com.example.announceit.data.db.models.Announcement

sealed class SavedAnnouncementsEvent {
    data class OnItemClick(val id: String): SavedAnnouncementsEvent()
    data class OnItemFavourited(val announcement: Announcement) : SavedAnnouncementsEvent()
}
