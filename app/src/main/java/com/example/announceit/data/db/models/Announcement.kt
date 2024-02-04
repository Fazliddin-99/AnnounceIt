package com.example.announceit.data.db.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "announcements",
    indices = [Index("autogeneratedId"), Index("id", unique = true)]
)

data class Announcement(
    @PrimaryKey(autoGenerate = true)
    val autogeneratedId: Long = 0L,
    val id: String,
    val title: String,
    val courseId: String,
    val body: String,
    val date: Long,
    val isSaved: Boolean = false,
    val imageUrl: String? = null
)