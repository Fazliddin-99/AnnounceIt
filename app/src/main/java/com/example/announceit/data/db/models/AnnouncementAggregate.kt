package com.example.announceit.data.db.models

import androidx.room.Embedded
import androidx.room.Relation

data class AnnouncementAggregate(
    @Embedded
    val announcement: Announcement,

    @Relation(
        parentColumn = "courseId",
        entityColumn = "id"
    )
    val course: Course?,

    @Relation(
        parentColumn = "id",
        entityColumn = "announcementId"
    )
    val attachments: List<Attachment>?,
)
