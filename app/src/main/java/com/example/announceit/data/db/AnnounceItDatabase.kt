package com.example.announceit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.announceit.data.db.models.*

@Database(
    entities = [Announcement::class, Course::class, Attachment::class], version = 5
)
abstract class AnnounceItDatabase : RoomDatabase() {
    abstract val dao: AnnounceItDao
}
