package com.example.announceit.data.db.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "attachments",
    primaryKeys = ["url", "announcementId"],
    foreignKeys = [ForeignKey(
        Announcement::class,
        parentColumns = ["id"],
        childColumns = ["announcementId"]
    )],
    indices = [Index("announcementId")]
)
data class Attachment(
    val url: String,
    val name: String,
    val id: String,
    val announcementId: String
)
