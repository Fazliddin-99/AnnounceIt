package com.example.announceit.data.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses"
)
data class Course(
    @PrimaryKey val id: String,
    val name: String,
    val teacher: String,
)
